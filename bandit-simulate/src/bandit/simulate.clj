(ns ^{:doc "Some functions to help test the algorithms using a monte carlo simulation."
      :author "Paul Ingles"}
  bandit.simulate
  (:use [clojure.data.csv :only (write-csv)]
        [clojure.java.io :only (writer)]
        [clojure.string :only (join)]
        [clojure.java.io :only (writer)]
        [bandit.arms :only (update pulled reward)]
        [clojure.tools.cli :only (cli)]
        [incanter.stats :only (mean)])
  (:require [bandit.algo.exp3 :as exp3]
            [bandit.algo.bayes :as bayes]
            [bandit.algo.ucb :as ucb]
            [bandit.algo.softmax :as softmax])
  (:gen-class))

(defn bernoulli-arm
  "creates a function representing the bandit arm. uses a fixed
   probability p of reward. p of 0.1 would reward ~10% of the time."
  [p]
  (fn []
    (if (> (rand) p)
      0
      1)))

(defn draw-arm [f] (f))

(defn mk-bernoulli-bandit
  "creates the simulation bandit: a map of labels to their arms (functions)"
  [& p]
  (->> p
       (partition 2)
       (map (fn [[label pct]] {label (bernoulli-arm pct)}))
       (reduce merge)))





(defn simulate
  "runs a simulation. bandit is a sequence of arms (functions) that
   return their numerical reward value.
   bandit: the multi-armed machine we're optimising against
   selectfn: the algorithm function to select the arm. (f arms)
   arms: current algorithm state"
  [bandit selectfn rewardfn {:keys [arms result]}]
  (let [gamma 0.1
        pull (selectfn (vals arms))
        selected-label (:name pull)
        arm (get bandit selected-label)
        rwd (draw-arm arm)
        {:keys [cumulative-reward t]} result]
    {:arms (update (-> pull (rewardfn rwd) (pulled)) arms)
     :result {:pulled selected-label
              :reward rwd
              :t (inc t)
              :cumulative-reward (+ cumulative-reward rwd)}}))

(defn simulation-seq
  "returns an unbounded sequence with results and arms for a test run.
   the number of items taken represents the horizon (or t) value.

   example: to run the algorithm against the bandit to horizon 20:

   (take 20 (simulation-seq bandit (partial eps/select-arm epsilon) arms))"
  [bandit selectfn rewardfn arms]
  (rest (iterate (partial simulate bandit selectfn rewardfn)
                 {:arms arms
                  :result {:t 0
                           :cumulative-reward 0}})))

(defn- summary-row
  "Results is the set of results at time t across all simulations."
  [results]
  (let [t (:t (first results))]
    [t (float (mean (map :cumulative-reward results)))]))

(defn transpose
  [coll]
  (partition (count coll)
             (apply interleave coll)))

(defn -main
  [& args]
  (let [[options args banner] (cli args
                                   ["-a" "--algorithm" "Algorithm to use." :default "bayes"]
                                   ["-e" "--epsilon" "Value to control algorithm's tendency to explore." :default 0.3]
                                   ["-o" "--output" "File path to write results to" :default "results.csv"]
                                   ["-n" "--num-simulations" "Number of simulations to execute" :default 10]
                                   ["-t" "--time" "Time: number of iterations within simulation" :default 1000]
                                   ["-h" "--help" "Display this"])]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (let [{:keys [output num-simulations time algorithm epsilon]} options
          n-sims      (Integer/valueOf num-simulations)
          horizon     (Integer/valueOf time)
          bandit      (mk-bernoulli-bandit :arm1 0.1 :arm2 0.1 :arm3 0.1 :arm4 0.1 :arm5 0.9)
          epsilon     (Double/valueOf epsilon)
          gamma       epsilon
          arms        (exp3/mk-arms :arm1 :arm2 :arm3 :arm4 :arm5)
          algos       {:bayes   {:select bayes/select-arm
                                 :reward bayes/reward}
                       :exp3    {:select (partial exp3/select-arm gamma)
                                 :reward (partial exp3/reward gamma 5)}
                       :ucb     {:select ucb/select-arm
                                 :reward reward}
                       :softmax {:select (partial softmax/select-arm epsilon)
                                 :reward reward}}
          algo        (keyword algorithm)]
      (println "Starting simulations ...")
      (with-open [out-csv (writer output)]
        (write-csv out-csv
                   (->> (repeatedly n-sims (fn []
                                             (map :result (simulation-seq bandit
                                                                          (-> algos algo :select)
                                                                          (-> algos algo :reward)
                                                                          arms))))
                        (transpose)
                        (map summary-row)
                        (take horizon))))
      (println "Completed simulations. Results in" output))))

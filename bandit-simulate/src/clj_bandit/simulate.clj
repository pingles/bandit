(ns ^{:doc "Some functions to help test the algorithms using a monte carlo simulation."
      :author "Paul Ingles"}
  clj-bandit.simulate
  (:use [clojure.data.csv :only (write-csv)]
        [clojure.java.io :only (writer)]
        [clojure.string :only (join)]
        [clojure.java.io :only (writer)]
        [clj-bandit.arms :only (mk-arm fold-arm reward)]
        [clojure.tools.cli :only (cli)])
  (:require [clj-bandit.algo.epsilon :as e])
  (:gen-class))

(set! *warn-on-reflection* true)

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
   selectfn: the algorithm function to select the arm. (f arms)
   arms: the initial arms map"
  [bandit selectfn {:keys [arms result]}]
  (let [pull (selectfn arms)
        selected-label (:name pull)
        arm (get bandit selected-label)
        rwd (draw-arm arm)
        {:keys [cumulative-reward t]} result]
    {:arms (fold-arm (reward pull rwd) arms)
     :result {:pulled selected-label
               :reward rwd
               :t (inc t)
               :cumulative-reward (+ cumulative-reward rwd)}}))

(defn simulation-seq
  "returns an unbounded sequence with results and arms for a test run.
   the number of items taken represents the horizon (or t) value.

   example: to run the algorithm against the bandit to horizon 20:

   (take 20 (simulation-seq bandit (partial e/select-arm epsilon) arms))"
  [bandit selectfn arms]
  (drop 1 (iterate (partial simulate bandit selectfn)
                   {:arms arms
                    :result {:t 0
                             :cumulative-reward 0}})))


(defn- csv-row
  [label {:keys [t pulled reward cumulative-reward]}]
  (concat label [t pulled reward cumulative-reward]))

(defn simulations
  [simulations]
  (let [bandit (mk-bernoulli-bandit :arm1 0.1 :arm2 0.1 :arm3 0.1 :arm4 0.1 :arm5 0.9)
        arms (map mk-arm [:arm1 :arm2 :arm3 :arm4 :arm5])
        epsilon 0.1
        horizon 1000]
    (letfn [(simulationfn [algo-label algorithm]
              (->> arms
                   (simulation-seq bandit algorithm)
                   (map :result)
                   (map (partial csv-row algo-label))
                   (take horizon)))]
      (apply concat
             (repeatedly simulations
                         #(simulationfn [:epsilon-greedy epsilon] (partial e/select-arm epsilon)))))))

(defn csv-simulate
  [file-path num-simulations]
  (with-open [out-csv (writer file-path)]
    (write-csv out-csv (simulations num-simulations))))

(defn -main
  [& args]
  (let [[options args banner] (cli args
                                   ["-o" "--output" "File path to write CSV results data to" :default "results.csv"]
                                   ["-n" "--simulations" "Number of monte-carlo simulations to execute" :default 10]
                                   ["-h" "--help" "Display this"])]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (let [{:keys [output simulations]} options]
      (println "Starting simulations ...")
      (csv-simulate output simulations)
      (println "Completed simulations. Results in" output))))

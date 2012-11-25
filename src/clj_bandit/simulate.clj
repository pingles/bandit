(ns clj-bandit.simulate
  (:import [java.util UUID])
  (:use [clojure.string :only (join)]
        [clojure.java.io :only (writer)]
        [clj-bandit.arms :only (mk-arm fold-arm reward)])
  (:require [clj-bandit.algo.epsilon :as e]))

(set! *warn-on-reflection* true)

(defn bernoulli-arm [p] (fn [] (if (> (rand) p) 0 1)))

(defn draw-arm [f] (f))

(defn mk-bernoulli-bandit
  "creates the simulation bandit: a map of labels to their arms (functions)"
  [& p]
  (reduce merge
          (map (fn [[label pct]] {label (bernoulli-arm pct)})
               (partition 2 p))))


(defn simulate
  "runs a simulation. bandit is a sequence of arms (functions) that
   return their numerical reward value.
   selectfn: the algorithm function to select the arm. (f arms)
   arms: the initial arms map"
  [bandit selectfn {:keys [arms results]}]
  (let [pull (selectfn arms)
        selected-label (:name pull)
        arm (get bandit selected-label)
        rwd (draw-arm arm)
        {:keys [cumulative-reward t]} results]
    {:arms (fold-arm (reward pull rwd) arms)
     :results {:pulled selected-label
               :reward rwd
               :t (inc t)
               :cumulative-reward (+ cumulative-reward rwd)}}))

(defn simulation-seq
  "returns an unbounded sequence with results and arms for a test run.
   the number of items taken represents the horizon (or t) value.

   example. run the algorithm against the bandit to horizon/t 20:

   (take 20 (simulation-seq bandit (partial e/select-arm epsilon) arms))"
  [bandit selectfn arms]
  (drop 1 (iterate (partial simulate bandit selectfn)
                   {:arms arms
                    :results {:t 0
                              :cumulative-reward 0}})))

(comment
  (def bandit (mk-bernoulli-bandit :arm1 0.1
                                   :arm2 0.1
                                   :arm3 0.1
                                   :arm4 0.1
                                   :arm5 0.9))
  (def epsilon 0.1)
  (def arms (map mk-arm [:arm1 :arm2 :arm3 :arm4 :arm5]))
  (simulate bandit epsilon arms)
  (def some-results (take 20 (simulation-seq bandit (partial e/select-arm epsilon) arms))))
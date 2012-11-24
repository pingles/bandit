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
   arms: the initial arms map"
  [bandit epsilon arms]
  (let [pull (e/select-arm epsilon arms)
        selected-label (:name pull)
        arm (get bandit selected-label)
        rwd (draw-arm arm)]
    (println "Pulling" selected-label "for reward" rwd)
    (fold-arm (reward pull rwd) arms)))

(defn simulation-seq
  [bandit epsilon arms]
  (iterate (partial simulate bandit epsilon) arms))

(comment
  (def bandit (mk-bernoulli-bandit :arm1 0.1
                                   :arm2 0.1
                                   :arm3 0.1
                                   :arm4 0.1
                                   :arm5 0.9))
  (def epsilon 0.1)
  (def arms (map mk-arm [:arm1 :arm2 :arm3 :arm4 :arm5]))
  (simulate bandit epsilon arms)
  (def some-results (take 5 (simulation-seq bandit epsilon arms))))
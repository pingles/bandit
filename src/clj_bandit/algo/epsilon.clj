(ns clj-bandit.algo.epsilon
  (:use [clj-bandit.core :only (BanditAlgorithm best-performing update-arms)]
        [clj-bandit.storage :only (get-arms put-arms)]))

(defn select-arm
  [epsilon arms]
  (if (> (rand) epsilon)
    (best-performing :value arms)
    (apply hash-map (rand-nth (seq arms)))))

(defn epsilon-greedy-algorithm
  "Returns an Epsilon-Greedy bandit with the specified lever labels. uses atom storage"
  [epsilon storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (select-arm epsilon (get-arms storage)))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

(ns clj-bandit.algo.epsilon
  (:use [clj-bandit.core :only (BanditAlgorithm best-performing weighted-arm-value)]
        [clj-bandit.storage :only (get-arms put-arms)]))

(defn update-arms
  [reward arm arms]
  (update-in arms [arm] (partial weighted-arm-value reward)))

(defn epsilon-greedy-algorithm
  "Returns an Epsilon-Greedy bandit with the specified lever labels. uses atom storage"
  [epsilon storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (if (> (rand) epsilon)
        (best-performing :value (get-arms storage))
        (apply hash-map (rand-nth (seq (get-arms storage))))))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

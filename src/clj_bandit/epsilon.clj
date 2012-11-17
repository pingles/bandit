(ns clj-bandit.epsilon
  (:use [clj-bandit.core :only (BanditAlgorithm best-performing update-arms)]
        [clj-bandit.storage :only (get-arms put-arms)]))

(defn epsilon-greedy-algorithm
  "Returns an Epsilon-Greedy bandit with the specified lever labels. uses atom storage"
  [epsilon storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (if (> (rand) epsilon)
        {:strategy :exploit
         :arm (best-performing (get-arms storage))}
        {:strategy :explore
         :arm (apply hash-map (rand-nth (seq (get-arms storage))))}))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

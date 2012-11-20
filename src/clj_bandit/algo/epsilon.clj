(ns clj-bandit.algo.epsilon
  (:use [clj-bandit.core :only (BanditAlgorithm best-performing update-arms total-pulls)]
        [clj-bandit.storage :only (get-arms put-arms)]))

(defmulti draw-arm (fn [x _] (number? x)))

(defmethod draw-arm true
  [epsilon arms]
  (if (> (rand) epsilon)
    (best-performing :value arms)
    (apply hash-map (rand-nth (seq arms)))))

(defmethod draw-arm false
  [anneal arms]
  (draw-arm (anneal (total-pulls arms)) arms))

(defn epsilon-greedy-algorithm
  "epsilon can either be a constant factor, or a function that will be
   applied to the current number of pulls. use with anneal to cause
   algorithm to favour exploitation over time."
  [epsilon storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (draw-arm epsilon (get-arms storage)))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

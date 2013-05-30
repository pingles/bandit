(ns ^{:doc "Epsilon-Greedy algorithm"
      :author "Paul Ingles"}
  clj-bandit.algo.epsilon
  (:use [clj-bandit.arms :only (best-performing total-pulls)]))

(defn- draw-arm
  ([epsilon arms]
     (draw-arm epsilon (rand) arms))
  ([epsilon n arms]
     (if (> n epsilon)
       (best-performing :value arms)
       (rand-nth (seq arms)))))

(defmulti select-arm
  "returns the arm that should be pulled. can provide an epsilon value: a number
   indicating the algorithms propensity for exploration. alternatively, provide
   an annealing function that will be called with the number of pulls; allowing the
   algorithm to dampen its exploration over time. 0 < epsilon < 1."
  (fn [epsilon arms] (number? epsilon)))

(defmethod select-arm true
  [epsilon arms]
  (draw-arm epsilon arms))

(defmethod select-arm false
  [annealfn arms]
  (draw-arm (annealfn (total-pulls arms)) arms))

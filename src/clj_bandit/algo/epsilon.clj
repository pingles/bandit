(ns clj-bandit.algo.epsilon
  (:use [clj-bandit.core :only (BanditAlgorithm best-performing update-arms total-pulls)]
        [clj-bandit.storage :only (get-arms put-arms)]))

(defn draw-arm
  [epsilon arms]
  (if (> (rand) epsilon)
    (best-performing :value arms)
    (apply hash-map (rand-nth (seq arms)))))

(defn draw-anneal-arm
  [anneal arms]
  (draw-arm (anneal (total-pulls arms)) arms))

(defmulti epsilon-greedy-algorithm
  "epsilon can either be a constant factor, or a function that will be
   applied to the current number of pulls. use with anneal to cause
   algorithm to favour exploitation over time."
  (fn [x _] (number? x)))

(defn- mk-algorithm
  [storage selectfn]
  (reify BanditAlgorithm
    (select-arm [_]
      (selectfn (get-arms storage)))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

(defmethod epsilon-greedy-algorithm true
  [epsilon storage]
  (mk-algorithm storage (partial draw-arm epsilon)))

(defmethod epsilon-greedy-algorithm false
  [annealfn storage]
  (mk-algorithm storage (partial draw-anneal-arm annealfn)))



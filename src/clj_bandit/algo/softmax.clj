(ns clj-bandit.algo.softmax
  (:use [clj-bandit.arms :only (unpulled)]))

(defn cumulative-sum
  [coll]
  (reduce (fn [v, x] (conj v (+ (last v) x)))
          [(first coll)]
          (rest coll)))

(defn z
  [temperature values]
  (reduce + (map (fn [x] (Math/exp (/ x temperature)))
                 values)))

(defn p
  [temperature z value]
  (/ (Math/exp (/ value temperature))
     z))

(defn probability
  [temperature {:keys [value] :as arm} arms]
  (let [values (map :value arms)
        zval (z temperature values)]
    (p temperature zval value)))

(defn probabilities
  "adds softmax p values to arms"
  [temperature arms]
  (let [probs (map #(probability temperature % arms) arms)
        arms (map #(assoc %1 :p %2) arms probs)]
    (map #(assoc %1 :cumulative-p %2) arms (cumulative-sum probs))))

(defn select-draw
  ([temperature arms]
     (select-draw (rand) arms))
  ([temperature rand-val arms]
     (or (first (unpulled arms))
         (first (filter (fn [{:keys [cumulative-p]}]
                          (> cumulative-p rand-val))
                        (probabilities temperature arms)))
         (last arms))))

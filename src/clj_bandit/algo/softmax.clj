(ns clj-bandit.algo.softmax
  (:use [clj-bandit.core :only (cumulative-sum)]))

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
        arms (map (fn [arm p] (assoc arm :p p)) arms probs)]
    (map #(assoc %1 :cumulative-p %2)
         arms
         (cumulative-sum probs))))

(defn select-draw
  ([temperature arms]
     (select-draw (rand) arms))
  ([temperature rand-val arms]
     (or (first (filter #(zero? (:pulls %)) arms))
         (first (filter (fn [{:keys [cumulative-p]}]
                          (> cumulative-p rand-val))
                        (probabilities temperature arms)))
         (last arms))))

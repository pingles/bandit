(ns clj-bandit.algo.ucb
  (:use [clj-bandit.core :only (BanditAlgorithm individual-maps best-performing update-arms total-pulls)]
        [clj-bandit.storage :only (get-arms put-arms)]
        [clojure.math.numeric-tower :only (sqrt)]))

(defn unused-arms
  [arms]
  (map (partial apply hash-map) (filter (fn [[_ {:keys [n]}]] (zero? n)) arms)))

(def first-unused-arm (comp first unused-arms))

(defn bonus-value
  [total-pulls arm-pulls]
  (sqrt (/ (* 2 (Math/log total-pulls))
           arm-pulls)))

(defn arm-ucb-value
  [total-pulls arm]
  (let [k (first (keys arm))
        v (first (vals arm))]
    {k (assoc v :ucb-value (+ (:value v)
                              (bonus-value total-pulls (:n v))))}))

(defn ucb-value
  "adds ucb-value to each arm"
  [arms]
  (apply conj (map (partial arm-ucb-value (total-pulls arms)) (individual-maps arms))))

(defn pick-arm
  [arms]
  (or (first-unused-arm arms)
      (best-performing :ucb-value (ucb-value arms))))

(defn ucb-algorithm
  [storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (pick-arm (get-arms storage)))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

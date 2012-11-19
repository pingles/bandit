(ns clj-bandit.ucb
  (:use [clj-bandit.core :only (BanditAlgorithm individual-maps)]
        [clj-bandit.storage :only (get-arms put-arms)]
        [clojure.math.numeric-tower :only (sqrt)]))

(defn mk-arms
  [labels]
  (letfn [(arm-map [label] {label {:n 0 :reward 0 :value 0}})]
    (apply merge (map arm-map labels))))

(defn unused-arm
  "picks an unused arm. returns nil if there aren't any"
  [arms]
  (if-let [arm (first (filter (fn [[_ {:keys [n]}]] (zero? n))
                              arms))]
    (apply hash-map arm)))

(defn total-pulls
  [arms]
  (reduce + (map (fn [[_ {:keys [n]}]]
                   n)
                 arms)))

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

;; TODO
;; this can be removed if the value is stored in 'value', the same fn
;; already exists in core
(defn best-performing
  [arms]
  (letfn [(performance [arm]
            (:ucb-value (->> arm vals first)))]
    (apply max-key performance (individual-maps arms))))

;; TODO
;; this is the weighted calc as in epsilon
(defn calc-value
  [n value reward]
  (+ (* (/ (dec n) n)
        value)
     (* (/ 1 n)
        reward)))

;; TODO
;; this is almost the exact same as in epsilon, extract
(defn arm-value
  [latest-reward {:keys [n reward value] :as arm}]
  (let [updated {:n (inc n)
                 :reward (+ reward latest-reward)}]
    (if (zero? n)
      (assoc updated :value latest-reward)
      (assoc updated :value (calc-value n value latest-reward)))))

(defn update-arms
  [reward arm arms]
  (update-in arms [arm] #(arm-value reward %)))

(defn pick-arm
  [arms]
  (or (unused-arm arms)
      (best-performing (ucb-value arms))))

(defn ucb-algorithm
  [storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (pick-arm (get-arms storage)))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

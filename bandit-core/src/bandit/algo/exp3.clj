(ns bandit.algo.exp3
  (:require [bandit.arms :as arms])
  (:use [incanter.core :only (cumulative-sum)]))

(defn mk-arm
  [name & kvs]
  (apply arms/mk-arm name :weight 1 kvs))

(defn mk-arms
  [& names]
  (apply sorted-map (interleave names (map mk-arm names))))

(defn arm-probability
  [gamma total-weight number-of-arms {:keys [weight] :as arm}]
  (+ (* (- 1 gamma)
        (/ weight total-weight))
     (* gamma (/ 1 number-of-arms))))

(defn total-weight
  [arms]
  (reduce + (map :weight arms)))

(defn cumulative-probabilities
  [arms]
  (map #(assoc %1 :cumulative-p %2) arms (cumulative-sum (map :p arms))))

(defn categorical-draw
  ([arms] (categorical-draw arms (rand)))
  ([arms z]
     (first (filter (fn [{:keys [cumulative-p]}]
                      (> cumulative-p z))
                    (cumulative-probabilities arms)))))

(defn arm-probabilities
  [gamma arms]
  (let [arm-prob (partial arm-probability
                          gamma
                          (total-weight arms)
                          (count arms))]
    (map #(assoc % :p (arm-prob %)) arms)))

(defn select-arm
  [gamma arms]
  (let [arms (arm-probabilities gamma arms)]
    (or (categorical-draw arms)
        (last arms))))

;; tracking rewards

(defn reward
  [gamma number-of-arms {:keys [weight p] :as arm} reward]
  (let [x (/ reward p)
        growth (Math/exp (* x (/ gamma number-of-arms)))]
    (assoc arm :weight (* weight growth))))

(ns clj-bandit.algo.ucb
  (:use [clj-bandit.storage :only (get-arms put-arms)]
        [clj-bandit.bandit :only (best-performing)]
        [clojure.math.numeric-tower :only (sqrt)]))

(defn unused-arms
  [arms]
  (filter (fn [{:keys [pulls]}] (zero? pulls)) arms))

(def first-unused-arm (comp first unused-arms))

(defn total-pulls
  [arms]
  (reduce + (map :pulls arms)))

(defn bonus-value
  [total-pulls arm-pulls]
  (sqrt (/ (* 2 (Math/log total-pulls))
           arm-pulls)))

(defn ucb-value
  "adds ucb-value to arm"
  [{:keys [value pulls] :as arm} arms]
  (assoc arm :ucb-value (+ value
                           (bonus-value (total-pulls arms)
                                        pulls))))

(defn select-arm
  [arms]
  (or (first-unused-arm arms)
      (best-performing :ucb-value (map #(ucb-value % arms) arms))))


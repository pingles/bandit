(ns clj-bandit.algo.ucb
  (:use [clj-bandit.storage :only (get-arms put-arms)]
        [clj-bandit.arms :only (best-performing total-pulls)]
        [clojure.math.numeric-tower :only (sqrt)]))

(defn unused-arms
  [arms]
  (filter (fn [{:keys [pulls]}] (zero? pulls)) arms))

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
  (or (first (unused-arms arms))
      (best-performing :ucb-value (map #(ucb-value % arms) arms))))

(comment
  (def arms (map mk-arm [:arm1 :arm2 :arm3]))
  (def selected-arm (select-arm arms))
  (select-arm (fold-arm (reward selected-arm 1) arms)))
(ns clj-bandit.algo.ucb
  (:use [clj-bandit.arms :only (best-performing unpulled total-pulls)]
        [clojure.math.numeric-tower :only (sqrt)]))

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
  (or (first (unpulled arms))
      (best-performing :ucb-value (map #(ucb-value % arms) arms))))

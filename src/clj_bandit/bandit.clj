(ns clj-bandit.bandit)

(defrecord Arm [name pulls value])

(defn mk-arm
  ([name]
     (Arm. name 0 0))
  ([name & keyvals]
     (apply assoc (mk-arm name) keyvals)))

(defn total-pulls
  [arms]
  (reduce + (map :pulls arms)))

(defn best-performing
  [arms]
  (apply max-key :value arms))


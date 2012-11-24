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
  [k arms]
  (apply max-key k arms))

(defn weighted-value
  [n value reward]
  (+ (* (/ (dec n) n)
        value)
     (* (/ 1 n)
        reward)))

(defn reward
  "returns the arm, with an update n and reward calculation."
  [{:keys [pulls value] :as arm} reward]
  (let [u (assoc arm :pulls (inc pulls))]
    (if (zero? pulls)
      (assoc u :value reward)
      (assoc u :value (weighted-value pulls value reward)))))

(defn fold-arm
  "returns arms with the data for arm folded in."
  [{:keys [name] :as arm} arms]
  (conj (remove (fn [x] (= name (:name x)))
                arms)
        arm))


(ns ^{:doc "Arms are used to track the performance of a bandit for the algorithm."
      :author "Paul Ingles"}
  clj-bandit.arms)

(defrecord Arm [name pulls value])

(defn mk-arm
  ([name]
     (Arm. name 0 0))
  ([name & keyvals]
     (apply assoc (mk-arm name) keyvals)))

(defn mk-arms
  [& names]
  (apply sorted-map  (interleave names
                                 (map mk-arm names))))

(defn total-pulls
  [arms]
  (reduce + (map :pulls arms)))

(defn unpulled
  [arms]
  (filter #(zero? (:pulls %)) arms))

(defn best-performing
  "identifies the arm with the highest value for (k arm)"
  [k arms]
  (apply max-key k arms))

(defn weighted-value
  [{:keys [pulls value] :as arm} reward]
  (+ (* (/ (dec pulls) pulls)
        value)
     (* (/ 1 pulls)
        reward)))

(defn reward
  "updates the arm given a reward (numeric value)"
  [{:keys [pulls] :as arm} reward]
  (let [u (assoc arm :pulls (inc pulls))]
    (if (zero? pulls)
      (assoc u :value reward)
      (assoc u :value (weighted-value arm reward)))))

(defn fold-arm
  "merges the updated arm back into the arms vector"
  [{:keys [name] :as arm} arms]
  (conj (remove (fn [x] (= name (:name x)))
                arms)
        arm))


(ns ^{:doc "Arms are used to track the performance of a bandit for the algorithm."
      :author "Paul Ingles"}
  clj-bandit.arms)

(defrecord Arm [name pulls value])

(defn mk-arm
  ([name] (Arm. name 0 0))
  ([name & keyvals] (apply assoc (mk-arm name) keyvals)))

(defn mk-arms
  "Creates a sorted map to hold onto the state of arms"
  [& names]
  (apply sorted-map (interleave names (map mk-arm names))))

(defn total-pulls
  [arms]
  (reduce + (map :pulls arms)))

(defn unpulled
  [arms]
  (filter #(zero? (:pulls %)) arms))

(defn exploit
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

(defn update
  "merges the updated arm back into arms"
  [{:keys [name] :as arm} arms]
  (assoc arms name arm))

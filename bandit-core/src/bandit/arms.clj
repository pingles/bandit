(ns ^{:doc "Arms are used to track the performance of a bandit for the algorithm."
      :author "Paul Ingles"}
  bandit.arms)

(defrecord Arm [name pulls value])

(defn arm
  ([name] (Arm. name 0 0))
  ([name & keyvals] (apply assoc (arm name) keyvals)))

(defn bandit
  "Creates a sorted map to hold onto the bandit's state"
  [& names]
  (apply sorted-map (interleave names (map arm names))))

(defn total-pulls
  [arms]
  (reduce + (map :pulls arms)))

(defn unpulled
  [arms]
  (filter #(zero? (:pulls %)) arms))

(defn rank
  "Sort arms by largest (k arm) value."
  [k arms]
  (reverse (sort-by k arms)))

(defn exploit
  "identifies the arm with the highest value for (k arm)"
  [k arms]
  (first (rank k arms)))

(defn weighted-value
  [{:keys [pulls value] :as arm} reward]
  (+ (* (/ (dec pulls) pulls)
        value)
     (* (/ 1 pulls)
        reward)))

(defn pulled
  "Record that we've pulled the arm. Allows the pull and reward
   to be tracked in separate events.
   e.g: (-> arm (pulled) (reward 0))"
  [{:keys [pulls] :as arm}]
  (assoc arm :pulls (inc pulls)))

(defn reward
  "updates the arm given a reward (numeric value)"
  [{:keys [pulls] :as arm} reward]
  (if (zero? pulls)
    (assoc arm :value reward)
    (assoc arm :value (weighted-value arm reward))))

(defn update
  "merges the updated arm back into arms"
  [{:keys [name] :as arm} arms]
  (assoc arms name arm))

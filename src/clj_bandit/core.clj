(ns clj-bandit.core)

(defn mk-arms
  "Creates the structure suitable for storing arm results"
  [labels]
  (apply merge (map (fn [label]
                      {label {:n 0
                              :reward 0
                              :value 0}})
                    labels)))

(defn- individual-maps
  "breaks m into a vector of maps. useful to break apart arms map"
  [m]
  (map #(apply hash-map %) (seq m)))

(defn best-performing
  "Given a map of arms + results, pick the one with the current highest reward"
  [m]
  (let [perf (fn [m]
               (:value (first (vals m))))]
    (apply max-key perf (individual-maps m))))



(defn weighted-average-value
  "calculates the reward value for the arm. uses a weighted average."
  ([reward]
     reward)
  ([reward {:keys [n value] :as arm}]
     (+ (* (/ (- n 1)
              n)
           value)
        (* (/ 1 n)
           reward))))

(defn arm-value
  "updates arm map given latest reward." 
  [value-fn latest-reward {:keys [n reward] :as arm}]
  (let [updated {:n (inc n)
                 :reward (+ latest-reward reward)}]
    (if (= n 0)
      (assoc updated :value (value-fn latest-reward))
      (assoc updated :value (value-fn latest-reward arm)))))

(defn update-arms
  [reward arm arms]
  (update-in arms [arm] #(arm-value weighted-average-value reward %)))


(defprotocol BanditAlgorithm
  (select-arm [this] "returns the label for the arm we pulled")
  (update-reward [this arm reward] "update performance for the arm")
  (arms [this] "Current results"))


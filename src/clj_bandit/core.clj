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

(defprotocol BanditAlgorithm
  (select-arm [this] "returns the label for the arm we pulled")
  (update-reward [this arm reward] "update performance for the arm")
  (arms [this] "Current results"))


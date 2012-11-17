(ns clj-bandit.core)

(defn- individual-maps
  "breaks m into a vector of maps. useful to break apart arms map"
  [m]
  (map #(apply hash-map %) (seq m)))

(defn best-performing
  "Given a map of arms + results, pick the one with the current highest reward"
  [arms]
  (let [perf (fn [m]
               (:value (first (vals m))))]
    (apply max-key perf (individual-maps arms))))

(defprotocol BanditAlgorithm
  (select-arm [this] "returns the label for the arm we pulled")
  (update-reward [this arm reward] "update performance for the arm")
  (arms [this] "Current results"))


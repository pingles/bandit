(ns clj-bandit.core)

(defn mk-levers
  "Creates the structure suitable for storing lever results"
  [labels]
  (apply merge (map (fn [label]
                      {label {:n 0
                              :reward 0
                              :value 0}})
                    labels)))

(defn- individual-maps
  "breaks m into a vector of maps. useful to break apart an overall map of levers"
  [m]
  (map #(apply hash-map %) (seq m)))

(defn best-performing
  "Given a map of levers + results, pick the one with the current highest reward"
  [m]
  (let [perf (fn [m]
               (:value (first (vals m))))]
    (apply max-key perf (individual-maps m))))



(defn weighted-average-value
  "calculates the reward value for the arm. uses a weighted average."
  ([reward]
     reward)
  ([reward {:keys [n value] :as levers}]
     (+ (* (/ (- n 1)
              n)
           value)
        (* (/ 1 n)
           reward))))

(defn arm-value
  "returns updated arm results map given the most recently observed reward.
   (arm-value 1 {:n 2 :reward 0 :value 0}) => {:value 1/2 :n 3 :reward 1}"
  ([latest-reward results]
     (arm-value weighted-average-value latest-reward results))
  ([value-fn latest-reward {:keys [n reward] :as results}]
     (let [updated {:n (inc n)
                    :reward (+ latest-reward reward)}]
       (if (= n 0)
         (assoc updated :value (value-fn latest-reward))
         (assoc updated :value (value-fn latest-reward results))))))

(defn update-levers
  [reward lever levers]
  (update-in levers [lever] #(arm-value reward %)))


(defprotocol BanditStorage
  (get-levers [storage])
  (put-levers [storage f]))

(defprotocol Bandit
  (select-arm [bandit] "returns the label for the arm we pulled")
  (update-reward [bandit arm reward] "update performance for the arm")
  (levers [bandit] "Current results"))

(defn atom-storage
  [labels]
  (let [levers (atom (mk-levers labels))]
    (reify BanditStorage
      (get-levers [_]
        @levers)
      (put-levers [_ f]
        (swap! levers f)))))

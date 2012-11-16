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
  ([reward {:keys [n value] :as arms}]
     (+ (* (/ (- n 1)
              n)
           value)
        (* (/ 1 n)
           reward))))

(defn arm-value
  "updates lever map given latest reward.
   (arm-value 1 {:n 2 :reward 0 :value 0}) => {:value 1/2 :n 3 :reward 1}"
  ([latest-reward arm]
     (arm-value weighted-average-value latest-reward arm))
  ([value-fn latest-reward {:keys [n reward] :as arm}]
     (let [updated {:n (inc n)
                    :reward (+ latest-reward reward)}]
       (if (= n 0)
         (assoc updated :value (value-fn latest-reward))
         (assoc updated :value (value-fn latest-reward arm))))))

(defn update-arms
  [reward arm arms]
  (update-in arms [arm] #(arm-value reward %)))


(defprotocol BanditStorage
  (get-arms [storage])
  (put-arms [storage f]))

(defprotocol Bandit
  (select-arm [bandit] "returns the label for the arm we pulled")
  (update-reward [bandit arm reward] "update performance for the arm")
  (arms [bandit] "Current results"))

(defn atom-storage
  [labels]
  (let [arms (atom (mk-arms labels))]
    (reify BanditStorage
      (get-arms [_]
        @arms)
      (put-arms [_ f]
        (swap! arms f)))))

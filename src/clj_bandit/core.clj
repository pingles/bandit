(ns clj-bandit.core)

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



(defn pull-arm
  "Picks a lever at random, with P(epsilon) of exploring and P(1-epsilon) of exploiting the currently best performing arm."
  [epsilon levers]
  (let [p (rand)]
    (if (> p epsilon)
      {:strategy :exploit
       :arm (best-performing levers)}
      {:strategy :explore
       :arm (apply hash-map (rand-nth (seq levers)))})))


(defn weighted-average-value
  "calculates the reward value for the arm. uses a weighted average"
  ([reward]
     reward)
  ([reward {:keys [n value] :as m}]
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


(defn bernoulli-arm
  "returns a function mimicking a bandit arm with a set probability. each function call represents the pulling of a bandit arm. can be used to simulate results. 0 < p < 1. p indicates probability of being rewarded (with 1).

   e.g (def draw-arm (bernoulli-arm 0.1)) ; 10% payout
   (repeatedly 10 draw-arm)"
  [p]
  (fn []
    (if (> (rand) p)
      0
      1)))

(defn draw-arm
  [f]
  (f))

;; take 5 results from a sequence of results for a bernoulli arm
;; (take 5 (repeatedly #(draw-arm (bernoulli-arm 0.1))))

(defn lever-name
  [pull]
  (first (keys (:arm pull))))

(defn update-levers
  [reward lever levers]
  (update-in levers [lever] #(arm-value 1 %)))



;; now have the building blocks for our algorithm:
;; 1) initialise the levers map
;; 2) pull-arm
;; 3) update results
(comment
  (def levers {:lever1 {:n 1
                        :reward 0
                        :value 0}
               :lever2 {:n 1
                        :reward 0
                        :value 0}})
  (def pull (pull-arm 0.2 levers))
  ;; now need to update based on the reward. update-levers returns the
  ;; new value for levers.
  (update-levers 1 :lever1 levers))
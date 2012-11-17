(ns clj-bandit.epsilon
  (:use [clj-bandit.core :only (BanditAlgorithm best-performing)]
        [clj-bandit.storage :only (get-arms put-arms)]))

(defn mk-arms
  "Creates the structure suitable for storing arm results for epsilon greedy algo."
  [labels]
  (apply merge (map (fn [label]
                      {label {:n 0
                              :reward 0
                              :value 0}})
                    labels)))

(defn weighted-average-value
  "calculates the reward value for the arm. uses a weighted average."
  ([reward] reward)
  ([reward {:keys [n value] :as arm}]
     (+ (* (/ (dec n) n)
           value)
        (* (/ 1 n)
           reward))))

(defn arm-value
  "updates arm map given latest reward." 
  [value-fn latest-reward {:keys [n reward] :as arm}]
  (let [updated {:n (inc n)
                 :reward (+ latest-reward reward)}]
    (if (zero? n)
      (assoc updated :value (value-fn latest-reward))
      (assoc updated :value (value-fn latest-reward arm)))))

(defn update-arms
  [reward arm arms]
  (update-in arms [arm] #(arm-value weighted-average-value reward %)))

(defn epsilon-greedy-algorithm
  "Returns an Epsilon-Greedy bandit with the specified lever labels. uses atom storage"
  [epsilon storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (if (> (rand) epsilon)
        {:strategy :exploit
         :arm (best-performing (get-arms storage))}
        {:strategy :explore
         :arm (apply hash-map (rand-nth (seq (get-arms storage))))}))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

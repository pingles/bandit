(ns clj-bandit.softmax
  (:use [clj-bandit.core :only (cumulative-sum BanditAlgorithm)]
        [clj-bandit.storage :only (get-arms put-arms)]))

(defn mk-arms
  "Creates the structure suitable for storing arm results for epsilon greedy algo."
  [labels]
  (letfn [(arm-map [label] {label {:n 0 :reward 0 :value 0}})]
    (apply merge (map arm-map labels))))

(defn z
  [temperature values]
  (reduce + (map (fn [x] (Math/exp (/ x temperature))) values)))

(defn probabilities
  [temperature values]
  (let [z-value (z temperature values)]
    (map (fn [x] (/ (Math/exp (/ x temperature))
                   z-value))
         values)))

(defn probability-maps
  [temperature values]
  (let [probs (probabilities temperature values)
        cum-probs (cumulative-sum probs)]
    (map (fn [[p cum-p]] {:p p :cum-p cum-p})
         (partition 2 (interleave probs cum-probs)))))

(defn categorical-draw
  [temperature arms]
  (let [arm-vec (vec arms)
        p-maps (probability-maps temperature (map #(:value (last %)) arm-vec))
        arm-labels (map first arm-vec)
        arm-vals (map last arm-vec)
        merged-arm-vals (map #(apply merge %)
                             (partition 2 (interleave arm-vals p-maps)))
        z (rand)]
    (apply hash-map (or (first (filter (fn [[_ {:keys [cum-p]}]] (> cum-p z))
                                       (partition 2
                                                  (interleave arm-labels merged-arm-vals))))
                        (first arms)))))

;; TODO
;; this is almost the exact same as in epsilon, extract
(defn arm-value
  [latest-reward {:keys [n reward value] :as arm}]
  (let [updated {:n (inc n)
                 :reward (+ reward latest-reward)}]
    (if (zero? n)
      (assoc updated :value latest-reward)
      (assoc updated :value (+ (* (/ (dec n)
                                     n)
                                  value)
                               (* (/ 1 n)
                                  reward))))))

(defn update-arms
  [reward arm arms]
  (update-in arms [arm] #(arm-value reward %)))

(defn softmax-algorithm
  "Temperature controls how stable the algorithm is. The lower the temperature the more stable. 0 < temperature < 1"
  [temperature storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (categorical-draw temperature (get-arms storage)))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))
(ns clj-bandit.algo.softmax
  (:use [clj-bandit.core :only (cumulative-sum BanditAlgorithm weighted-arm-value)]
        [clj-bandit.storage :only (get-arms put-arms)]
        [clojure.math.numeric-tower :only (sqrt expt)]))

(defn z
  [temperature values]
  (reduce + (map (fn [x] (Math/exp (/ x temperature)))
                 values)))

(defn p
  [temperature z value]
  (/ (Math/exp (/ value temperature))
     z))

(defn probabilities
  [temperature values]
  (let [z-value (z temperature values)]
    (map (partial p temperature z-value) values)))

(defn probability-maps
  [temperature values]
  (let [probs (probabilities temperature values)
        cum-probs (cumulative-sum probs)]
    (map (fn [[p cum-p]] {:p p :cum-p cum-p})
         (partition 2 (interleave probs cum-probs)))))

(defn select-cumulative-probability
  [val arms]
  (if-let [found (first (filter (fn [[_ {:keys [cum-p]}]]
                                  (> cum-p val)) arms))]
    (apply hash-map found)))

(defn select-draw
  "Given arms with cum-p values, pick the one we'll use"
  [rand-val arms]
  (or (select-cumulative-probability rand-val arms)
      (apply hash-map (last arms))))

(defn draw-probabilities
  [temperature arms]
  (let [arm-vec (vec arms)
        values (map #(:value (last %)) arm-vec)]
    (let [p-maps (probability-maps temperature values)
          arm-labels (map first arm-vec)
          arm-vals (map last arm-vec)
          merged-arm-vals (map #(apply merge %)
                               (partition 2 (interleave arm-vals p-maps)))]
      (apply merge (map #(apply hash-map %)
                        (partition 2 (interleave arm-labels merged-arm-vals)))))))

(defn make-draw
  ([temperature arms]
     (make-draw temperature (rand) arms))
  ([temperature rand-val arms]
     (if (not-every? zero? (map :value (vals arms)))
       (select-draw rand-val (draw-probabilities temperature arms))
       (apply hash-map (last arms)))))

(defn update-arms
  [reward arm arms]
  (update-in arms [arm] (partial weighted-arm-value reward)))

(defn softmax-algorithm
  "Temperature controls how stable the algorithm is. The lower the temperature the more stable. 0 < temperature < 1"
  [temperature storage]
  (reify BanditAlgorithm
    (select-arm [_]
      (make-draw temperature (get-arms storage)))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))
(ns clj-bandit.core)

(defn mk-arms
  [labels]
  (letfn [(arm-map [label] {label {:n 0 :reward 0 :value 0}})]
    (apply merge (map arm-map labels))))

(defn individual-maps
  "breaks m into a vector of maps. useful to break apart arms map"
  [m]
  (map #(apply hash-map %) (seq m)))

(defn best-performing
  "Given a map of arms + results, pick the one with the current highest (k val). assumes each map contains a value for k."
  [k arms]
  (letfn [(performance [arm]
            (k (->> arm vals first)))]
    (apply max-key performance (individual-maps arms))))

(defn cumulative-sum
  [coll]
  (reduce (fn [v, x] (conj v (+ (last v) x)))
          [(first coll)]
          (rest coll)))

(defn weighted-value
  "calculates the update value given the acknowledgement of a reward."
  [n value reward]
  (+ (* (/ (dec n) n)
        value)
     (* (/ 1 n)
        reward)))

(defn weighted-arm-value
  [latest-reward {:keys [n reward value] :as arm}]
  (let [updated {:n (inc n)
                 :reward (+ reward latest-reward)}]
    (if (zero? n)
      (assoc updated :value latest-reward)
      (assoc updated :value (weighted-value n value latest-reward)))))


;; TODO
;; update-reward requires acknowledgement of either a reward or not at
;; the same time as incrementing n. this is no use for using in a
;; situation where the reward may occur some time later (ie
;; conversion, sale etc.). need to increment n separately from
;; tracking arm reward/value.
;; when being used through an algo with state its probably fine to
;; hide behind select-arm fn, or better to make explicit?

(defprotocol BanditAlgorithm
  (select-arm [this] "returns the label for the arm we pulled")
  (update-reward [this arm reward] "update performance for the arm")
  (arms [this] "Current results"))

(ns clj-bandit.core)

(defn individual-maps
  "breaks m into a vector of maps. useful to break apart arms map"
  [m]
  (map #(apply hash-map %) (seq m)))

(defn best-performing
  "Given a map of arms + results, pick the one with the current highest reward"
  [arms]
  (letfn [(performance [arm]
            (:value (->> arm vals first)))]
    (apply max-key performance (individual-maps arms))))

(defn cumulative-sum
  [coll]
  (reduce 
   (fn [v, x] (conj v (+ (last v) x))) 
   [(first coll)] 
   (rest coll)))

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


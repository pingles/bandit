(ns clj-bandit.algo.epsilon
  (:use [clj-bandit.core :only (BanditAlgorithm update-arms)]
        [clj-bandit.storage :only (get-arms put-arms)]))

(defrecord Arm [name n value])

(defn mk-arm
  ([name]
     (Arm. name 0 0))
  ([name & keyvals]
     (apply assoc (mk-arm name) keyvals)))

(defn total-pulls
  [arms]
  (reduce + (map :n arms)))

(defn best-performing
  [arms]
  (apply max-key :value arms))

(defn draw-arm
  ([epsilon arms]
     (draw-arm (rand) arms))
  ([epsilon n arms]
     (if (> n epsilon)
       (best-performing arms)
       (rand-nth (seq arms)))))

(defn draw-anneal-arm
  [anneal arms]
  (draw-arm (anneal (total-pulls arms)) arms))


(defn weighted-value
  [n value reward]
  (+ (* (/ (dec n) n)
        value)
     (* (/ 1 n)
        reward)))

(defn reward
  "updates the arm with its new weighted value."
  [{:keys [n value] :as arm} reward]
  (let [u (assoc arm :n (inc (:n arm)))]
    (if (zero? n)
      (assoc u :value reward)
      (assoc u :value (weighted-value n value reward)))))







(defmulti epsilon-greedy-algorithm
  "epsilon can either be a constant factor, or a function that will be
   applied to the current number of pulls. use with anneal to cause
   algorithm to favour exploitation over time."
  (fn [x _] (number? x)))

(defn- mk-algorithm
  [storage selectfn]
  (reify BanditAlgorithm
    (select-arm [_]
      (selectfn (get-arms storage)))
    (update-reward [_ arm reward]
      (put-arms storage #(update-arms reward arm %)))
    (arms [_]
      (get-arms storage))))

(defmethod epsilon-greedy-algorithm true
  [epsilon storage]
  (mk-algorithm storage (partial draw-arm epsilon)))

(defmethod epsilon-greedy-algorithm false
  [annealfn storage]
  (mk-algorithm storage (partial draw-anneal-arm annealfn)))



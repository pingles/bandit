(ns clj-bandit.algo.epsilon
  (:use [clj-bandit.arms :only (best-performing total-pulls)]))

(defn draw-arm
  ([epsilon arms]
     (draw-arm epsilon (rand) arms))
  ([epsilon n arms]
     (if (> n epsilon)
       (best-performing :value arms)
       (rand-nth (seq arms)))))

;; stuff needed to operate the bandit:

(defmulti select-arm (fn [epsilon arms] (number? epsilon)))

(defmethod select-arm true
  [epsilon arms]
  (draw-arm epsilon arms))

(defmethod select-arm false
  [annealfn arms]
  (draw-arm (annealfn (total-pulls arms)) arms))

(comment
  (def arms (map mk-arm [:arm1 :arm2 :arm3]))
  (def selected-arm (select-arm 0.1 arms))
  (select-arm 0.1 (fold-arm (reward selected-arm 1) arms)))





;; TODO
;; Want to delete all the stuff below. just need a way to fold
;; updated arms together


;; (defmulti epsilon-greedy-algorithm
;;   "epsilon can either be a constant factor, or a function that will be
;;    applied to the current number of pulls. use with anneal to cause
;;    algorithm to favour exploitation over time."
;;   (fn [x _] (number? x)))

;; (defn- mk-algorithm
;;   [storage selectfn]
;;   (reify BanditAlgorithm
;;     (select-arm [_]
;;       (selectfn (get-arms storage)))
;;     (update-reward [_ arm reward]
;;       (put-arms storage #(update-arms reward arm %)))
;;     (arms [_]
;;       (get-arms storage))))

;; (defmethod epsilon-greedy-algorithm true
;;   [epsilon storage]
;;   (mk-algorithm storage (partial draw-arm epsilon)))

;; (defmethod epsilon-greedy-algorithm false
;;   [annealfn storage]
;;   (mk-algorithm storage (partial draw-anneal-arm annealfn)))



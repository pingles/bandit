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



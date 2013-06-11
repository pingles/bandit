(ns clj-bandit.algo.epsilon-test
  (:use [expectations]
        [clj-bandit.arms :only (mk-arm best-performing total-pulls reward fold-arm)]
        [clj-bandit.algo.epsilon]))

;; select the best performing arm
(given (best-performing :value [(mk-arm :arm1) (mk-arm :arm2 :value 1)])
       (expect :name :arm2
               :value 1))

(expect 1 (total-pulls [(mk-arm :arm1 :pulls 1) (mk-arm :arm2)]))

;; tracking arm reward
(given (-> (mk-arm :arm1) (reward 0) (reward 1))
       (expect :name :arm1
               :value 1
               :pulls 2))

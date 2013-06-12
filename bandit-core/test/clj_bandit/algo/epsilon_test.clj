(ns clj-bandit.algo.epsilon-test
  (:use [expectations]
        [clj-bandit.arms]
        [clj-bandit.algo.epsilon]))

;; tracking arm reward
(given (-> (mk-arm :arm1) (reward 0) (reward 1))
       (expect :name :arm1
               :value 1
               :pulls 2))

(let [arms (mk-arms :arm1 :arm2)]
  (expect (select-arm 0.1 (vals arms))))

(ns clj-bandit.algo.ucb_test
  (:use [expectations]
        [clj-bandit.arms :only (mk-arm best-performing unpulled)]
        [clj-bandit.algo.ucb]))

;; picking unpulled arms
(expect 2 (count (unpulled [(mk-arm :arm1) (mk-arm :arm2)])))
(expect empty? (unpulled [(mk-arm :arm1 :pulls 1)]))

;; selecting best performing
(expect (mk-arm :arm1 :ucb-value 1)
        (best-performing :ucb-value [(mk-arm :arm1 :ucb-value 1) (mk-arm :arm2 :ucb-value 0)]))

;; ucb bonus values
(expect 1.2389740629499462 (bonus-value 10 3))
(given (ucb-value (mk-arm :arm1 :pulls 1)
                  [(mk-arm :arm1 :pulls 1) (mk-arm :arm2 :pulls 2)])
       (expect :name :arm1
               :pulls 1
               :ucb-value 1.4823038073675112))

;; selecting arms
(given (select-arm [(mk-arm :arm1) (mk-arm :arm2 :pulls 1)])
       (expect :name :arm1))
(given (select-arm [(mk-arm :arm1 :pulls 1 :value 1)
                    (mk-arm :arm2 :pulls 10 :value 10)])
       (expect :name :arm2
               :pulls 10
               :ucb-value 10.692516465190305))


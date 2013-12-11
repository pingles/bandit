(ns clj-bandit.algo.softmax_test
  (use [expectations]
       [clj-bandit.arms :only (mk-arm)]
       [clj-bandit.algo.softmax]))

;; z-values
(expect 1.0687444933941748E13 (z 1/10 [2 2 3 1]))
(expect 49.52498537632265 (z 9/10 [2 2 3 1]))

;; probabilities
(expect 0.5 (probability 1 (mk-arm :arm1) [(mk-arm :arm1) (mk-arm :arm2)]))
(given (probabilities 1 [(mk-arm :arm1 :value 1) (mk-arm :arm2 :value 10)])
       (expect #(map :cumulative-p %) [1.233945759862317E-4 0.9999999999999999]
               #(map :p %) [1.233945759862317E-4 0.9998766054240137]))

;; select unpulled arms
(expect (mk-arm :arm1) (select-arm 1 1 [(mk-arm :arm1) (mk-arm :arm2)]))
(expect (mk-arm :arm2) (select-arm 1 1 [(mk-arm :arm1 :pulls 1) (mk-arm :arm2)]))

;; selecting arms by their cumulative probability
(let [arms [(mk-arm :arm1 :cumulative-p 0.1 :pulls 10)
            (mk-arm :arm2 :cumulative-p 0.9 :pulls 10)]]
  (expect (mk-arm :arm1 :p 0.5 :cumulative-p 0.5 :pulls 10) (select-arm 1 0.05 arms))
  (expect (mk-arm :arm2 :p 0.5 :cumulative-p 1.0 :pulls 10) (select-arm 1 0.91 arms))
  (expect (mk-arm :arm2 :cumulative-p 0.9 :pulls 10) (select-arm 1 100 arms)))

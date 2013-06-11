(ns clj-bandit.arms_test
  (:use [expectations]
        [clj-bandit.arms]))

;; finding unpulled arms
(expect empty? (unpulled [(mk-arm :arm1 :pulls 1)]))
(expect [(mk-arm :arm1)] (unpulled [(mk-arm :arm1) (mk-arm :arm2 :pulls 1)]))

;; finding best value
(expect (mk-arm :arm1 :value 10) (best-performing :value [(mk-arm :arm1 :value 10)
                                                          (mk-arm :arm2)]))

;; total pulls
(expect 10 (total-pulls [(mk-arm :arm1 :pulls 5) (mk-arm :arm2 :pulls 5)]))

;; weighted values
(expect 1 (weighted-value (mk-arm :arm1 :pulls 1 :value 0) 1))
(expect 1 (weighted-value (mk-arm :arm1 :pulls 2 :value 1) 1))
(expect 2/3 (weighted-value (mk-arm :arm1 :pulls 3 :value 1) 0))

;; folding arm results
(expect [(mk-arm :arm1 :pulls 1) (mk-arm :arm2)]
        (fold-arm (mk-arm :arm1 :pulls 1)
                  [(mk-arm :arm1) (mk-arm :arm2)]))

(ns clj-bandit.arms-test
  (:use [expectations]
        [clj-bandit.arms]))

;; making arms
(given (mk-arms :arm1 :arm2 :arm3)
       (expect count 3
               :arm1 (mk-arm :arm1 :pulls 0 :value 0)
               vals [(mk-arm :arm1) (mk-arm :arm2) (mk-arm :arm3)]))

;; tracking arm reward
;; recording a pull
(given (-> (mk-arm :arm1) (pulled))
       (expect :pulls 1))

;; recording the reward
(given (-> (mk-arm :arm1) (pulled) (reward 1))
       (expect :pulls 1
               :value 1))


;; finding unpulled arms
(expect empty? (unpulled [(mk-arm :arm1 :pulls 1)]))
(expect [(mk-arm :arm1)] (unpulled [(mk-arm :arm1) (mk-arm :arm2 :pulls 1)]))

;; finding best value
(expect (mk-arm :arm1 :value 10) (exploit :value [(mk-arm :arm1 :value 10)
                                                          (mk-arm :arm2)]))

;; total pulls
(expect 10 (total-pulls [(mk-arm :arm1 :pulls 5) (mk-arm :arm2 :pulls 5)]))

;; weighted values
(expect 1 (weighted-value (mk-arm :arm1 :pulls 1 :value 0) 1))
(expect 1 (weighted-value (mk-arm :arm1 :pulls 2 :value 1) 1))
(expect 2/3 (weighted-value (mk-arm :arm1 :pulls 3 :value 1) 0))

;; folding arm results
(expect [(mk-arm :arm1 :pulls 1 :value 1) (mk-arm :arm2)]
        (vals (update (-> (mk-arm :arm1) (pulled) (reward 1))
                      (mk-arms :arm1 :arm2))))


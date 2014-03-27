(ns bandit.arms-test
  (:use [expectations]
        [bandit.arms]))

;; making arms
(given (bandit :arm1 :arm2 :arm3)
       (expect count 3
               :arm1 (arm :arm1 :pulls 0 :value 0)
               vals [(arm :arm1) (arm :arm2) (arm :arm3)]))

(let [arms (bandit :arm1 :arm2)]
  (given (rank :value (vals (bandit.arms/update (-> (first (vals arms))
                                                    (pulled)
                                                    (reward 1))
                                                arms)))
         (expect first (->Arm :arm1 1 1))
         (expect second (arm :arm2))))

;; tracking arm reward
;; recording a pull
(given (-> (arm :arm1) (pulled))
       (expect :pulls 1))

;; recording the reward
(given (-> (arm :arm1) (pulled) (reward 1))
       (expect :pulls 1
               :value 1))


;; finding unpulled arms
(expect empty? (unpulled [(arm :arm1 :pulls 1)]))
(expect [(arm :arm1)] (unpulled [(arm :arm1) (arm :arm2 :pulls 1)]))

;; finding best value
(expect (arm :arm1 :value 10) (exploit :value [(arm :arm1 :value 10)
                                                          (arm :arm2)]))

;; total pulls
(expect 10 (total-pulls [(arm :arm1 :pulls 5) (arm :arm2 :pulls 5)]))

;; weighted values
(expect 1 (weighted-value (arm :arm1 :pulls 1 :value 0) 1))
(expect 1 (weighted-value (arm :arm1 :pulls 2 :value 1) 1))
(expect 2/3 (weighted-value (arm :arm1 :pulls 3 :value 1) 0))

;; folding arm results
(expect [(arm :arm1 :pulls 1 :value 1) (arm :arm2)]
        (vals (update (-> (arm :arm1) (pulled) (reward 1))
                      (bandit :arm1 :arm2))))

(ns bandit.algo.exp3-test
  (:use [expectations]
        [bandit.algo.exp3]))

(expect 1 (:weight (arm :arm1)))
(expect 2 (total-weight [(arm :arm1) (arm :arm2)]))

(expect 1/2 (arm-probability 1 2 2 {:weight 1}))

(expect [(arm :arm1 :p 1 :weight 1)] (arm-probabilities 1 [(arm :arm1)]))

(given (cumulative-probabilities [(arm :arm1 :p 0.5) (arm :arm2 :p 0.5)])
       (expect (comp :cumulative-p first) 0.5
               (comp :cumulative-p second) 1.0))

;; tracking reward
(given (reward 1 1 (arm :arm1 :p 1) 1)
       (expect :weight 2.718281828459045))

;; draw arm based on arm probability
(let [arms [(arm :arm1 :p 0.5) (arm :arm2 :p 0.99)]]
  (given (categorical-draw arms 0.9)
         (expect :name :arm2))
  (given (categorical-draw arms 0.4)
         (expect :name :arm1))
  (given (categorical-draw arms 1)
         (expect :name :arm2)))

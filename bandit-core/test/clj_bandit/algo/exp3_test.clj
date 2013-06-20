(ns clj-bandit.algo.exp3-test
  (:use [expectations]
        [clj-bandit.algo.exp3]))

(expect 1 (:weight (mk-arm :arm1)))
(expect 2 (total-weight [(mk-arm :arm1) (mk-arm :arm2)]))

(expect 1/2 (arm-p 1 2 2 {:weight 1}))

(expect [(mk-arm :arm1 :p 1 :weight 1)] (arms-with-probs 1 [(mk-arm :arm1)]))

(given (cumulative-probabilities [(mk-arm :arm1 :p 0.5) (mk-arm :arm2 :p 0.5)])
       (expect (comp :cumulative-p first) 0.5
               (comp :cumulative-p second) 1.0))

;; tracking reward
(given (reward 1 1 (mk-arm :arm1 :p 1) 1)
       (expect :weight 2.7182818284590455))

;; draw arm based on arm probability
(let [arms [(mk-arm :arm1 :p 0.5) (mk-arm :arm2 :p 0.99)]]
  (given (categorical-draw arms 0.9)
         (expect :name :arm2))
  (given (categorical-draw arms 0.4)
         (expect :name :arm1))
  (given (categorical-draw arms 1)
         (expect :name :arm2)))

(ns clj-bandit.softmax_test
  (use [clojure.test]
       [clj-bandit.core :only (select-arm cumulative-sum)]
       [clj-bandit.storage :only (atom-storage)]
       [clj-bandit.softmax] :reload))

(deftest z-values
  (is (= 1800
         (z 1/10 [2 2 3 1])))
  (is (= 200/9
         (z 9/10 [2 2 3 1])))
  (is (= [1/14 2/7 9/14]
         (probabilities 1/10 [1 2 3])))
  (is (= [1/14 2/7 9/14]
         (probabilities 9/10 [1 2 3])))
  (is (= [1/14 5/14 1]
         (cumulative-sum (probabilities 1/10 [1 2 3]))))
  (is (= [1/14 5/14 1]
         (cumulative-sum (probabilities 5/10 [1 2 3])))))

(deftest calculating-arm-value
  (is (= {:n 1 :reward 1 :value 1}
         (arm-value 1 {:n 0 :reward 0 :value 0})))
  (is (= {:n 2 :reward 2 :value 1}
         (arm-value 1 {:n 1 :reward 1 :value 0})))
  (is (= {:value 1, :n 3, :reward 3}
         (arm-value 1 {:n 2 :reward 2 :value 1})))
  (is (= {:value 23/5 :n 11 :reward 2}
         (arm-value 1 {:n 10 :reward 1 :value 5}))))

(deftest picking
  (is (= {:arm2 {:cum-p 0.2857142857142857
                 :p 0.2857142857142857
                 :reward 0
                 :n 11
                 :value 10}
          :arm3 {:cum-p 0.3571428571428571
                 :p 0.07142857142857142
                 :reward 0
                 :n 11
                 :value 5}
          :arm1 {:cum-p 1.0
                 :p 0.6428571428571429
                 :reward 0
                 :n 12
                 :value 15}}
         (draw-probabilities 0.1 {:arm1 {:n 12 :reward 0 :value 15}
                                  :arm2 {:n 11 :reward 0 :value 10}
                                  :arm3 {:n 11 :reward 0 :value 5}}))) 
  (is (= {:arm3 {:cum-p 0.3571428571428571
                 :p 0.07142857142857142
                 :reward 0
                 :n 11
                 :value 5}}
         (make-draw 0.1 0.3 {:arm1 {:n 12 :reward 0 :value 15}
                             :arm2 {:n 11 :reward 0 :value 10}
                             :arm3 {:n 11 :reward 0 :value 5}})))
  (is (= {:arm1 {:n 0
                 :reward 0
                 :value 0}}
         (make-draw 0.1 0.3 {:arm1 {:n 0 :reward 0 :value 0}
                             :arm2 {:n 0 :reward 0 :value 0}
                             :arm3 {:n 0 :reward 0 :value 0}})))
  (is (= {:arm3 {:cum-p 1}}
         (select-draw 0.92 {:arm1 {:cum-p 0.28}
                            :arm2 {:cum-p 0.39}
                            :arm3 {:cum-p 1}})))
  (is (= {:arm2 {:cum-p 0.39}}
         (select-draw 0.3 {:arm1 {:cum-p 0.28}
                           :arm2 {:cum-p 0.39}
                           :arm3 {:cum-p 1}})))
  (is (= {:arm1 {:cum-p 0.28}}
         (select-draw 1.2 {:arm1 {:cum-p 0.28}
                           :arm2 {:cum-p 0.39}
                           :arm3 {:cum-p 1}}))))


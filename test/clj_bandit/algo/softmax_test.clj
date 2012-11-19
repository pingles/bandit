(ns clj-bandit.algo.softmax_test
  (use [clojure.test]
       [clj-bandit.core :only (select-arm cumulative-sum)]
       [clj-bandit.storage :only (atom-storage)]
       [clj-bandit.algo.softmax] :reload))

(deftest z-values
  (is (= 1.0687444933941748E13
         (z 1/10 [2 2 3 1])))
  (is (= 49.52498537632265
         (z 9/10 [2 2 3 1])))
  (is (= [2.061060046209062E-9 4.539786860886666E-5 0.9999546000703311]
         (probabilities 1/10 [1 2 3])))
  (is (= [0.07538325148667332 0.22899409853365954 0.6956226499796672]
         (probabilities 9/10 [1 2 3])))
  (is (= [2.061060046209062E-9 4.5399929668912874E-5 1.0]
         (cumulative-sum (probabilities 1/10 [1 2 3]))))
  (is (= [0.015876239976466765 0.1331866678026651 0.9999999999999999]
         (cumulative-sum (probabilities 5/10 [1 2 3])))))

(deftest picking
  (is (= {:arm2 {:cum-p 1.9287498479639178E-22
                 :p 1.9287498479639178E-22
                 :reward 0
                 :n 11
                 :value 10}
          :arm3 {:cum-p 1.9287498479639178E-22
                 :p 3.7200759760208356E-44
                 :reward 0
                 :n 11
                 :value 5}
          :arm1 {:cum-p 1.0
                 :p 1.0
                 :reward 0
                 :n 12
                 :value 15}}
         (draw-probabilities 0.1 {:arm1 {:n 12 :reward 0 :value 15}
                                  :arm2 {:n 11 :reward 0 :value 10}
                                  :arm3 {:n 11 :reward 0 :value 5}})))
  (is (= {:arm1 {:cum-p 1.0
                 :p 1.0
                 :reward 0
                 :n 12
                 :value 15}}
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

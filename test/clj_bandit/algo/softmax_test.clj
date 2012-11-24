(ns clj-bandit.algo.softmax_test
  (use [clojure.test]
       [clj-bandit.core :only (cumulative-sum)]
       [clj-bandit.arms :only (mk-arm)]
       [clj-bandit.algo.softmax] :reload))

(deftest z-values
  (is (= 1.0687444933941748E13
         (z 1/10 [2 2 3 1])))
  (is (= 49.52498537632265
         (z 9/10 [2 2 3 1]))))

(deftest adding-probabilities
  (is (= 0.5
         (probability 1
                      (mk-arm :arm1)
                      [(mk-arm :arm1)
                       (mk-arm :arm2)])))
  (let [probs (probabilities 1
                             [(mk-arm :arm1 :value 1)
                              (mk-arm :arm2 :value 10)])]
    (is (= :arm1
           (->> probs first :name)))
    (is (= 1.2339457598623172E-4
           (->> probs first :p)))
    (is (= [1.2339457598623172E-4 0.9999999999999999]
           (map :cumulative-p probs)))))

(deftest selecting-arms
  (testing "before every arm"
    (is (= (mk-arm :arm1)
           (select-draw 1 1 [(mk-arm :arm1)
                             (mk-arm :arm2)])))
    (is (= (mk-arm :arm2)
           (select-draw 1 1 [(mk-arm :arm1 :pulls 1)
                             (mk-arm :arm2)]))))
  (testing "after every arm has been pulled"
    (let [arms [(mk-arm :arm1 :cumulative-p 0.1 :pulls 10)
                (mk-arm :arm2 :cumulative-p 0.9 :pulls 10)]]
      (is (= (mk-arm :arm1 :p 0.5 :cumulative-p 0.5 :pulls 10)
             (select-draw 1 0.05 arms)))
      (is (= (mk-arm :arm2 :p 0.5 :cumulative-p 1.0 :pulls 10)
             (select-draw 1 0.91 arms))))))

;; (deftest picking
;;   (is (= {:arm2 {:cum-p 1.9287498479639178E-22
;;                  :p 1.9287498479639178E-22
;;                  :reward 0
;;                  :n 11
;;                  :value 10}
;;           :arm3 {:cum-p 1.9287498479639178E-22
;;                  :p 3.7200759760208356E-44
;;                  :reward 0
;;                  :n 11
;;                  :value 5}
;;           :arm1 {:cum-p 1.0
;;                  :p 1.0
;;                  :reward 0
;;                  :n 12
;;                  :value 15}}
;;          (draw-probabilities 0.1 {:arm1 {:n 12 :reward 0 :value 15}
;;                                   :arm2 {:n 11 :reward 0 :value 10}
;;                                   :arm3 {:n 11 :reward 0 :value 5}})))
;;   (is (= {:arm1 {:cum-p 1.0
;;                  :p 1.0
;;                  :reward 0
;;                  :n 12
;;                  :value 15}}
;;          (make-draw 0.1 0.3 {:arm1 {:n 12 :reward 0 :value 15}
;;                              :arm2 {:n 11 :reward 0 :value 10}
;;                              :arm3 {:n 11 :reward 0 :value 5}})))
;;   (is (= {:arm1 {:n 0
;;                  :reward 0
;;                  :value 0}}
;;          (make-draw 0.1 0.3 {:arm1 {:n 0 :reward 0 :value 0}
;;                              :arm2 {:n 0 :reward 0 :value 0}
;;                              :arm3 {:n 0 :reward 0 :value 0}})))
;;   (is (= {:arm3 {:cum-p 1}}
;;          (select-draw 0.92 {:arm1 {:cum-p 0.28}
;;                             :arm2 {:cum-p 0.39}
;;                             :arm3 {:cum-p 1}})))
;;   (is (= {:arm2 {:cum-p 0.39}}
;;          (select-draw 0.3 {:arm1 {:cum-p 0.28}
;;                            :arm2 {:cum-p 0.39}
;;                            :arm3 {:cum-p 1}})))
;;   (is (= {:arm1 {:cum-p 0.28}}
;;          (select-draw 1.2 {:arm1 {:cum-p 0.28}
;;                            :arm2 {:cum-p 0.39}
;;                            :arm3 {:cum-p 1}}))))

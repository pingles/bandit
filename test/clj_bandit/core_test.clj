(ns clj-bandit.core-test
  (:use clojure.test
        clj-bandit.core))

(deftest breaking-maps
  (is (= '({:a 1} {:b 2})
         (individual-maps {:a 1 :b 2}))))

(deftest best-performing-arm
  (is (= {:lever1 {:value 1}}
         (best-performing {:lever1 {:value 1}}))
      (= {:lever1 {:value 1}}
         (best-performing {:lever1 {:value 1}
                           :lever2 {:value 0}}))))

(deftest calculating-arm-value
  (is (= 1
         (weighted-value 1 0 1)))
  (is (= 1
         (weighted-value 2 1 1)))
  (is (= 23/5
         (weighted-value 10 5 1))))

(deftest weighted-arm-values
  (is (= {:value 1 :n 2 :reward 1}
         (weighted-arm-value 1 {:n 1 :reward 0 :value 0})))
  (is (= {:value 0 :n 2 :reward 0}
         (weighted-arm-value 0 {:n 1 :reward 0 :value 0})))
  (is (= {:value 1 :n 2 :reward 1}
         (weighted-arm-value 1 {:n 1 :reward 0 :value 0})))
  (is (= {:value 1/2 :n 3 :reward 1}
         (weighted-arm-value 1 {:n 2 :reward 0 :value 0})))
  (is (= {:value 2/3 :n 4 :reward 2}
         (weighted-arm-value 1 {:n 3 :reward 1 :value 1/2}))))

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

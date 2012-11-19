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

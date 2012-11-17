(ns clj-bandit.epsilon-test
  (:use [clojure.test]
        [clj-bandit.core :only (arms update-reward)]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.epsilon]))

(deftest updating-lever-results
  (is (= {:lever1 {:n 4 :reward 2 :value 2/3}}
         (update-arms 1 :lever1 {:lever1 {:n 3 :reward 1 :value 1/2}})))
  (is (= {:lever1 {:n 4 :reward 1 :value 1/3}}
         (update-arms 0 :lever1 {:lever1 {:n 3 :reward 1 :value 1/2}}))))

(deftest weighted-value
  (is (= 1 (weighted-average-value 1)))
  (is (= 0
         (weighted-average-value 0 {:n 1 :reward 0 :value 0})))
  (is (= 1
         (weighted-average-value 1 {:n 1 :reward 0 :value 0})))
  (is (= 1/2
         (weighted-average-value 1 {:n 2 :reward 0 :value 0})))
  (is (= 2/3
         (weighted-average-value 1 {:n 3 :reward 1 :value 1/2}))))

(deftest epsilon-greedy-bandit
  (let [algo (epsilon-greedy-algorithm 0.2 (atom-storage #{:lever1 :lever2}))]
    (is (= {:lever1 {:reward 0 :n 0 :value 0}
            :lever2 {:reward 0 :n 0 :value 0}}
           (arms algo)))
    (update-reward algo :lever1 1)
    (is (= {:lever1 {:reward 1 :n 1 :value 1}
            :lever2 {:reward 0 :n 0 :value 0}}
           (arms algo)))))
(ns clj-bandit.core-test
  (:use clojure.test
        clj-bandit.core))

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

(deftest best-performing-arm
  (is (= {:lever1 {:value 1}}
         (best-performing {:lever1 {:value 1}}))
      (= {:lever1 {:value 1}}
         (best-performing {:lever1 {:value 1}
                           :lever2 {:value 0}}))))

(deftest updating-lever-results
  (is (= {:lever1 {:n 4 :reward 2 :value 2/3}}
         (update-levers 1 :lever1 {:lever1 {:n 3 :reward 1 :value 1/2}}))))


(deftest epsilon-greedy-bandit
  (let [bandit (epsilon-bandit 0.2 #{:lever1 :lever2})]
    (is (= {:lever1 {:reward 0 :n 0 :value 0}
            :lever2 {:reward 0 :n 0 :value 0}}
           (levers bandit)))
    (update-reward bandit :lever1 1)
    (is (= {:lever1 {:reward 1 :n 1 :value 1}
            :lever2 {:reward 0 :n 0 :value 0}}
           (levers bandit)))))
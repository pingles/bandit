(ns clj-bandit.epsilon-test
  (:use [clojure.test]
        [clj-bandit.core :only (arms update-reward)]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.epsilon]))

(deftest epsilon-greedy-bandit
  (let [bandit (epsilon-bandit 0.2 (atom-storage #{:lever1 :lever2}))]
    (is (= {:lever1 {:reward 0 :n 0 :value 0}
            :lever2 {:reward 0 :n 0 :value 0}}
           (arms bandit)))
    (update-reward bandit :lever1 1)
    (is (= {:lever1 {:reward 1 :n 1 :value 1}
            :lever2 {:reward 0 :n 0 :value 0}}
           (arms bandit)))))
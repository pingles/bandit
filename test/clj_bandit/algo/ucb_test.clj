(ns clj-bandit.algo.ucb_test
  (:use [clojure.test]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.arms :only (mk-arm best-performing)]
        [clj-bandit.algo.ucb] :reload))

(deftest picks-unpulled-arms
  (is (= [(mk-arm :arm1)]
         (unused-arms [(mk-arm :arm1) (mk-arm :arm2 :pulls 2)])))
  (is (nil? (first (unused-arms [(mk-arm :arm1 :pulls 1)])))))

(deftest finding-best-performing
  (is (= (mk-arm :arm2 :ucb-value 100)
         (best-performing :ucb-value [(mk-arm :arm1 :ucb-value 0)
                                      (mk-arm :arm2 :ucb-value 100)]))))

(deftest ucb-bonus-val
  (is (= 1.2389740629499462
         (bonus-value 10 3))))

(deftest conj-ucb-value
  (is (= 2.189929347170073
         (:ucb-value (ucb-value (mk-arm :arm1 :pulls 1)
                                [(mk-arm :arm1 :pulls 1)
                                 (mk-arm :arm1 :pulls 10)])))))

(deftest selecting-arm
  (is (= (mk-arm :arm1)
         (select-arm [(mk-arm :arm1)
                      (mk-arm :arm2 :value 10 :pulls 10)])))
  (is (= (mk-arm :arm2 :pulls 10 :value 10 :ucb-value 10.692516465190305)
         (select-arm [(mk-arm :arm1 :pulls 1 :value 1)
                      (mk-arm :arm2 :pulls 10 :value 10)]))))

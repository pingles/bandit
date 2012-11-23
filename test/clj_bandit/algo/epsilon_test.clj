(ns clj-bandit.algo.epsilon-test
  (:use [clojure.test]
        [clj-bandit.core :only (mk-arms arms update-reward)]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.algo.epsilon] :reload)
  (:require [clj-bandit.algo.epsilon :as e]))

(deftest epsilon-greedy-bandit
  (let [algo (epsilon-greedy-algorithm 0.2 (atom-storage (mk-arms #{:lever1 :lever2})))]
    (is (= {:lever1 {:reward 0 :n 0 :value 0}
            :lever2 {:reward 0 :n 0 :value 0}}
           (arms algo)))
    (update-reward algo :lever1 1)
    (is (= {:lever1 {:reward 1 :n 1 :value 1}
            :lever2 {:reward 0 :n 0 :value 0}}
           (arms algo)))))

(deftest best-performing-arm
  (is (= (e/mk-arm :arm2 :value 100)
         (best-performing [(e/mk-arm :arm1 :value 1)
                           (e/mk-arm :arm2 :value 100)]))))

(deftest calculating-total-pulls
  (is (= 101
         (total-pulls [(e/mk-arm :arm1 :n 1)
                       (e/mk-arm :arm2 :n 100)]))))

(deftest drawing-arms
  (testing "standard"
    (is (= (e/mk-arm :arm2 :value 2)
           (draw-arm 0.1 0.2 [(e/mk-arm :arm1 :value 1)
                              (e/mk-arm :arm2 :value 2)])))))

(deftest tracking-arm-reward
  (let [arm (e/mk-arm :arm1 :value 0 :n 0)]
    (is (= (e/mk-arm :arm1 :n 1 :value 0)
           (reward arm 0)))
    (is (= (e/mk-arm :arm1 :n 1 :value 1)
           (reward arm 1)))
    (is (= (e/mk-arm :arm1 :n 3 :value 1/2)
           (-> arm
               (reward 0)
               (reward 0)
               (reward 1))))))
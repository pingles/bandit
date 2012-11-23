(ns clj-bandit.algo.epsilon-test
  (:use [clojure.test]
        [clj-bandit.core :only (mk-arms arms update-reward)]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.algo.epsilon] :reload)
  (:require [clj-bandit.algo.epsilon :as e]))

(deftest best-performing-arm
  (is (= (e/mk-arm :arm2 :value 100)
         (best-performing [(e/mk-arm :arm1 :value 1)
                           (e/mk-arm :arm2 :value 100)]))))

(deftest calculating-total-pulls
  (is (= 101
         (total-pulls [(e/mk-arm :arm1 :pulls 1)
                       (e/mk-arm :arm2 :pulls 100)]))))

(deftest tracking-arm-reward
  (let [arm (e/mk-arm :arm1 :value 0 :pulls 0)]
    (is (= (e/mk-arm :arm1 :pulls 1 :value 0)
           (reward arm 0)))
    (is (= (e/mk-arm :arm1 :pulls 1 :value 1)
           (reward arm 1)))
    (is (= (e/mk-arm :arm1 :pulls 3 :value 1/2)
           (-> arm
               (reward 0)
               (reward 0)
               (reward 1))))))

(deftest updating-bandit-state
  (let [arms [(e/mk-arm :arm1)
              (e/mk-arm :arm2)]]
    (is (= [(e/mk-arm :arm1 :pulls 1)
            (e/mk-arm :arm2 :pulls 0)]
           (fold-arm (e/mk-arm :arm1 :pulls 1)
                     arms)))))

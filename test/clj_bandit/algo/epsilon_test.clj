(ns clj-bandit.algo.epsilon-test
  (:use [clojure.test]
        [clj-bandit.arms :only (mk-arm best-performing total-pulls reward fold-arm)]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.algo.epsilon] :reload))

(deftest best-performing-arm
  (is (= (mk-arm :arm2 :value 100)
         (best-performing :value [(mk-arm :arm1 :value 1)
                                  (mk-arm :arm2 :value 100)]))))

(deftest calculating-total-pulls
  (is (= 101
         (total-pulls [(mk-arm :arm1 :pulls 1)
                       (mk-arm :arm2 :pulls 100)]))))

(deftest tracking-arm-reward
  (let [arm (mk-arm :arm1 :value 0 :pulls 0)]
    (is (= (mk-arm :arm1 :pulls 1 :value 0)
           (reward arm 0)))
    (is (= (mk-arm :arm1 :pulls 1 :value 1)
           (reward arm 1)))
    (is (= (mk-arm :arm1 :pulls 3 :value 1/2)
           (-> arm
               (reward 0)
               (reward 0)
               (reward 1))))))

(deftest updating-bandit-state
  (let [arms [(mk-arm :arm1)
              (mk-arm :arm2)]]
    (is (= [(mk-arm :arm1 :pulls 1)
            (mk-arm :arm2 :pulls 0)]
           (fold-arm (mk-arm :arm1 :pulls 1)
                     arms)))))

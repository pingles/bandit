(ns clj-bandit.ucb_test
  (:use [clojure.test]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.core :only (select-arm update-reward)]
        [clj-bandit.ucb] :reload))

(deftest picks-unpulled-arms
  (is (= {:arm1 {:n 0}}
         (unused-arm {:arm1 {:n 0}
                      :arm2 {:n 1}})))
  (is (nil? (unused-arm {:arm1 {:n 1}}))))

(deftest number-pulls-across-arms
  (is (= 1
         (total-pulls {:arm1 {:n 1}}))))

(deftest arm-bonus
  (is (= 1.2389740629499462
         (bonus-value 10 3)))
  (is (= {:arm1 {:ucb-value 4.794122577994101
                 :n 1
                 :value 3}
          :arm2 {:ucb-value 1.8970612889970506
                 :n 4
                 :value 1}}
         (bonus {:arm1 {:n 1 :value 3}
                 :arm2 {:n 4 :value 1}}))))

(deftest arm-selection
  (is (= {:arm1 {:ucb-value 9}}
         (best-performing {:arm1 {:ucb-value 9}
                           :arm2 {:ucb-value 1}}))))

(deftest arm-feedback
  (is (= {:arm1 {:value 1 :n 2 :reward 2}}
         (update-arms 1 :arm1 {:arm1 {:n 1 :reward 1 :value 2}}))))

(deftest algorithm
  (let [algo (ucb-algorithm (atom-storage (mk-arms #{:arm1 :arm2})))]
    (is (= {:arm1 {:reward 0, :n 0, :value 0}}
           (select-arm algo)))
    (is (= {:arm1 {:value 1
                   :n 1
                   :reward 1}
            :arm2 {:reward 0
                   :n 0
                   :value 0}}
           (update-reward algo :arm1 1)))))

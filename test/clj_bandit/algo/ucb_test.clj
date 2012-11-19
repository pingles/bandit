(ns clj-bandit.algo.ucb_test
  (:use [clojure.test]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.core :only (select-arm update-reward arms best-performing)]
        [clj-bandit.algo.ucb] :reload))

(deftest picks-unpulled-arms
  (is (= '({:arm2 {:n 0}} {:arm1 {:n 0}})
         (unused-arms {:arm1 {:n 0}
                       :arm2 {:n 0}
                       :arm3 {:n 1}})))
  (is (= {:arm1 {:n 0}}
         (first-unused-arm {:arm1 {:n 0}
                            :arm2 {:n 1}})))
  (is (nil? (first-unused-arm {:arm1 {:n 1}}))))

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
         (ucb-value {:arm1 {:n 1 :value 3}
                     :arm2 {:n 4 :value 1}}))))

(deftest arm-selection
  (is (= {:arm1 {:ucb-value 9}}
         (best-performing :ucb-value {:arm1 {:ucb-value 9}
                                      :arm2 {:ucb-value 1}})))
  (is (= {:arm2 {:ucb-value 3.467599010262171
                 :reward 1
                 :n 1
                 :value 1}}
         (pick-arm {:arm1 {:value 1N
                           :n 20
                           :reward 20}
                    :arm2 {:value 1
                           :n 1
                           :reward 1}}))))

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
           (update-reward algo :arm1 1)))

    (update-reward algo :arm2 1)
    (doseq [x (range 1 20)]
      (update-reward algo :arm1 1))
    (is (= {:arm1 {:value 1N
                   :n 20
                   :reward 20}
            :arm2 {:value 1
                   :n 1
                   :reward 1}}
           (arms algo)))
    (is (= {:arm2 {:ucb-value 3.467599010262171
                   :value 1
                   :n 1
                   :reward 1}}
           (select-arm algo)))))

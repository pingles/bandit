(ns clj-bandit.test.softmax
  (use [clojure.test]
       [clj-bandit.core :only (select-arm)]
       [clj-bandit.storage :only (atom-storage)]
       [clj-bandit.softmax] :reload))

(deftest calculating-arm-value
  (is (= {:n 1 :reward 1 :value 1}
         (arm-value 1 {:n 0 :reward 0 :value 0})))
  (is (= {:n 2 :reward 2 :value 1}
         (arm-value 1 {:n 1 :reward 1 :value 0})))
  (is (= {:n 3 :reward 3 :value 3/2}
         (arm-value 1 {:n 2 :reward 2 :value 1}))))

(deftest picking-items
  (is (= {:arm1 {:cum-p 1.0, :p 0.9999546021312976, :reward 2, :n 2, :value 1}}
         (categorical-draw 0.1 {:arm1 {:n 2 :reward 2 :value 1}
                                :arm2 {:n 3 :reward 0 :value 0}})))
  (let [algo (softmax-algorithm 0.1 (atom-storage (mk-arms #{:arm1})))]
    (is (= {:arm1 {:cum-p 1.0, :p 1.0, :reward 0, :n 0, :value 0}}
           (select-arm algo)))))

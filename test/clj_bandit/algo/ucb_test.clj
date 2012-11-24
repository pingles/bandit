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

;; (deftest arm-bonus
;;   (is (= 1.2389740629499462
;;          (bonus-value 10 3)))
;;   (is (= {:arm1 {:ucb-value 4.794122577994101
;;                  :n 1
;;                  :value 3}
;;           :arm2 {:ucb-value 1.8970612889970506
;;                  :n 4
;;                  :value 1}}
;;          (ucb-value {:arm1 {:n 1 :value 3}
;;                      :arm2 {:n 4 :value 1}}))))

;; (deftest arm-selection
;;   (is (= {:arm1 {:ucb-value 9}}
;;          (best-performing :ucb-value {:arm1 {:ucb-value 9}
;;                                       :arm2 {:ucb-value 1}})))
;;   (is (= {:arm2 {:ucb-value 3.467599010262171
;;                  :reward 1
;;                  :n 1
;;                  :value 1}}
;;          (pick-arm {:arm1 {:value 1N
;;                            :n 20
;;                            :reward 20}
;;                     :arm2 {:value 1
;;                            :n 1
;;                            :reward 1}}))))

;; (deftest algorithm
;;   (let [algo (ucb-algorithm (atom-storage (mk-arms #{:arm1 :arm2})))]
;;     (is (= {:arm1 {:reward 0, :n 0, :value 0}}
;;            (select-arm algo)))
;;     (is (= {:arm1 {:value 1
;;                    :n 1
;;                    :reward 1}
;;             :arm2 {:reward 0
;;                    :n 0
;;                    :value 0}}
;;            (update-reward algo :arm1 1)))

;;     (update-reward algo :arm2 1)
;;     (doseq [x (range 1 20)]
;;       (update-reward algo :arm1 1))
;;     (is (= {:arm1 {:value 1N
;;                    :n 20
;;                    :reward 20}
;;             :arm2 {:value 1
;;                    :n 1
;;                    :reward 1}}
;;            (arms algo)))
;;     (is (= {:arm2 {:ucb-value 3.467599010262171
;;                    :value 1
;;                    :n 1
;;                    :reward 1}}
;;            (select-arm algo)))))

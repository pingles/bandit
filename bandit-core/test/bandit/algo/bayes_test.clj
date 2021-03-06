(ns bandit.algo.bayes-test
  (:use [expectations]
        [bandit.algo.bayes])
  (:require [bandit.arms :as arms]))

(let [a (arms/arm :arm1)]
  (given a
         (expect :pulls 0
                 :value 0))
  (given (estimate-value (arms/pulled a))
         (expect :pulls 1
                 :value 0
                 :alpha 1.0
                 :beta  2.0))
  (given (estimate-value (-> a (arms/pulled) (reward 1)))
         (expect :pulls 1
                 :value 1
                 :alpha 2.0
                 :beta  1.0)))

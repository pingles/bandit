(ns clj-bandit.arms_test
  (:use [expectations]
        [clj-bandit.arms]))

;; folding arm results
(expect [(mk-arm :arm1 :pulls 1) (mk-arm :arm2)]
        (fold-arm (mk-arm :arm1 :pulls 1)
                  [(mk-arm :arm1) (mk-arm :arm2)]))


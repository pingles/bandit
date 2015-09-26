(ns bandit.context-test
  (:use [bandit.context]
        [expectations])
  (:require [bandit.arms :as arms]
            [clojure.core.matrix :as m]))

(expect {"test" 1
         "dictionary" 0}
        (initialise-dictionary (tokenise "test dictionary")))

(expect m/vec? (feature-vector {"test" 0} "test"))
(expect m/vec? (feature-vector {"test" 0} ""))

(expect (m/matrix [1.0])     (feature-vector {"test" 0} ["test" "blah"]))
(expect (m/matrix [1.0 1.0]) (feature-vector {"test" 1
                                              "blah" 0} ["test" "blah"]))

(let [d (initialise-dictionary (tokenise "test dictionary"))
      a (arm d :name)]
  (expect (m/matrix [0.0 0.0]) (:pulls a))
  (expect (m/matrix [0.0 0.0]) (:value a))
  (expect (m/matrix [0.0 1.0])
          (:pulls (pulled a (feature-vector d (tokenise "test")))))
  (expect (m/matrix [0.0 1.0])
          (:value (reward a (feature-vector d (tokenise "test"))))))

;; updating the dictionary

(expect {"hello" 0} (add-word {} "hello"))
(expect {"hello" 0
         "world" 1}
        (add-word {"hello" 0} "world"))


;; introduce protocol for vector value- will be the sum
;; or perhaps the sum / n-elements etc.
;; or, for numbers, just the value itself
;; or just use an (arm-value :pulls) multimethod?
;; and have standardised (pulls/value fns in arms)?


;; TODO
;; return number of arm pulls (given context)
;; return value of arm (given context)

;; use bayes algo to estimate value from above

;; updating the dictionary with new terms
;;  - will need to update dictionary _before_ any
;;    arm operations (pulled/reward etc.)
;; updating the arm as dictionary grows
;;  - this should just happen automatically once
;;    the dictionary has been updated.

(ns clj-bandit.simulate
  (:import [java.util UUID])
  (:use [clojure.string :only (join)]
        [clojure.java.io :only (writer)]))

(set! *warn-on-reflection* true)

(defn bernoulli-arm [p] (fn [] (if (> (rand) p) 0 1)))

(defn draw-arm [f] (f))

(defn mk-bernoulli-bandit
  "creates the simulation bandit: a vector of arms that reward with fixed probability."
  [& p]
  (map bernoulli-arm p))

(defn simulate
  "runs a simulation. bandit is a sequence of arms (functions) that
   return their numerical reward value."
  [bandit])
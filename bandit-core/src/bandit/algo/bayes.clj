(ns ^{:doc "Bayesian Bandit algorithm"
      :author "Paul Ingles"}
  bandit.algo.bayes
  (:use [bandit.arms :only (exploit)]
        [incanter.distributions :only (beta-distribution draw)]))

;; Clojure implementation of Bayesian bandit algorithm as
;; implemented here: http://www.chrisstucchio.com/blog/2013/bayesian_bandit.html
;; by Chris Stucchio.

(defn estimate-value
  [{:keys [pulls value prior] :as arm}]
  (let [pr (or prior 1.0)
        alpha (+ pr value)
        beta  (+ pr (- pulls value))
        dist  (beta-distribution alpha beta)]
    (assoc arm
      :theta (draw dist)
      :alpha alpha
      :beta beta)))

(defn select-arm
  "Returns the arm that should be pulled. Arm records should have
   a :prior value (assumed to be 1.0 if no prior set)."
  [arms]
  (exploit :theta (map estimate-value arms)))

(defn reward
  [arm reward]
  (update-in arm [:value] (partial + reward)))

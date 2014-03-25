(ns ^{:doc "Rank items using a bandit. This could be news articles, search result items, products etc."}
  bandit.ring.rank
  (:require [bandit.arms :as arms]
            [bandit.algo.bayes :as bayes]))

(comment
  (def arms (arms/mk-arms :product1 :product2 :product3 :product4 :product5))

  ;; instead of just exploiting the best performing, we'll estimate
  ;; the value of all arms and rank (desc) by their theta.
  (defn rank [arms]
    (reverse (sort-by :theta
                      (map bayes/estimate-value
                           (vals arms)))))

  ;; given we're showing all products at once we'll be recording
  ;; pulls against all arms simultaneously
  (defn map-vals [m f]
    (into {} (for [[k v] m] [k (f v)])))

  (defn pull-all-arms
    "Records a pull against every arm"
    [arms]
    (map-vals arms arms/pulled))

  ;; tracking rewarded arms remains the same
  )

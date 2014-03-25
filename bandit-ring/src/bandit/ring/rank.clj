(ns ^{:doc "Rank items using a bandit. This could be news articles, search result items, products etc."}
  bandit.ring.rank
  (:require [bandit.arms :as arms]
            [bandit.algo.bayes :as bayes]
            [hiccup.core :as hic]))

(defonce bandit (ref (arms/mk-arms :item1 :item2 :item3 :item4 :item5)))

(defn rank
  "Rather than exploiting the maximum theta value item, instead we estimate
   the value of all arms and sort in descending order of value."
  [arms]
  (reverse (sort-by :theta
                    (map bayes/estimate-value
                         arms))))

(defn bandit-state
  [bandit]
  [:div#arms
   [:h2 "Bandit State"]
   [:table
    [:thead
     [:tr
      [:th "Arm"]
      [:th "Pulls"]
      [:th "Current Value"]]]
    [:tbody
     (for [{:keys [name pulls value]} (vals bandit)]
       [:tr
        [:td name]
        [:td pulls]
        [:td value]])]]])


(defn map-vals [m f]
  (into {} (for [[k v] m] [k (f v)])))

(defn pull-all-arms
  "Records a pull against every arm"
  [arms]
  (map-vals arms arms/pulled))

(defn items-html
  []
  (dosync
   (alter bandit pull-all-arms)
   (hic/html [:ul
              (for [{:keys [name]} (rank (vals @bandit))]
                [:li name])]
             (bandit-state @bandit))))

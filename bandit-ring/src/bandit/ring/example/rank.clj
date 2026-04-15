(ns ^{:doc "Rank items using a bandit. This could be news articles, search result items, products etc."}
  bandit.ring.example.rank
  (:use [ring.util.response :only (redirect response status)]
        [compojure.core])
  (:require [bandit.arms :as arms]
            [bandit.algo.bayes :as bayes]
            [bandit.ring.page :as page]
            [hiccup.core :as hic]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(defonce bandit (ref (arms/bandit :item1 :item2 :item3 :item4 :item5)))
(def valid-arms #{:item1 :item2 :item3 :item4 :item5})

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
   (hic/html
    [:div#explanation
     [:p "In this example we use the bandit to optimise the rank of items in a list; items with more clicks will rank higher in the list. As with the advertisement example, each item is represented as an arm on the bandit. However, rather than showing just the highest-performing arm we estimate the value of all arms and rank items according to their value."]]
    [:div#items
     [:ul
      (for [{:keys [name]} (arms/rank :theta (map bayes/estimate-value (vals @bandit)))]
        [:li
         [:form {:action (str "/rank/click/" (clojure.core/name name))
                 :method "POST"}
          (anti-forgery-field)
          [:button {:type "submit"} name]]])]]
    (page/bandit-state @bandit))))

(defn record-click
  [arm-state arm-name]
  (update-in arm-state [arm-name] bayes/reward 1))

(defn parse-arm-name
  [arm-name]
  (let [arm (keyword arm-name)]
    (when (contains? valid-arms arm)
      arm)))


(defroutes rank-example-routes
  (GET "/rank" []
       (page/layout "Ranking items"
                    [:div#main (items-html)]))
  (POST "/rank/click/:arm-name" [arm-name]
       (if-let [arm (parse-arm-name arm-name)]
         (do
           (dosync (alter bandit record-click arm))
           (redirect "/rank"))
         (-> (response "Invalid arm")
             (status 400)))))

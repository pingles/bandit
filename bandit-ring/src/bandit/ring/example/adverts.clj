(ns ^{:doc "Advertisement optimisation example"}
  bandit.ring.example.adverts
  (:use [compojure.core]
        [ring.util.response :only (redirect)])
  (:require [bandit.arms :as arms]
            [bandit.algo.bayes :as bayes]
            [bandit.ring.page :as page]
            [hiccup.core :as hic]))

(defonce bandit (ref (arms/bandit :advert1 :advert2 :advert3)))

(defmulti advertisement :name)
(defmethod advertisement :advert1
  [_]
  [:div.advert
   [:h3 "Advert 1"]
   [:a {:href "/ads/click/advert1"} "Buy Now"]])
(defmethod advertisement :advert2
  [_]
  [:div.advert
   [:h3 "Advert 2"]
   [:a {:href "/ads/click/advert2"} "More Info"]])
(defmethod advertisement :advert3
  [_]
  [:div.advert
   [:h3 "Advert 3"]
   [:a {:href "/ads/click/advert3"} "Apply Now"]])

(defn record-pull
  [arm-state {:keys [name] :as arm}]
  (update-in arm-state [name] arms/pulled))

(defn record-click
  [arm-state arm-name]
  (update-in arm-state [arm-name] bayes/reward 1))

(defn advert-html
  "Uses the bandit algorithm to optimise which advert to show
   and returns it's HTML."
  []
  (dosync
   (let [pulled (bayes/select-arm (vals @bandit))]
     (alter bandit record-pull pulled)
     (hic/html (advertisement pulled)
               (page/bandit-state @bandit)))))


(defroutes advert-example-routes
  (GET "/ads" []
       (page/layout "Advertisement Click-through"
                    [:div#explanation
                     [:p "This example demonstrates using a Bayesian algorithm to optimise advert click-throughs. There are 3 different adverts (with 3 different calls-to-action). The problem is modeled by using each arm to represent each advert. As you click on adverts the algorithm will tend towards picking that advert."]]
                    [:div#main
                     (advert-html)]))
  (GET "/ads/click/:arm-name" [arm-name]
       (dosync (alter bandit record-click (keyword arm-name)))
       (redirect "/ads")))

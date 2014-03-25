(ns ^{:doc "Advertisement optimisation example"}
  bandit.ring.adverts
  (:require [bandit.arms :as arms]
            [bandit.algo.ucb :as ucb]
            [hiccup.core :as hic]))

(defonce bandit (ref (arms/mk-arms :advert1 :advert2 :advert3)))

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
  (update-in arm-state [arm-name] arms/reward 1))

(defn bandit-perf
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

(defn advert-html
  "Uses the bandit algorithm to optimise which advert to show
   and returns it's HTML."
  []
  (dosync
   (let [pulled (ucb/select-arm (vals @bandit))]
     (alter bandit record-pull pulled)
     (hic/html (advertisement pulled)
               (bandit-perf @bandit)))))

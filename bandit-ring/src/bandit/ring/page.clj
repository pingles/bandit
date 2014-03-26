(ns bandit.ring.page
  (:require [hiccup.core :as hic]
            [hiccup.page :as page]))

(defn layout
  [title & body]
  (page/html5
   [:head
    [:title title]
    (page/include-css "/css/app.css")]
   [:body
    [:h1 title]
    body]))


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

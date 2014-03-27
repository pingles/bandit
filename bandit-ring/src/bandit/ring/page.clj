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
    [:div#container
     [:h1 "Multi-armed Bandits in Clojure"]
     [:h2 title]
     body
     [:div#footer
      [:p "Example application for the Clojure "
       [:a {:href "https://github.com/pingles/bandit"} "bandit"]
       " library, created by Paul Ingles "
       [:a {:href "http://twitter.com/pingles"} "@pingles"]]]]]))


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

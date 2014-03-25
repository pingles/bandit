(ns bandit.ring.app
  (:use [compojure.core]
        [ring.middleware stacktrace reload]
        [ring.util.response]
        [ring.adapter.jetty :only (run-jetty)])
  (:require [bandit.ring.adverts :as ads]
            [hiccup.page :as page]))

(defn layout
  [title & body]
  (page/html5
   [:head [:title title]]
   [:body
    [:h1 title]
    body]))

(defroutes main-routes
  (GET "/" []
       (layout "Bandit Examples"
               [:div#main
                [:ul
                 [:li
                  [:a {:href "/ads"} "Adverts example"]]]]))
  (GET "/ads" []
       (layout "Advertisement Click-through"
               [:div#main
                (ads/advert-html)]))
  (GET "/ads/click/:arm-name" [arm-name]
       (dosync
        (alter ads/bandit ads/record-click (keyword arm-name)))
       (redirect "/ads")))

(def app (-> main-routes
             (wrap-reload '(bandit.ring app adverts))
             (wrap-stacktrace)))

(defn -main
  []
  (run-jetty #'app {:port 8080}))

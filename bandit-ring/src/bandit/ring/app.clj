(ns bandit.ring.app
  (:use [compojure.core]
        [ring.middleware stacktrace reload]
        [ring.util.response]
        [ring.adapter.jetty :only (run-jetty)])
  (:require [bandit.ring.adverts :as ads]
            [hiccup.page :as page]))

(defn layout
  [& body]
  (page/html5
   [:head [:title "bandit sample"]]
   [:body
    [:h1 "Bandit Examples"]
    body]))

(defroutes main-routes
  (GET "/" []
       (layout
        [:div#main
         [:ul
          [:li
           [:a {:href "/ads"} "Adverts example"]]]]))
  (GET "/ads" []
       (layout
        [:div#main
         (ads/advert-html)]))
  (GET "/ads/click/:arm-name" [arm-name]
       (dosync
        (alter ads/bandit ads/record-click (keyword arm-name)))
       (redirect "/ads")))

(def app (-> main-routes
             (wrap-reload '(bandit.ring example-app))
             (wrap-stacktrace)))

(defn -main
  []
  (run-jetty #'app {:port 8080}))

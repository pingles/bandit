(ns bandit.ring.app
  (:use [compojure.core]
        [ring.middleware stacktrace reload]
        [ring.util.response]
        [ring.adapter.jetty :only (run-jetty)])
  (:require [bandit.ring.adverts :as ads]
            [bandit.ring.rank :as rank]
            [bandit.ring.page :as page]))

(defroutes main-routes
  (GET "/" []
       (page/layout "Bandit Examples"
                    [:div#main
                     [:ul
                      [:li
                       [:a {:href "/ads"} "Adverts example"]]
                      [:li
                       [:a {:href "/rank"} "Ranking items example"]]]])))

(def app (-> (routes main-routes ads/advert-example-routes rank/rank-example-routes)
             (wrap-reload '(bandit.ring app adverts rank))
             (wrap-stacktrace)))

(defn -main
  []
  (run-jetty #'app {:port 8080}))

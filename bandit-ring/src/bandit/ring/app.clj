(ns bandit.ring.app
  (:use [compojure.core]
        [ring.middleware stacktrace reload cookies]
        [ring.util.response]
        [ring.adapter.jetty :only (run-jetty)]
        [ring.middleware.resource :only (wrap-resource)])
  (:require [bandit.ring.example.adverts :as ads]
            [bandit.ring.example.rank :as rank]
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

(defn wrap-user-cookie
  [handler]
  (fn [{:keys [cookies] :as request}]
    (let [resp (handler request)]
      (if (get cookies "userid")
        resp
        (-> resp
            (set-cookie "userid" (.toString (java.util.UUID/randomUUID))))))))

(def app (-> (routes main-routes ads/advert-example-routes rank/rank-example-routes)
             (wrap-reload '(bandit.ring app adverts rank))
             (wrap-stacktrace)
             (wrap-resource "public")
             (wrap-user-cookie)
             (wrap-cookies)))

(defn -main
  [port]
  (run-jetty #'app {:port (Integer. port)}))

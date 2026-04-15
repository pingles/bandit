(ns bandit.ring.app
  (:use [compojure.core]
        [ring.middleware stacktrace reload cookies]
        [ring.middleware.anti-forgery :only (wrap-anti-forgery)]
        [ring.util.response]
        [ring.adapter.jetty :only (run-jetty)]
        [ring.middleware.resource :only (wrap-resource)])
  (:require [bandit.ring.example.adverts :as ads]
            [bandit.ring.example.rank :as rank]
            [bandit.ring.page :as page]))

(defroutes main-routes
  (GET "/" []
       (page/layout "Example applications"
                    [:div#main
                     [:p "This application shows example applications of Multi-armed Bandit optimisation algorithms. The source code, implemented in Clojure, for the library and this application are available at "
                      [:a {:href "https://github.com/pingles/bandit"} "https://github.com/pingles/bandit"]]
                     [:p [:ul
                          [:li
                           [:a {:href "/ads"} "Adverts example"]]
                          [:li
                           [:a {:href "/rank"} "Ranking items example"]]]]])))

(defn wrap-user-cookie
  [handler]
  (fn [{:keys [cookies] :as request}]
    (let [resp (handler request)]
      (if (get cookies "userid")
        resp
        (-> resp
            (set-cookie "userid"
                        (.toString (java.util.UUID/randomUUID))
                        {:http-only true
                         :same-site :lax
                         :secure (= :https (:scheme request))
                         :path "/"}))))))

(defn wrap-dev-middleware
  [handler]
  (if (= "dev" (System/getenv "BANDIT_ENV"))
    (-> handler
        (wrap-reload '(bandit.ring app adverts rank))
        (wrap-stacktrace))
    handler))

(def app (-> (routes main-routes ads/advert-example-routes rank/rank-example-routes)
             (wrap-dev-middleware)
             (wrap-resource "public")
             (wrap-anti-forgery)
             (wrap-user-cookie)
             (wrap-cookies)))

(defn -main
  [port]
  (run-jetty #'app {:port (Integer. port)}))

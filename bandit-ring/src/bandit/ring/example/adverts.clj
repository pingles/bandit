(ns ^{:doc "Advertisement optimisation example"}
  bandit.ring.example.adverts
  (:use [compojure.core]
        [ring.util.response :only (redirect response status)])
  (:require [bandit.arms :as arms]
            [bandit.algo.bayes :as bayes]
            [bandit.ring.page :as page]
            [hiccup.core :as hic]
            [ring.util.anti-forgery :refer [anti-forgery-field]]))

(def advert-catalog
  [[:advert1 "Advert 1" "Buy Now"]
   [:advert2 "Advert 2" "More Info"]
   [:advert3 "Advert 3" "Apply Now"]])

(def advert-by-name
  (into {} (for [[arm-name title cta] advert-catalog]
             [arm-name {:title title :cta cta}])))

(def valid-arms (set (keys advert-by-name)))
(defonce bandit (ref (apply arms/bandit (map first advert-catalog))))

(defn advertisement
  [{:keys [name]}]
  (let [{:keys [title cta]} (get advert-by-name name)
        arm-name (clojure.core/name name)]
    [:div.advert
     [:h3 title]
     [:form {:action (str "/ads/click/" arm-name) :method "POST"}
      (anti-forgery-field)
      [:button {:type "submit"} cta]]]))

(defn record-pull
  [arm-state {:keys [name] :as arm}]
  (update-in arm-state [name] arms/pulled))

(defn record-click
  [arm-state arm-name]
  (update-in arm-state [arm-name] bayes/reward 1))

(defn parse-arm-name
  [arm-name]
  (let [arm (keyword arm-name)]
    (when (contains? valid-arms arm)
      arm)))

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
  (POST "/ads/click/:arm-name" [arm-name]
       (if-let [arm (parse-arm-name arm-name)]
         (do
           (dosync (alter bandit record-click arm))
           (redirect "/ads"))
         (-> (response "Invalid arm")
             (status 400)))))

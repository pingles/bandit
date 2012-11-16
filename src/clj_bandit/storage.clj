(ns clj-bandit.storage
  (:use [clj-bandit.core :only (mk-arms)]))

(defprotocol BanditStorage
  (get-arms [storage])
  (put-arms [storage f]))

(defn atom-storage
  [labels]
  (let [arms (atom (mk-arms labels))]
    (reify BanditStorage
      (get-arms [_]
        @arms)
      (put-arms [_ f]
        (swap! arms f)))))
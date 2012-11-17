(ns clj-bandit.storage)

(defprotocol BanditStorage
  (get-arms [storage])
  (put-arms [storage f]))

(defn atom-storage
  [state]
  (let [arms (atom state)]
    (reify BanditStorage
      (get-arms [_]
        @arms)
      (put-arms [_ f]
        (swap! arms f)))))
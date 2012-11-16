(ns clj-bandit.epsilon
  (:use [clj-bandit.core :only (Bandit atom-storage get-arms put-arms best-performing update-arms)]))

(defn epsilon-bandit
  "Returns an Epsilon-Greedy bandit with the specified lever labels. uses atom storage"
  [epsilon labels]
  (let [storage (atom-storage labels)]
    (reify Bandit
      (select-arm [_]
        (if (> (rand) epsilon)
          {:strategy :exploit
           :arm (best-performing (get-arms storage))}
          {:strategy :explore
           :arm (let [arms (get-arms storage)
                      label (rand-nth (keys arms))]
                  {label (get arms label)})}))
      (update-reward [_ arm reward]
        (put-arms storage #(update-arms reward arm %)))
      (arms [_]
        (get-arms storage)))))

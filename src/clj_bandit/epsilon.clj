(ns clj-bandit.epsilon
  (:use [clj-bandit.core :only (Bandit atom-storage get-levers put-levers best-performing update-levers)]))

(defn epsilon-bandit
  "Returns an Epsilon-Greedy bandit with the specified lever labels. uses atom storage"
  [epsilon labels]
  (let [storage (atom-storage labels)]
    (reify Bandit
      (select-arm [_]
        (if (> (rand) epsilon)
          {:strategy :exploit
           :arm (best-performing (get-levers storage))}
          {:strategy :explore
           :arm (let [levers (get-levers storage)
                      label (rand-nth (keys levers))]
                  {label (get levers label)})}))
      (update-reward [_ arm reward]
        (put-levers storage #(update-levers reward arm %)))
      (levers [_]
        (get-levers storage)))))

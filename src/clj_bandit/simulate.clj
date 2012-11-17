(ns clj-bandit.simulate
  (:use [clojure.data.csv :only (write-csv)]
        [clojure.java.io :only (writer)]
        [clj-bandit.epsilon]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.core :only (select-arm update-reward)]))

(defn bernoulli-arm [p] (fn [] (if (> (rand) p) 0 1)))

(defn draw-arm [f] (f))

;; take 5 results from a sequence of results for a bernoulli arm
;; (take 5 (repeatedly #(draw-arm (bernoulli-arm 0.1))))

(defn arm-name
  [pull]
  (first (keys (:arm pull))))

(comment
  (let [simulation-seq (repeatedly #(map draw-arm arms))]
    (take 10 simulation-seq)))

(defn cumulative-sum
  [coll]
  (reduce 
   (fn [v, x] (conj v (+ (last v) x))) 
   [(first coll)] 
   (rest coll)))

(defn simulation-results
  [epsilon iterations simulation-number]
  (let [arm-labels [:arm1 :arm2 :arm3 :arm4 :arm5]
        arms (map bernoulli-arm [0.1 0.1 0.1 0.1 0.9])
        algo (epsilon-greedy-algorithm epsilon (atom-storage arm-labels))]
    (let [rows (map (fn [t]
                      (let [chosen-arm (arm-name (select-arm algo))
                            reward (draw-arm (nth arms (.indexOf arm-labels chosen-arm)))
                            cumulative-reward 0]
                        (update-reward algo chosen-arm reward)
                        [epsilon simulation-number t chosen-arm reward]))
                    (range 1 iterations))
          cumulative-rewards (cumulative-sum (map last rows))]
      (map conj rows cumulative-rewards))))

(defn run-simulation
  "simulations: fixed number of sims to run to cancel out noise within individual tests.
   horizon: number of times algorithm can pull on arms during each simulation. measure how well the algorithm does with 1 try, 100 tries etc."
  ([]
     (run-simulation 1000 200))
  ([simulations iterations]
     (with-open [csv (writer "tmp/results.csv")]
       (let [epsilon-values [0.1 0.2 0.3 0.4 0.5]]
         (write-csv csv (apply concat (pmap (fn [epsilon]
                                              (apply concat (pmap (partial simulation-results epsilon iterations)
                                                                  (range 1 simulations))))
                                            epsilon-values)))))))

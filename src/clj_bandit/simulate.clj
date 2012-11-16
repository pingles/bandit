(ns clj-bandit.simulate
  (:use [clojure.data.csv :only (write-csv)]
        [clojure.java.io :only (writer)]))

(defn bernoulli-arm
  "returns a function mimicking a bandit arm with a set probability. each function call represents the pulling of a bandit arm. can be used to simulate results. 0 < p < 1. p indicates probability of being rewarded (with 1).

   e.g (def draw-arm (bernoulli-arm 0.1)) ; 10% payout
   (repeatedly 10 draw-arm)"
  [p]
  (fn []
    (if (> (rand) p)
      0
      1)))

(defn draw-arm
  [f]
  (f))

;; take 5 results from a sequence of results for a bernoulli arm
;; (take 5 (repeatedly #(draw-arm (bernoulli-arm 0.1))))

(defn arm-name
  [pull]
  (first (keys (:arm pull))))


(comment
  (let [simulation-seq (repeatedly #(map draw-arm arms))]
    (take 10 simulation-seq)))

(defn run-simulation
  "simulations: fixed number of sims to run to cancel out noise within individual tests.
   horizon: number of times algorithm can pull on arms during each simulation. measure how well the algorithm does with 1 try, 100 tries etc."
  ([]
     (run-simulation 5000 250))
  ([simulations horizon]
     (with-open [csv (writer "tmp/results.csv")]
       (write-csv csv [["hello" "there"]
                       ["paul" "test"]]))))
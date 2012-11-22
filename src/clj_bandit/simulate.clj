(ns clj-bandit.simulate
  (:use [clojure.string :only (join)]
        [clojure.java.io :only (writer)]
        [clj-bandit.algo.epsilon :only (epsilon-greedy-algorithm)]
        [clj-bandit.algo.softmax :only (softmax-algorithm)]
        [clj-bandit.algo.ucb :only [ucb-algorithm]]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.core :only (mk-arms select-arm update-reward cumulative-sum anneal)]))

(set! *warn-on-reflection* true)

(defn bernoulli-arm [p] (fn [] (if (> (rand) p) 0 1)))

(defn draw-arm [f] (f))

;; take 5 results from a sequence of results for a bernoulli arm
;; (take 5 (repeatedly #(draw-arm (bernoulli-arm 0.1))))

(defn arm-name
  [pull]
  (first (keys pull)))

(defn mk-bernoulli-bandit
  "creates the simulation bandit: a vector of arms that reward with fixed probability."
  [& p]
  (map bernoulli-arm p))

(defn simulation-results
  [arms arm-labels {:keys [algo-name algo-fn variant parameter] :as algorithm} horizon simulation-number]
  (let [algo (algo-fn)
        rows (map (fn [t]
                    (let [chosen-arm (arm-name (select-arm algo))
                          reward (draw-arm (nth arms (.indexOf ^clojure.lang.LazySeq arm-labels chosen-arm)))
                          cumulative-reward 0]
                      (update-reward algo chosen-arm reward)
                      [algo-name variant parameter simulation-number t chosen-arm reward]))
                  (range 1 horizon))
        cumulative-rewards (cumulative-sum (map last rows))]
    (map conj rows cumulative-rewards)))

(defn mk-arm-labels [n]
  (map #(keyword (str "arm" %)) (range n)))

(defn mk-storage
  [n]
  (atom-storage (mk-arms (mk-arm-labels n))))

(defn mk-epsilon-algorithm
  [n type epsilon]
  {:algo-name "epsilon-greedy"
   :parameter (if (= "standard" type) epsilon)
   :variant type
   :algo-fn (fn [] (epsilon-greedy-algorithm epsilon (mk-storage n)))})

(defn mk-softmax-algorithm
  [n type temperature]
  {:algo-name "softmax"
   :parameter (if (= "standard" type) temperature)
   :variant type
   :algo-fn (fn [] (softmax-algorithm temperature (mk-storage n)))})

(defn run-simulation
  "runs a number of monte carlo simulations. horizon specifies the number of pulls that will be made against the bandit. the algorithm aims to optimise the reward over this time.
   arms is a sequence of functions that represent the arms of the bandit.
   example: (run-simulation (mk-bernoulli-bandit 0.1 0.1 0.1 0.1 0.9) 1 1)
  
  result header: algo name, variant [standard or anneal], parameter to algo, simulation #, horizon t, arm picked, reward for this choice, cumulative reward]
  "
  ([arms]
     (run-simulation arms 1000 200))
  ([arms simulations horizon]
     (with-open [writer (clojure.java.io/writer "tmp/results.csv")]
       (let [arm-labels    (mk-arm-labels (count arms))
             epsilon-algos (concat (map (partial mk-epsilon-algorithm (count arms) "standard") [0.1 0.2 0.3 0.4 0.5])
                                   (map (partial mk-epsilon-algorithm (count arms) "anneal") [anneal]))
             softmax-algos (concat (map (partial mk-softmax-algorithm (count arms) "standard") [0.1 0.2 0.3 0.4 0.5])
                                   (map (partial mk-softmax-algorithm (count arms) "anneal") [anneal]))
             ucb-algo {:algo-name "ucb"
                       :variant nil
                       :algo-fn (fn []
                                  (ucb-algorithm (mk-storage (count arms))))}
             algorithms (concat [ucb-algo] epsilon-algos softmax-algos)
             n (+ 2 (.. Runtime getRuntime availableProcessors))
             algo-chunks (partition n algorithms)]
         (doseq [chunk algo-chunks]
           (doseq [algo chunk]
             (doseq [sim (range 1 (inc simulations))]
               (doseq [result (simulation-results arms arm-labels algo (inc horizon) sim)]
                 (.write writer (str (join "," result) "\n"))))))))))

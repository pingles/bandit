(ns clj-bandit.simulate)

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

(defn lever-name
  [pull]
  (first (keys (:arm pull))))

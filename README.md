# clj-bandit

A simple Clojure library for multi-armed bandit optimisation.

Algorithms are implemented following ["Bandit Algorithms for Website Optimization"](http://shop.oreilly.com/product/0636920027393.do) by John Myles White.

This library aims to be simple; it concerns itself with multi-armed bandit optimisation only. Integrating with web applications etc. is the responsibility of other libraries.

By keeping it small and simple we hope to make it far easier to integrate than existing tools.

[![Build Status](https://secure.travis-ci.org/pingles/clj-bandit.png)](http://travis-ci.org/pingles/clj-bandit)

## Dependency

The library is hosted on [Clojars](http://clojars.org) so you can add the following to your `project.clj`:

```clojure
:dependencies [[clj-bandit "0.1.0"]]
```

## Usage

```clojure
(ns casino.wynn
  (:require [clj-bandit.arms :as a]
            [clj-bandit.algo.epsilon :as e]))

;; arms represents the knowledge the algorithm acquires. 
;; a sorted map of clj-bandit.Arm records
(def arms (a/mk-arms :arm1 :arm2 :arm3 :arm4 :arm5))

(def epsilon 0.1)

(def algo-select (partial e/select-arm epsilon))

;; which arm should we pull?
(def arm (algo-select (vals arms)))

;; it told us to pull :arm5
;; casino.wynn> arm
;;#clj_bandit.arms.Arm{:name :arm5, :pulls 0, :value 0}

;; let's update the arms to record our payout from :arm5
;; this becomes the next arms state we pass in to e/select-arm
;; we can use 1.0 to indicate we were paid, and 0 to indicate
;; we weren't
(a/update (-> arm (a/reward 1.0) (a/pulled)) arms)
```

## Algorithms

As per the book, the following algorithms have been implemented so far:

* Epsilon-Greedy
* Softmax
* UCB

## Performance

["Bandit Algorithms for Website Optimization"](http://shop.oreilly.com/product/0636920027393.do) uses Monte Carlo Simulation to measure the performance of the algorithms. These can be run using functions from `clj-bandit.simulate`, and `./scripts/plot_results.r` will produce the following plots with ggplot2.

In the plots below, `algo.variant` can refer to either a "standard" or "annealing" algorithm (annealing applies a factor that causes the algorithm to explore less as it gains more experience). For "standard" algorithms, `algo.parameter` represents the temperature or epsilon value used to tune the algorithms tendency to explore.

* Epsilon-Greedy: the variant is the epsilon value (0.1, 0.2 etc.); 0.1 would mean the algorithm would experiment 10% of the time, and exploit the best performing for the remainder.
* softmax: the variant is the algorithm's temperature, behaving much like the epsilon value above.
* ucb: no variant value is used.

Results are for a 5-armed machine, rewarding at rates of: 10%, 10%, 10%, 10%, 90%. This is the same example as used in the book. Such significantly varying payouts are unlikely for most other applications so I'll update with some more complex simulations later.

### Average Reward

![Average Reward](http://clojure.bandit.s3-external-3.amazonaws.com/avg_reward.png)

### Accuracy

![Accuracy](http://clojure.bandit.s3-external-3.amazonaws.com/accuracy.png)

### Cumulative Reward

![Cumulative Reward](http://clojure.bandit.s3-external-3.amazonaws.com/cumulative_reward.png)

The following plot shows the summary for the algorithms when the simulation finished. 1500 simulations were run.

![Boxplot](http://clojure.bandit.s3-external-3.amazonaws.com/algorithm_reward_boxplot.png)

## License

Copyright &copy; Paul Ingles 2012

Distributed under the Eclipse Public License, the same as Clojure.

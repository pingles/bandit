# clj-bandit

A simple Clojure library for multi-armed bandit optimisation.

Algorithms are implemented following ["Bandit Algorithms for Website Optimization"](http://shop.oreilly.com/product/0636920027393.do) by John Myles White.

This library aims to be simple; it concerns itself with multi-armed bandit optimisation only. Integrating with web applications etc. is the responsibility of other libraries.

By keeping it small and simple we hope to make it far easier to integrate than existing tools. It will also be far more flexible.

## Usage

Algorithms are defined in `clj-bandit.algo.xxx` namespaces. Algorithms are created with a set of arms and indicate the choices the algorithm has to optimise between.

For example, to create an epsilon greedy algorithm with 4 arms:

```clojure
(ns casino.wynn
  (:use [clj-bandit.core :only (mk-arms)]
        [clj-bandit.storage :only (atom-storage)]
        [clj-bandit.algo.epsilon :only (epsilon-greedy-algorithm)]))

(def epsilon 0.1) ; epsilon indicates the percentage of time the algorithm
                  ; will explore vs. exploit. the higher the value the more
                  ; exploration

(def algo (epsilon-greedy-algorithm 0.1 (atom-storage (mk-arms #{:arm1 :arm2 :arm3 :arm4}))))

;; we can ask the algorithm which arm of the multi-armed bandit we should pull next:
(select-arm algo)

;; and indicate that we received a reward of 1
(update-reward algo :arm1 1)

;; or, that we received no reward
(update-reward algo :arm1 0)
```

## Performance

["Bandit Algorithms for Website Optimization"](http://shop.oreilly.com/product/0636920027393.do) uses Monte Carlo Simulation to measure the performance of the algorithms. These can be run using `clj-bandit.simulate/run-simulation`, and `./scripts/plot_results.r` will produce the following plots with ggplot2.

In the plots below, `algo.variant` refers to a parameter that can be passed to the algorithm to tune it's desire for experimentation vs exploitation:

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

The following plots show the summary for the simulations at `t = 250`, that is when the simulation reaches the final iteration.

![Maximum Rewards](http://clojure.bandit.s3-external-3.amazonaws.com/max_reward.png)

![Boxplot](http://clojure.bandit.s3-external-3.amazonaws.com/algorithm_reward_boxplot.png)

## License

Copyright &copy; Paul Ingles 2012

Distributed under the Eclipse Public License, the same as Clojure.

# clj-bandit

A Clojure library following ["Bandit Algorithms for Website Optimization"](http://shop.oreilly.com/product/0636920027393.do) by John Myles White.

This library aims to be simple- concerning itself with multi-armed bandit optimisation only. Integrating with web applications etc. is the responsibility of other libraries. Through keeping it small and simple we hope to make it far easier to integrate than existing tools.

## Performance

The ["Bandit Algorithms for Website Optimization"](http://shop.oreilly.com/product/0636920027393.do) uses Monte Carlo Simulation to measure the performance of the algorithms. These can be run using `clj-bandit.simulate/run-simulation`, and `./scripts/plot_results.r` will produce the following plots with ggplot2.

### Average Reward

![Average Reward](http://clojure.bandit.s3-external-3.amazonaws.com/avg_reward.png)

![Accuracy](http://clojure.bandit.s3-external-3.amazonaws.com/accuracy.png)

![Cumulative Reward](http://clojure.bandit.s3-external-3.amazonaws.com/cumulative_reward.png)

## License

Copyright &copy; Paul Ingles 2012

Distributed under the Eclipse Public License, the same as Clojure.

# TODO

* Break apart update-reward
  * Need to be able to increase n separately from the reward
    * Is this possible with the way that values are currently recorded? Do the value functions require us to state whether there was/wasn't a reward?


* Add bandit record- hold a collection of arms and names, make it quicker to fold results in
  * Measure performance with YourKit and optimise
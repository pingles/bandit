# TODO

* Change arm data structure- lots of places have to convert to a vector then back again, is it possible to switch it to a vector of maps? How much does that tidy up the code?

* Is it better to just use functions + maps rather than the protocol for the algorithm? Could use a macro `(with-algorithm [])` macro to avoid passing arm state everywhere

* Could certainly pull out functions from the reified protocol- so people could use just the functions if they wanted.

* Break apart update-reward
  * Need to be able to increase n separately from the reward
    * Is this possible with the way that values are currently recorded? Do the value functions require us to state whether there was/wasn't a reward?
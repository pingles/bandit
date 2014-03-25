# bandit-ring

A sample Ring application applying Multi-Armed Bandit Testing. A copy of the app is deployed on Heroku: [http://bandit-example.herokuapp.com/](http://bandit-example.herokuapp.com/) (although state is stored in a Ref so will be wiped when the process is cycled).

## Usage

Start the application

    $ lein run -m bandit.ring.example-app

Then open your browser and visit [http://localhost:8080](http://localhost:8080).

## Deploy to Heroku

In the root of the project (not `bandit-ring`)

    $ git subtree push --prefix bandit-ring heroku master

## To Do

`bandit-core` algorithms and `bandit-simulate` monte-carlo simulations assume immediate reward feedback. Need to modify `bandit-ring` to incorporate changes to handle such delays. See: [Multi-Armed Bandit Problems with Delayed Feedback](http://arxiv.org/pdf/1011.1161.pdf).

## License

Copyright &copy; 2013 Paul Ingles

Distributed under the Eclipse Public License, the same as Clojure.

# bandit-ring

A sample Ring application applying Multi-Armed Bandit Testing

## Usage

Start the application

    $ lein run -m clj-bandit.ring.example-app

Then open your browser and visit [http://localhost:8080](http://localhost:8080).

## To Do

`bandit-core` algorithms and `bandit-simulate` monte-carlo simulations assume immediate reward feedback. Need to modify `bandit-ring` to incorporate changes to handle such delays. See: [Multi-Armed Bandit Problems with Delayed Feedback](http://arxiv.org/pdf/1011.1161.pdf).

## License

Copyright &copy; 2013 Paul Ingles

Distributed under the Eclipse Public License, the same as Clojure.

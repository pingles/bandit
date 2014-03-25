(defproject bandit/bandit-ring "0.2.1-SNAPSHOT"
  :description "Ring middleware for multi-armed bandit testing"
  :url "http://github.com/pingles/bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.2.2"]
                 [bandit/bandit-core "0.2.1-SNAPSHOT"]]
  :main bandit.ring.example-app
  :profiles {:dev {:dependencies [[ring/ring-devel "1.2.2"]
                                  [ring/ring-jetty-adapter "1.2.2"]
                                  [hiccup "1.0.3"]
                                  [expectations "1.4.45"]
                                  [compojure "1.1.6"]]
                   :plugins [[lein-expectations "0.0.8"]]}})

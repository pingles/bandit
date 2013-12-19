(defproject clj-bandit/bandit-ring "0.2.0-SNAPSHOT"
  :description "Ring middleware for multi-armed bandit testing"
  :url "http://github.com/pingles/clj-bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring/ring-core "1.2.0-RC1"]
                 [clj-bandit/bandit-core "0.2.0"]]
  :profiles {:dev {:dependencies [[ring/ring-devel "1.2.0-RC1"]
                                  [ring/ring-jetty-adapter "1.2.0-RC1"]
                                  [hiccup "1.0.3"]
                                  [expectations "1.4.45"]
                                  [compojure "1.1.5"]]
                   :plugins [[lein-expectations "0.0.8"]]}})

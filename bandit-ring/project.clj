(defproject bandit/bandit-ring "0.2.1-SNAPSHOT"
  :description "Ring middleware for multi-armed bandit testing"
  :url "http://github.com/pingles/bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.12.3"]
                 [ring/ring-core "1.15.4"]
                 [ring/ring-jetty-adapter "1.15.4"]
                 [ring/ring-devel "1.15.4"]
                 [bandit/bandit-core "0.2.1-SNAPSHOT"]
                 [compojure "1.7.2"]
                 [hiccup "1.0.5"]]
  :min-lein-version "2.10.0"
  :main bandit.ring.app
  :uberjar-name "bandit-ring-standalone.jar"
  :profiles {:dev {:dependencies [[expectations "1.4.56"]]
                   :plugins [[lein-expectations "0.0.8"]]}})

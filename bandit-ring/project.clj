(defproject bandit/bandit-ring "0.2.1-SNAPSHOT"
  :description "Ring middleware for multi-armed bandit testing"
  :url "http://github.com/pingles/bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.2.2"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [ring/ring-devel "1.2.2"]
                 [bandit/bandit-core "0.2.1-SNAPSHOT"]
                 [compojure "1.1.6"]
                 [hiccup "1.0.3"]]
  :min-lein-version "2.0.0"
  :main bandit.ring.app
  :uberjar-name "bandit-ring-standalone.jar"
  :profiles {:dev {:dependencies [[expectations "1.4.45"]]
                   :plugins [[lein-expectations "0.0.8"]]}})

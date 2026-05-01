(defproject bandit/bandit-simulate "0.2.1-SNAPSHOT"
  :description "Multi-armed bandit simulation"
  :url "http://github.com/pingles/bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[bandit/bandit-core "0.2.1-SNAPSHOT"]
                 [org.clojure/clojure "1.12.3"]
                 [org.clojure/math.numeric-tower "0.1.0"]
                 [org.clojure/tools.cli "1.1.230"]
                 [org.clojure/data.csv "1.1.0"]
                 [incanter/incanter-core "1.9.3"]]
  :profiles {:dev {:dependencies [[criterium "0.4.6"]
                                  [expectations "1.4.56"]]
                   :plugins [[lein-expectations "0.0.8"]]}}
  :main bandit.simulate
  :min-lein-version "2.10.0"
  :jvm-opts ["-Xmx2G" "-server"])

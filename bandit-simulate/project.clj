(defproject bandit/bandit-simulate "0.2.0"
  :description "Multi-armed bandit simulation"
  :url "http://github.com/pingles/bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[bandit/bandit-core "0.2.0"]
                 [org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [org.clojure/tools.cli "0.2.2"]
                 [org.clojure/data.csv "0.1.2"]
                 [incanter/incanter-core "1.5.1"]]
  :profiles {:dev {:dependencies [[criterium "0.4.1"]
                                  [expectations "1.4.48"]]
                   :plugins [[lein-expectations "0.0.8"]]}}
  :main bandit.simulate
  :jvm-opts ["-Xmx2G" "-server" "-XX:+UseConcMarkSweepGC"])

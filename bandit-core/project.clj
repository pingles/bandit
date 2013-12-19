(defproject clj-bandit/bandit-core "0.2.0-SNAPSHOT"
  :description "Multi-armed bandit algorithms"
  :url "http://github.com/pingles/clj-bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/math.numeric-tower "0.0.2"]
                 [incanter/incanter-core "1.5.1"]]
  :profiles {:dev {:dependencies [[expectations "1.4.45"]]}}
  :plugins [[lein-expectations "0.0.8"]]
  :aot [clj-bandit.arms])

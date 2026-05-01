(defproject bandit/bandit-core "0.2.1-SNAPSHOT"
  :description "Multi-armed bandit algorithms"
  :url "http://github.com/pingles/bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.12.3"]
                 [org.clojure/math.numeric-tower "0.1.0"]
                 [incanter/incanter-core "1.9.3"]]
  :profiles {:dev {:dependencies [[expectations "1.4.56"]]}}
  :plugins [[lein-expectations "0.0.8"]]
  :min-lein-version "2.10.0"
  :aot [bandit.arms])

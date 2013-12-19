(defproject clj-bandit/bandit "0.2.0-SNAPSHOT"
  :description "Multi-armed bandit testing"
  :url "http://github.com/pingles/clj-bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[clj-bandit/bandit-core "0.2.0"]
                 [clj-bandit/bandit-simulate "0.2.0-SNAPSHOT"]
                 [clj-bandit/bandit-ring "0.2.0-SNAPSHOT"]]
  :plugins [[lein-sub "0.2.4"]]
  :sub ["bandit-core" "bandit-simulate" "bandit-ring"])

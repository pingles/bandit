(defproject bandit/bandit "0.2.1-SNAPSHOT"
  :description "Multi-armed bandit testing"
  :url "http://github.com/pingles/bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[bandit/bandit-core "0.2.1-SNAPSHOT"]
                 [bandit/bandit-simulate "0.2.1-SNAPSHOT"]
                 [bandit/bandit-ring "0.2.1-SNAPSHOT"]]
  :plugins [[lein-sub "0.2.4"]]
  :sub ["bandit-core" "bandit-simulate" "bandit-ring"])

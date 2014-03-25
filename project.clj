(defproject bandit/bandit "0.2.1-SNAPSHOT"
  :description "Multi-armed bandit testing"
  :url "http://github.com/pingles/bandit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-sub "0.3.0"]]
  :min-lein-version "2.0.0"
  :sub ["bandit-core" "bandit-simulate" "bandit-ring"])

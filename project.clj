(defproject kosmos "0.0.13-SNAPSHOT"

  :description "create an ordered system of components"

  :url "https://github.com/pupcus/kosmos"

  :scm {:url "git@github.com:pupcus/kosmos.git"}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :signing {:gpg-key "FCA46A30FEEE7E10"}

  :dependencies [[org.clojure/tools.logging "1.1.0"]
                 [com.stuartsierra/component "1.0.0"]]

  :profiles {:dev  {:resource-paths ["dev-resources"]
                    :dependencies [[org.clojure/clojure "1.10.3"]
                                   [org.slf4j/slf4j-log4j12 "1.7.32"]]}}

  :deploy-repositories {"releases" {:url "https://clojars.org/repo" :creds :gpg :sign-releases false}
                        "snapshots" {:url "https://clojars.org/repo" :creds :gpg :sign-releases false}}


  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]

  :global-vars {*warn-on-reflection* true
                *assert* false})

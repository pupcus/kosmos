(defproject kosmos "0.0.7"

  :description "create an ordered system of components"

  :url "https://bitbucket.org/pupcus/kosmos"

  :scm {:url "git@bitbucket.org:pupcus/kosmos"}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/tools.reader "1.0.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [com.stuartsierra/component "0.3.2"]]

  :profiles {:dev  {:resource-paths ["dev-resources"]
                    :dependencies [[org.clojure/clojure "1.8.0"]
                                   [org.slf4j/slf4j-log4j12 "1.7.25"]]}}

  :deploy-repositories [["snapshots"
                         {:url "https://clojars.org/repo"
                          :sign-releases false
                          :creds :gpg}]
                        ["releases"
                         {:url "https://clojars.org/repo"
                          :sign-releases false
                          :creds :gpg}]]

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

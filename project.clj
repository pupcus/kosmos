(defproject org.pupcus/kosmos "0.0.2-SNAPSHOT"

  :description "create an ordered system of components"

  :url "https://bitbucket.org/mdpendergrass/kosmos"

  :scm {:url "git@bitbucket.org:mdpendergrass/kosmos"}

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.reader "0.10.0"]
                 [com.stuartsierra/component "0.3.1"]]

  :repositories [["snapshots" {:url "http://maven.pupcus.org/archiva/repository/snapshots"
                               :creds :gpg}]
                 ["releases"  {:url "http://maven.pupcus.org/archiva/repository/internal"
                               :creds :gpg}]]

  :global-vars {*warn-on-reflection* true
                *assert* false})

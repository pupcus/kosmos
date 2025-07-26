(ns build
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

(def lib 'kosmos/kosmos)
(def version (slurp ".version"))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn- pom-template [version]
  [[:description "library to build a system of components in clojure "]
   [:url "https://github.com/pupcus/kosmos"]
   [:licenses
    [:license
     [:name "Eclipse Public License"]
     [:url "http://www.eclipse.org/legal/epl-v10.html"]]]
   [:developers
    [:developer
     [:name "pupcus"]]]
   [:scm
    [:url "https://github.com/pupcus/kosmos.git"]
    [:connection "scm:git:https://github.com/pupcus/kosmos.git"]
    [:developerConnection "scm:git:ssh:git@github.com:pupcus/kosmos.git"]
    [:tag version]]])

(defn- jar-opts [opts]
  (assoc opts
         :lib lib   :version version
         :jar-file  jar-file
         :basis     basis
         :class-dir class-dir
         :target    "target"
         :src-dirs  ["src"]
         :pom-data  (pom-template version)))

(defn jar [opts]
  (b/delete {:path "target"})
  (let [opts (jar-opts opts)]
    (b/write-pom opts)
    (b/copy-dir {:src-dirs ["src" "resources"]
                 :target-dir class-dir})
    (b/jar opts)))

(defn deploy "Deploy the JAR to Clojars." [opts]
  (let [{:keys [jar-file] :as opts} (jar-opts opts)]
    (dd/deploy {:installer :remote :artifact (b/resolve-path jar-file)
                :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))}))
  opts)

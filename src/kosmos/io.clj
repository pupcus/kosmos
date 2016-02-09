(ns kosmos.io
  (:refer-clojure :exclude [read-string])
  (:require [clojure.java.io :as io]
            [clojure.tools.reader.edn :as edn]
            [kosmos.util :as u]
            [kosmos.error :as err]))

(defn config-files
  "Given a base-directory location and an environment name, find all files ending in .edn"
  [base-directory env]
  (let [dir (io/as-file (u/mkpath base-directory (name env)))]
    (if-not (u/valid-directory? dir)
      (throw (err/file-not-found dir))
      (filterv #(re-find #"\.edn$" (.getName ^java.io.File %)) (vec (.listFiles dir))))))

(def ^:private read-string
  (partial edn/read-string {}))

(defn load-dispatch-fn [& args]
  (when-let [arg1 (first args)]
    (if (map? arg1)
      :edn
      (if (string? arg1)
        (let [target (io/as-file arg1)]
          (cond
            (.isDirectory target) :directory
            (.isFile target)      :file
            :otherwise            :string))
        (throw (err/invalid-config-source args))))))

(defmulti load-config #'load-dispatch-fn)

(defmethod load-config :directory [base-dir env] {:post [(map? %)]}
  (->> (config-files base-dir env)
       (map (comp read-string slurp))
       (apply merge)))

(defmethod load-config :file [f]  {:post [(map? %)]}
  ((comp read-string slurp) f))

(defmethod load-config :edn [m] {:post [(map? %)]}
  m)

(defmethod load-config :string [s] {:post [(map? %)]}
  (read-string s))

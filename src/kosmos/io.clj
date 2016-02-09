(ns kosmos.io
  (:refer-clojure :exclude [read-string])
  (:require [clojure.java.io :as io]
            [clojure.tools.reader.edn :as edn]
            [kosmos.error :as err]))

(defn config-files [d]
  (let [dir (io/as-file d)]
    (if-not (.isDirectory dir)
      (throw (err/file-not-found dir))
      (filterv #(re-find #"\.edn$" (.getName ^java.io.File %)) (vec (.listFiles dir))))))

(def ^:private read-string
  (partial edn/read-string {}))

(defn load-dispatch-fn [source]
  (if (map? source)
    :edn
    (if (string? source)
      (let [target (io/as-file source)]
        (cond
          (.isDirectory target) :directory
          (.isFile target)      :file
          :otherwise            :string))
      (throw (err/invalid-config-source source)))))

(defmulti load-config #'load-dispatch-fn)

(defmethod load-config :directory [d] {:post [(map? %)]}
  (->> (config-files d)
       (map (comp read-string slurp))
       (apply merge)))

(defmethod load-config :file [f]  {:post [(map? %)]}
  ((comp read-string slurp) f))

(defmethod load-config :edn [m] {:post [(map? %)]}
  m)

(defmethod load-config :string [s] {:post [(map? %)]}
  (read-string s))

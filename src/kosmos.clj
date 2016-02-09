(ns kosmos
  (:require [com.stuartsierra.component :as component]
            [kosmos.io :as io]
            [kosmos.util :as u]))

(def system)

(defn initialize
  "takes 'sources' where sources can be:

     directories (will read all .edn files in the directory as config maps)
      --   Note that this does NOT read .clj files in a directory source

     files (containing edn maps)

     strings (each to be read as edn map)

     clojure maps (returned as-is)

  returns a com.stuartsierra.component/SystemMap"
  [& sources]
  (u/initialize (reduce merge (map io/load-config sources))))

(defn stop [system]
  (component/stop-system (dissoc system :shutdown-hook)))

(defn- shutdown-hook [system]
  (Thread.
   (fn []
     (stop system))))

(defn- system-shutdown-hook [system]
  (let [h (shutdown-hook system)]
    (u/add-shutdown-hook h)
    h))

(defn start [system-map]
  (let [system (component/start-system system-map)]
    (assoc system :shutdown-hook (system-shutdown-hook system))))

(defn exit [system status-code]
  (when system
    (stop system))
  (shutdown-agents)
  (System/exit (or status-code 0)))

(defn start! [system-map]
  (let [started-system (start system-map)]
    (u/remove-shutdown-hook (:shutdown-hook started-system))
    (alter-var-root #'system (fn [_]
                               (assoc started-system :shutdown-hook (system-shutdown-hook #'system))))))

(defn stop! []
  (u/remove-shutdown-hook (:shutdown-hook system))
  (alter-var-root #'system stop))

(defn exit! [status-code]
  (u/remove-shutdown-hook (:shutdown-hook system))
  (alter-var-root #'system stop)
  (exit nil status-code))

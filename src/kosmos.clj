(ns kosmos
  (:require [com.stuartsierra.component :as component]
            [kosmos.io :as io]
            [kosmos.util :as u]))

(def system)

(defn initialize
  ([edn-or-string-or-file] (u/initialize (io/load-config edn-or-string-or-file)))
  ([base-directory env]    (u/initialize (io/load-config base-directory env))))

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

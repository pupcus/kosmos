(ns kosmos.util
  (:require [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [kosmos.error :as err]))

(defn- expect-thread [t]
  (when-not (instance? Thread t)
    (throw (err/invalid-thread t))))

(defn add-shutdown-hook [h]
  (expect-thread h)
  (.addShutdownHook (Runtime/getRuntime) h))

(defn remove-shutdown-hook [h]
  (when h
    (when-let [runtime (Runtime/getRuntime)]
      (.removeShutdownHook runtime h))))

(defn resolve-symbol [x]
  (if (instance? clojure.lang.Named x)
    (let [ns  (symbol (or (namespace x) (throw (err/not-fully-qualified x))))]
      (require ns)
      (or (ns-resolve ns (symbol (name x)))
          (throw (err/unable-to-resolve x))))
    x))

(defn- create-component [config]
  (if-let [init-fn (:kosmos/init config)]
    ((resolve-symbol init-fn) config)
    config))

(defn- create-components [m]
  (reduce-kv
   (fn [s k v]
     (assoc s k (create-component v)))
   {}
   m))

(defn- dependencies [m]
  (reduce-kv
   (fn [s k v]
     (if (and (associative? v) (contains? v :kosmos/requires))
       (assoc s k (:kosmos/requires v))
       s))
   {} m))

(defn- build-initialization-symbol [component-config]
  (when-let [type (:kosmos/type component-config)]
    (symbol (str (namespace type) "/" (str "map->" (name type))))))

(defn- process-component-config
  [component-config]
  (if-let [init (build-initialization-symbol component-config)]
    (-> component-config
        (dissoc :kosmos/type)
        (assoc :kosmos/init init))
    component-config))

(defn- process-system-config [system-config]
  (reduce-kv
   (fn [m k v]
     (assoc m k (process-component-config v)))
   {}
   system-config))

(defn initialize [system-config]
  (remove-method clojure.core/print-method com.stuartsierra.component.SystemMap)
  (let [initialized-system (->> (process-system-config system-config)
                                create-components
                                component/map->SystemMap)]
    (component/system-using initialized-system (dependencies system-config))))

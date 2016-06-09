(ns kosmos.util
  (:require [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [kosmos.error :as err]))

(defn get-sym [s]
  (if (var? s)
    @s
    s))

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
    (init-fn config)
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

(defn- type-constructor [type]
  (symbol (str (namespace type) "/" (str "map->" (name type)))))

(defn- init? [type]
  (try
    (let [sym (resolve-symbol type)]
      (when (fn? (get-sym sym))
        sym))
    (catch Exception e)))

(defn- type? [type]
  (try
    (let [sym (resolve-symbol (type-constructor type))]
      (when (fn? (get-sym sym))
        sym))
    (catch Exception e)))

(defrecord InitFnComponent []
  component/Lifecycle
  (start [{init-fn :kosmos/init-fn :as component}]
    (if-let [init-fn (resolve-symbol init-fn)]
      (init-fn component)
      (throw
       (ex-info
        "kosmos/init required but cannot be resolved!"
        {:init-fn (:kosmos/init-fn component)}))))
  (stop [_]))

(defn- build-initializable-component [component-config]
  (if-let [init (:kosmos/init component-config)]
    (cond
      (init? init)   (assoc component-config
                           :kosmos/init map->InitFnComponent
                           :kosmos/init-fn (init? init))
      (type? init)   (assoc component-config
                            :kosmos/init (type? init))
      :otherwise     (throw (err/invalid-initialization init)))

    component-config))

(defn- process-system-config [system-config]
  (reduce-kv
   (fn [m k v]
     (assoc m k (build-initializable-component v)))
   {}
   system-config))

(defn map->system [system-config]
  (let [initialized-system (->> (process-system-config system-config)
                                create-components
                                component/map->SystemMap)]
    (component/system-using initialized-system (dependencies system-config))))

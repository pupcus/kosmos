(ns kosmos-test
  (:require [kosmos :refer :all]
            [clojure.test :refer :all]
            [com.stuartsierra.component :as component]))


(defrecord TestComponent [status]

  component/Lifecycle
  (start
   [component]
   (assoc component :status :started))

  (stop
   [component]
   (assoc component :status :stopped)))


(defn init-component-three [config]
  (let [component (map->TestComponent config)]
    (assoc component :initialized true)))


(def test-config
  {:one   {:kosmos/type :kosmos-test/TestComponent
           :status :not-started}
   :two   {:kosmos/type :kosmos-test/TestComponent
           :status :not-started}
   :three {:kosmos/init kosmos-test/init-component-three
           :status :not-started}})

(deftest test-initialize
  (is (= (component/map->SystemMap {:component {:option1 :one}})
         (initialize {:component {:option1 :one}}))))

(deftest test-system-start-and-stop
  (start! (initialize test-config))

  (let [c1 (:one   kosmos/system)
        c2 (:two   kosmos/system)
        c3 (:three kosmos/system)]
    (is c1)
    (is c2)
    (is c3)
    (is (= :started (:status c1)))
    (is (= :started (:status c2)))
    (is (= :started (:status c3)))
    (is (:initialized c3)))

  (stop!)

  (let [c1 (:one   kosmos/system)
        c2 (:two   kosmos/system)
        c3 (:three kosmos/system)]
    (is c1)
    (is c2)
    (is c3)
    (is (= :stopped (:status c1)))
    (is (= :stopped (:status c2)))
    (is (= :stopped (:status c3)))))

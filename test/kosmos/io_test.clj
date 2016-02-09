(ns kosmos.io-test
  (:require [kosmos.io :refer :all]
            [clojure.test :refer :all]))

(deftest test-config-files
  (is (= (mapv #(.getPath ^java.io.File %) (config-files "test/config/test"))
         ["test/config/test/one.edn" "test/config/test/three.edn"])))

(deftest test-config-files-directory-does-not-exist
  (is (thrown? clojure.lang.ExceptionInfo (config-files "test/config/doesnotexist"))))

(deftest test-load-config
  (is (= {:component {:option1 :one}}
         (load-config "test/config/test")))
  (is (= {:component {:option1 :one}}
         (load-config "test/config/test/one.edn")))
  (is (= {:component {:option1 :one}}
         (load-config {:component {:option1 :one}})))
  (is (= {:component {:option1 :one}}
         (load-config "{:component {:option1 :one}}")))
  (is (thrown? clojure.lang.ExceptionInfo (load-config [:a]))))

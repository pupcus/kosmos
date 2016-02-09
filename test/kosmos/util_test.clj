(ns kosmos.util-test
  (:require [kosmos.util :refer :all]
            [clojure.test :refer :all]))

(defn test-fn [x] (* x x))
(deftest test-resolve-symbol
  (is (= (test-fn 5) ((resolve-symbol kosmos.util-test/test-fn) 5)))
  (is (= (test-fn 5) ((resolve-symbol :kosmos.util-test/test-fn) 5))))

(deftest test-resolve-symbol-w-not-fq-symbol
  (is (thrown? java.lang.IllegalArgumentException (resolve-symbol :test))))

(deftest test-resolve-symbol-w-unresolvable-symbol
  (is (thrown? clojure.lang.ExceptionInfo (resolve-symbol :kosmos-test/not-defined))))

(ns kosmos.error)

(defn file-not-found [^java.io.File f]
  (ex-info
   (format
    "Unable to find target %s"
    (.getAbsolutePath f))
   {:target f}
   (java.io.FileNotFoundException.
    (format
     "file or directory [\"%s\"] not found"
     (.getAbsolutePath f)))))

(defn not-fully-qualified [x]
  (IllegalArgumentException.
   (format
    "expecting fully-qualified symbol or keyword, but found [%s]"
    x)))

(defn unable-to-resolve [x]
  (ex-info
   (format
    "Unable to resolve %s"
    x)
   {:arg x}))

(defn invalid-config-source [args]
  (ex-info
   (format
    "Expecting a base directory and environment name, a file, or a string to load but found %s"
    args)
   {:args args}))

(defn invalid-thread [t]
  (ex-info
   (format
    "Expecting a thread but found %s"
    (class t))
   {:arg t}))


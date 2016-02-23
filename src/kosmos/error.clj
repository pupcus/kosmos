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

(defn invalid-config-source [source]
  (ex-info
   (format
    "Expecting a directory, a file, a string, or an edn map to load but found %s"
    source)
   {:source source}))

(defn invalid-thread [t]
  (ex-info
   (format
    "Expecting a thread but found %s"
    (class t))
   {:arg t}))

(defn invalid-initialization [type]
  (ex-info
   (format
    "Expecting a fn or a type to initialize component"
    type)
   {:type type}))

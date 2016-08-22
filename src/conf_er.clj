;;; Copyright (c) 2013 TouchType Ltd. All Rights Reserved.

(ns conf-er
  "Global configuration utilities. This is intended to be very simple,
   for top-level projects which only have one configuration file. Any
   library projects using this should be good citizens and namespace
   their keywords with ::keyword, so as to peacefully coexist in the
   single configuration file as there is no way to specify multiple
   files"
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn])
  (:import  [java.io File PushbackReader]))

(defn config-file
  "Handle to active config file as defined in the -Dconfig= java
   property. This can be set with leiningen in the :jvm-opts vector"
  []
  (if-let [filename (System/getProperty "config")]
    (or (io/file filename)
        (throw (Exception. (str "Can't find requested configuration file: " filename))))
    (throw (Exception. "You must set the jvm property -Dconfig=<config file> to use conf-er"))))

(defn reload-config-file
  "Next time a config is requested, the configuration will be re-read
   from disk. Obviously this could mean that other parts of your program
   have old values and haven't re-read the configuration - it is up to
   you to ensure your program doesn't get into an inconsistent state and
   to re-read the config when necessary"
  []
  (def config-map
    "Global settings map. Delayed evaluation to prevent io during
     compilation. Use `config` to access this without deref."
    (delay (let [file (config-file)]
             (with-open [conf (PushbackReader. (io/reader file))]
               (edn/read conf))))))

;;; Ensure that the config map has been requested at least once on load
(reload-config-file)

(defn configured?
  "Return whether the given (possibly nested) key is provided in the
   configuration"
  [& ks]
  (let [parent (get-in @config-map (butlast ks))]
    (and (map? parent) (contains? parent (last ks)))))

(defn config
  "Return the requested section of the config map.  Provide any number
   of nested keys, or no keys at all for the whole map.

   Throws an exception if the requested key is not found"
  ([] @config-map)
  ([& ks]
   (if (apply configured? ks)
     (get-in @config-map ks)
     (throw (Exception. (str "Could not find " ks " in configuration file"))))))

(defn opt-config
  "Return the requested section of the config map.  Provide any number
   of nested keys, or no keys at all for the whole map.

   Does NOT throw exceptions for missing keys"
  [& ks]
  (get-in @config-map ks))

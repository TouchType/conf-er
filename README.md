# conf-er

A ridiculously simple application configuration library.

I found myself duplicating this code in almost every project and so decided to break it out into a library. Thanks to @jaley for the original gist.

I hope you find this as useful as I have.

## Usage

Add to your leiningen dependencies

```clojure
{:dependencies [[conf-er "1.0.1"]]}
```

The idea is to have a single configuration file which consists of a keyworded map, you then look up individual properties with nested keywords, for example:

```clojure
;; my-config.conf
{:username "joe.bloggs"
 :password "letmein"
 :database {:host "127.0.0.1"
            :port 1234}

 :my.library/number 42}

```

And then look up the configuration from anywhere within your program! Simply include the conf-er namespace and then either the strict (config) function (which throws if nothing is configured) or (opt-config) which will simply return nil if unconfigured.

```clojure
(use 'conf-er)

(config :username) => "joe.bloggs"
(configured? :database) => true
(config :database) => {:host "127.0.0.1" :port 1234}
(config :database :port) => 1234

;; Trying to retrieve unconfigured options
(config :database :connections) => (Exception "Couldn't find :database :connections in configuration file")
(opt-config :database :connections) => nil
```

Tell your program where to find the configuration file from your leiningen project.clj, or if you want to set it when you've packaged up and distributed a jar, simply set it as an option on the java command

```clojure
...
:jvm-opts ["-Dconfig=my-config.conf"]
...
```

If you're changing your config on the go, you can reload the config file from your REPL:

```clojure
=> (reload-config-file)
```

If you use this from within a library, you must namespace your configuration in case the application using your library also wishes to use conf-er. You can do this like so:

```clojure
(ns my.library
  (:require [conf-er :refer [config]]))

(config ::number) => 42
```

Here :number will expand out to the namespaced keyword :my.library/number, which we put in our configuration file earlier.

## Testing

If you want to write tests which use this code, it's useful to know that conf-er stores all the settings in conf-er/config-map and so you can with-redefs this for tests. See conf-er's own tests for an example usage.

## Issues

Hopefully this project is simplistic enough that nothing much will come up, but please report any issues through the github issue tracker.

## License

Copyright © 2013 TouchType Ltd.

Distributed under the Eclipse Public License, the same as Clojure.

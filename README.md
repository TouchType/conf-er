# conf-er

A ridiculously simple application configuration library.

I found myself duplicating this code in almost every project and so decided to break it out into a library. Thanks to @jaley for the original gist.

I hope you find this as useful as I have.

## Usage

Add to your leiningen dependencies

```clojure
{:dependencies [conf-er "1.0.0"]}
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

Tell your program where to find the configuration file from your leiningen project.clj

```clojure
...
:jvm-opts ["-Dconfig=~/my-config.conf"]
...
```

And then look up the configuration from anywhere within your program! Simply include the conf-er namespace

```clojure
(use 'conf-er)

(config :username) => "joe.bloggs"
(configured? :database) => true
(config :database) => {:host "127.0.0.1" :port 1234}
(config :database :port) => 1234
(opt-config :database :connections) => nil
```

If you use this from within a library, you must namespace your configuration in case the application using your library also wishes to use conf-er. You can do this like so:

```clojure
(ns my.library
  (:require [conf-er :refer [config]]))

(config ::number) => 42
```

Here :number will expand out to the namespaced keyword :my.library/number, which we put in our configuration file earlier.

## Issues

Hopefully this project is simplistic enough that nothing much will come up, but please report any issues through the github issue tracker.

## License

Copyright © 2013 TouchType Ltd.

Distributed under the Eclipse Public License, the same as Clojure.

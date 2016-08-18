(ns test-conf-er
  (:require [midje.sweet :refer :all]
            [conf-er :refer :all]))

(def map {:option1 1
          :option2 {:thing :test
                    :foo {:bar :baz}}
          :booloptf false
          :booloptt true
          :something nil})

(with-redefs [conf-er/config-map (delay map)]
  (fact "Basic lookup works"
        (config :option1) => 1
        (config :option2 :thing) => :test
        (config :option2 :foo :bar) => :baz)

  (fact "Looking up things which evaluate to truthy/falsey still returns the
   appropriate values"
        (config :booloptf) => false
        (config :booloptt) => true
        (config :something) => nil)

  (fact "Attempting to get keys which aren't there with strict config throws"
        (config :option2 :thing :bar) => (throws)
        (config :not-there) => (throws))

  (fact "Passing no keys to config returns the whole map"
        (config) => map)

  (fact "Basic lookups return whether they are configured or not"
        (configured? :option1) => true
        (configured? :option2 :thing) => true
        (configured? :option2 :foo :bar) => true

        (configured? :option2 :thing :bar) => false
        (configured? :not-there) => false)

  (fact "Truthy/falsey values don't throw the configured? predicate"
        (configured? :booloptf) => true
        (configured? :booloptt) => true
        (configured? :something) => true)

  (fact "Optional config returns things when they are there"
        (opt-config :option1) => 1
        (opt-config :option2 :thing) => :test
        (opt-config :option2 :foo :bar) => :baz)

  (fact "Optional config works fine with things which evaluate to
   truthy/falsey"
        (opt-config :booloptf) => false
        (opt-config :booloptt) => true
        (opt-config :something) => nil)

  (fact "Optional config simply returns nil if things aren't there"
        (opt-config :option2 :thing :bar) => nil
        (opt-config :not-there) => nil))

(ns nucleus.event-test
  (:require-macros [cemerick.cljs.test :refer (is deftest testing)])
  (:require [nucleus.test-helper :as h]
            [nucleus.event :as event]
            [cemerick.cljs.test])
  (:import [goog.events Event BrowserEvent]))

(deftest event-test
  (let [target (h/create-element "div")
        event (h/browser-event :click target)]

    (is (satisfies? event/Event event))
    (is (instance? BrowserEvent event))
    (is (instance? Event event))

    (testing "IFn"
      (is (satisfies? IFn event))
      (is (= target (event :current-target)))
      (is (= target (event :currentTarget)))
      (is (= target (event "currentTarget"))))

    (testing "ILookup"
      (is (satisfies? ILookup event))
      (is (= target (:current-target event)))
      (is (= target (get event :current-target)))
      (is (= target (get event :currentTarget)))
      (is (= target (get event "currentTarget"))))

    (testing "event properties"
      (is (= "click" (event :type)))
      ;;(is (= target (event :target)))
      (is (= target (event :current-target)))
      (is (not (:default-prevented event)))
      (is (not (:propagation-stopped_ event))))

    ;; TODO:
    ;; (testing "browser event properties")

    (testing "prevent-default"
      (event/prevent-default! event)
      (is (:default-prevented event)))

    (testing "stop-propagation"
      (event/stop-propagation! event)
      (is (:propagation-stopped_ event)))))


(deftest event-target-test
  (let [target (h/create-element "div")]
    (is (satisfies? event/EventTarget target))
    (let [f (fn [event])]
      (is (= 0 (count @event/listeners-map))
          "no listeners have been added yet")

      (testing "listen!"
        (event/listen! target :foo f)
        (event/listen! target :foo f)
        (is (= 1 (count @event/listeners-map))
            "listener can only be added once for type")
        (event/listen! target :bar f)
        (is (= 2 (count @event/listeners-map))
            "listener can be added for multiple types")
        (event/listen! target #{:foo :bar :baz} f)
        (is (= 3 (count @event/listeners-map))
            "listener can be added for multiple types at once"))

      (testing "unlisten!"
        (event/unlisten! target :bar f)
        (is (= 2 (count @event/listeners-map))
            "listener can be removed for a single type")
        (event/unlisten! target #{:foo :bar :baz} f)
        (is (= 0 (count @event/listeners-map))
            "listener can be removed for multiple types at once")))))

(ns nucleus.event-test
  (:require-macros [cemerick.cljs.test :refer (is deftest testing done)])
  (:require [nucleus.test-helper :as h]
            [nucleus.event :as event]
            [nucleus.event.proto :as proto]
            [nucleus.event.impl :as impl]
            [cemerick.cljs.test])
  (:import [goog.events Event EventTarget BrowserEvent]))

(defn event-tests [event]
  (is (event/event? event))

  (testing "IFn"
    (is (satisfies? IFn event))
    (is (= (event :default-prevented)
           (event :defaultPrevented)
           (event "defaultPrevented")
           false)))

  (testing "ILookup"
    (is (satisfies? ILookup event))
    (is (= (:default-prevented event)
           (get event :default-prevented)
           (get event :defaultPrevented)
           (get event "defaultPrevented")
           false)))

  (testing "event functions"
    (testing "prevent-default"
      (is (not (:default-prevented event)))
      (event/prevent-default! event)
      (is (:default-prevented event)))

    (testing "stop-propagation"
      (is (not (:propagation-stopped_ event)))
      (event/stop-propagation! event)
      (is (:propagation-stopped_ event)))))

(deftest event-test
  (testing "goog event and target"
    (let [type :foo
          target (event/event-target)
          event (event/event type target)]

      (event-tests event)

      (testing "event properties"
        (is (string? (event :type))
            "event type is a string")
        (is (= (name type) (event :type))
            "event type is correct")
        (is (= target (:current-target event))
            "current target is always set")
        (is (= target (event :target))
            "target is always set")))))

(deftest browser-event-test
  (testing "goog browser event and target"
    (let [type :click
          target (event/event-target)
          orig (h/create-event type)
          event (event/browser-event orig target)]

      (event-tests event)

      (is (event/browser-event? event))

      (testing "browser event functions"
        (is (= orig (event/original-event event))))

      (testing "event properties"
        (is (string? (event :type))
            "event type is a string")
        (is (= (name type) (event :type))
            "event type is correct")
        (is (= target (:current-target event))
            "current target is always set")
        (is (= nil (event :target))
            "target is not set unless fired")))))

(deftest event-listener-test
  (let [f (fn [event])]

    (is (event/event-listener? f))

    (is (= (event/listener-id f) f))
    (is (= (event/listener-fn f) f))

    (let [id ::listener-id
          f1 (event/wrap-listener-id f id)]
      (is (= (event/listener-id f1) id))
      (is (= (event/listener-fn f1)
             (event/listener-fn f)
             f)))))

(defn event-target-tests [target]
  (is (event/event-target? target))
  (let [n impl/listener-count
        f (fn [event])]

    (testing "listen!"
      (event/listen! target :foo f)
      (event/listen! target :foo f)
      (is (= (+ n 1) impl/listener-count)
          "listener can only be added once for type")
      (event/listen! target :bar f)
      (is (= (+ n 2) impl/listener-count)
          "listener can be added for multiple types")
      (event/listen! target #{:foo :bar :baz} f)
      (is (= (+ n 3) impl/listener-count)
          "listener can be added for multiple types at once"))

    (testing "unlisten!"
      (event/unlisten! target :bar f)
      (is (= (+ n 2) impl/listener-count)
          "listener can be removed for a single type")
      (event/unlisten! target #{:foo :bar :baz} f)
      (is (= n impl/listener-count)
          "listener can be removed for multiple types at once"))))

(deftest event-target-test
  (event-target-tests (event/event-target))
  (event-target-tests (h/create-element "div")))

(deftest ^:async event-target-dispatch-test
  (let [type :foo
        target (event/event-target)
        f (fn [event]
            (is (event/event? event))
            (is (= (:type event) (name type)))
            (is (= (:target event) target))
            (is (= (:current-target event) target))
            (done))]

    (event/listen! target type f)
    (event/dispatch! target type)))

;; (deftest ^:async basic-test
;;   (let [target (h/create-element "div")]
;;     (event/listen! target :click
;;       (fn [event]
;;         (try (is (instance? BrowserEvent event))
;;              (is (satisfies? IFn event))
;;              (is (= "click" (event :type)))
;;              (is (satisfies? ILookup event))
;;              (is (= "click" (:type event)))

;;              (is (= target (event "currentTarget")))
;;              (is (= target (event :currentTarget)))
;;              (is (= target (event :current-target)))
;;              (is (= target (event :target)))

;;              (is (not (:default-prevented event)))
;;              (event/prevent-default! event)
;;              (is (:default-prevented event))
;;              (catch :default e
;;                (done e)))
;;         (done)))
;;     (h/click! target)))

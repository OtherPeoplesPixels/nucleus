(ns nucleus.event-test
  (:require-macros [cemerick.cljs.test :refer (is deftest testing)])
  (:require [nucleus.test-helper :as h]
            [nucleus.event :as event]
            [nucleus.event.proto :as proto]
            [nucleus.event.impl :as impl]
            [cemerick.cljs.test])
  (:import [goog.events Event BrowserEvent]))

(deftest event-test
  (let [target (h/create-element "div")
        event (h/browser-event :click target)]

    (is (event/event? event))
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

    (is (event/event-target? target))

    (let [f (fn [event])]
      (is (= 0 impl/listener-count)
          "no listeners have been added yet")

      (testing "listen!"
        (event/listen! target :foo f)
        (event/listen! target :foo f)
        (is (= 1 impl/listener-count)
            "listener can only be added once for type")
        (event/listen! target :bar f)
        (is (= 2 impl/listener-count)
            "listener can be added for multiple types")
        (event/listen! target #{:foo :bar :baz} f)
        (is (= 3 impl/listener-count)
            "listener can be added for multiple types at once"))

      (testing "unlisten!"
        (event/unlisten! target :bar f)
        (is (= 2 impl/listener-count)
            "listener can be removed for a single type")
        (event/unlisten! target #{:foo :bar :baz} f)
        (is (= 0 impl/listener-count)
            "listener can be removed for multiple types at once")))))




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

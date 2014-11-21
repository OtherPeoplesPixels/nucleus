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

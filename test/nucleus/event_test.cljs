(ns opp.nucleus.event-test
  (:require-macros [cemerick.cljs.test :refer (is deftest done)])
  (:require [cemerick.cljs.test :as t]
            [opp.nucleus.event :as event]))


;; Test Helpers

(defn create-element [tag]
  (js/document.createElement tag))

(defn click! [el]
  (let [event (doto (.createEvent js/document "MouseEvent")
                (.initEvent "click" true true))]
    (.dispatchEvent el event)))


;; Tests

(deftest ^:async basic-test
  (let [target (create-element "div")]
    (event/listen! target :click
      (fn [event]
        (is (satisfies? IFn event))
        (is (= "click" (event :type)))
        (is (satisfies? ILookup event))
        (is (= "click" (:type event)))

        (is (= target (event "currentTarget")))
        (is (= target (event :currentTarget)))
        (is (= target (event :current-target)))
        (is (= target (event :target)))

        (is (not (:default-prevented event)))
        (event/prevent-default! event)
        (is (:default-prevented event))

        (done)))
    (click! target)))

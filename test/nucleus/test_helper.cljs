(ns nucleus.test-helper
  (:require [goog.events :as gevt]))

(defn create-element [tag]
  (js/document.createElement tag))

;; https://github.com/madrobby/zepto/blob/0f920c1/src/event.js#L16
(def mouse-events #{"click" "mousedown" "mouseup" "mousemove"})

;; https://github.com/madrobby/zepto/blob/0f920c1/test/event.html#L36
(def click-defaults [js/document.defaultView 1 0 0 0 0 false false false false 0 nil])

;; NOTE: this written specifically for testing in old version of webkit,
;; e.g. phantomjs
(defn create-event
  ([type] (create-event type true true))
  ([type bubbles? cancelable? & more]
     (let [type (name type)
           ctor (if (mouse-events type)
                  "MouseEvent"
                  "Event")
           more (if (= "click" type)
                  (into more (drop (count more) click-defaults))
                  more)
           args (list* type bubbles? cancelable? more)
           event (.createEvent js/document ctor)
           init (if (= "MouseEvent" ctor)
                  (.-initMouseEvent event)
                  (.-initEvent event))]

       (.apply init event (into-array args))
       event)))

(defn browser-event
  ([type]
     (browser-event type js/undefined))
  ([type target]
     (let [event (create-event type)]
       ;;(.dispatchEvent target event)
       (gevt/BrowserEvent. event target))))

(defn goog-event
  ([type]
     (gevt/Event. (name type)))
  ([type target]
     (gevt/Event. (name type) target)))

(defn click! [el]
  (let [event (create-event :click)]
    (.dispatchEvent el event)))

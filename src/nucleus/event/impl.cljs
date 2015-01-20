(ns nucleus.event.impl
  (:require [nucleus.event.proto :as proto]
            [plumbing.core :refer (dissoc-in)]
            [goog.events :as gevt]
            [goog.object :as gobj]
            [goog.string :as gstr])
  (:import [goog.events Event EventTarget BrowserEvent]))

;; # Event

(extend-type Event
  proto/Event
  (-prevent-default [event] (.preventDefault event))
  (-stop-propagation [event] (.stopPropagation event))

  ILookup
  (-lookup
    ([event k]
       (-lookup event k nil))
    ([event k not-found]
       (let [k (gstr/toCamelCase (name k))]
         (gobj/get event k not-found))))

  IFn
  (-invoke
    ([event k]
       (-lookup event k))
    ([event k not-found]
       (-lookup event k not-found))))

;; # BrowserEvent

(extend-type BrowserEvent
  proto/BrowserEvent
  (-original-event [event]
    (.getBrowserEvent event)))


;; # EventListener

(extend-protocol proto/EventListener
  function
  (-listener-id [f] f)
  (-listener-fn [f] f))


;; # EventTarget

;; A map used to track goog.event listener keys for each target by type. This is
;; used to prevent multiple listeners with the same identity from being added to
;; the same target and type.
(def ^:private listener-map (atom {}))

;; Internal listener count used for testing.
(def ^:internal listener-count 0)

(defn- listener-map-ks
  ([target type]
     [(goog/getUid target) (keyword type)])
  ([target type listener]
     (conj (listener-map-ks target type)
           (proto/-listener-id listener))))

(defn- listen [target type listener]
  (let [ks (listener-map-ks target type listener)]
    (when-not (get-in @listener-map ks)
      (let [f (proto/-listener-fn listener)
            k (gevt/listen target (name type) f)]
        (swap! listener-map assoc-in ks k)
        (set! listener-count (inc listener-count))))))

(defn- unlisten [target type listener]
  (let [ks (listener-map-ks target type listener)]
    (when-let [key (get-in @listener-map ks)]
      (gevt/unlistenByKey key)
      (swap! listener-map dissoc-in ks)
      (set! listener-count (dec listener-count)))))

;; See goog.events.EventLike
(defn- event-like [event-data]
  (cond (keyword? event-data) (name event-data)
        (map? event-data) (clj->js event-data)
        :else event-data))

(extend-protocol proto/EventTarget

  js/EventTarget
  (-listen [target type listener _]
    (listen target type listener))
  (-unlisten [target type listener _]
    (unlisten target type listener))
  (-build-event [target event]
    ;; TODO: create event from data
    event)
  (-dispatch-event [target event]
    (.dispatchEvent target event))

  EventTarget
  (-listen [target type listener _]
    (listen target type listener))
  (-unlisten [target type listener _]
    (unlisten target type listener))
  (-build-event [target event-data]
    (event-like event-data))
  (-dispatch-event [target event]
    (gevt/dispatchEvent target event))
)

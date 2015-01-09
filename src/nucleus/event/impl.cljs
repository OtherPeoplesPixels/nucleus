(ns nucleus.event.impl
  (:require [nucleus.event.proto :as proto]
            [goog.events :as gevt]
            [goog.object :as gobj]
            [goog.string :as gstr]))

;; # Event

(extend-type goog.events.Event
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


;; # EventListener

(extend-protocol proto/EventListener
  function
  (-listener-id [f] f)
  (-listener-fn [f] f))


;; # EventTarget

;; A mapping of argument vectors to goog event listener keys. This is used to
;; prevent multiple listeners with the same identity from being added to the
;; same target.
(def ^:private listeners-map (atom {}))

(defn- add-listener
  [token target type f]
  (when-not (contains? @listeners-map token)
    (let [key (gevt/listen target (name type) f)]
      (swap! listeners-map assoc token key))))

(defn- make-token
  [target type listener]
  [(goog/getUid target)
   (keyword type)
   (proto/-listener-id listener)])

(extend-protocol proto/EventTarget
  default ;js/EventTarget gevt/EventTarget gevt/Listenable
  (-listen [target type listener opts]
    (let [token (make-token target type listener)
          f (proto/-listener-fn listener)]
      (add-listener token target type f)))

  (-unlisten [target type listener opts]
    (let [token (make-token target type listener)]
      (when-let [key (get @listeners-map token)]
        (gevt/unlistenByKey key)
        (swap! listeners-map dissoc token))))

  ;; See goog.events.EventLike
  (-build-event [target event-data]
    (cond (keyword? event-data) (name event-data)
          (map? event-data) (clj->js event-data)
          :else event-data))

  (-dispatch-event [target event]
    (gevt/dispatchEvent target event)))

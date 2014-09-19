(ns opp.nucleus.event
  "An abstracted browser event system."
  (:require [goog.events :as gevt]
            [goog.object :as gobj]
            [goog.string :as gstr]))


;; # Event Protocols

(defprotocol Event
  (-prevent-default [event])
  (-stop-propagation [event]))

(defprotocol EventListener
  (-listener-id [this])
  (-listener-fn [this]))

(defprotocol EventTarget
  (-listen [this type listener opts])
  (-unlisten [this type listener opts])
  (-build-event [this event-data])
  (-dispatch-event [this event]))


;; # Event

(defn prevent-default!
  "Prevent the default browser action."
  [event]
  (-prevent-default event))

(defn stop-propagation!
  "Stop event propagation."
  [event]
  (-stop-propagation event))

(extend-type goog.events.Event
  Event
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

(defn listener-id
  "Returns the event listener identity."
  [listener]
  (-listener-id listener))

(defn listener-fn
  "Returns the event listener function."
  [listener]
  (-listener-fn listener))

(extend-protocol EventListener
  function
  (-listener-id [f] f)
  (-listener-fn [f] f))


;; ## FnEventListener

(deftype FnEventListener [f id]
  EventListener
  (-listener-id [_] id)
  (-listener-fn [_] f))

(defn fn-event-listener
  "Create an event listener using the provided event listener id and function."
  [id f]
  (if (not= id f)
    (FnEventListener. f id)
    f))


;; # EventTarget

(defn- listen*
  [target type listener opts]
  (if (coll? type)
    (doseq [t type]
      (-listen target (keyword t) listener opts))
    (-listen target (keyword type) listener opts)))

(defn- unlisten*
  [target type listener opts]
  (if (coll? type)
    (doseq [t type]
      (-listen target (keyword t) listener opts))
    (-unlisten target (keyword type) listener opts)))

(defn listen!
  "Add an event listener. See target implementaiton for supported options."
  ([target type listener & opts]
     (listen* target type listener opts)))

(defn unlisten!
  "Remove an event listener added with listen. See target implementaiton for
  supported options."
  ([target type listener & opts]
     (unlisten* target type listener opts)))

(defn dispatch!
  "Dispatch an event. See target implementation for supported events."
  ([target event-data]
     (let [event (-build-event target event-data)]
       (-dispatch-event target event))))


;; # EventTarget Implementation

;; A mapping of argument vectors to goog event listener keys. This is used to
;; prevent multiple listeners with the same identity from being added to the
;; same target.
(def ^:private listeners (atom {}))

(defn- add-listener
  [token target type f]
  (when-not (contains? @listeners token)
    (let [key (gevt/listen target (name type) f)]
      (swap! listeners assoc token key))))

(defn- make-token
  [target type listener]
  [(goog/getUid target)
   (keyword type)
   (-listener-id listener)])

(extend-protocol EventTarget
  default ;js/EventTarget gevt/EventTarget gevt/Listenable
  (-listen [target type listener opts]
    (let [token (make-token target type listener)
          f (-listener-fn listener)]
      (add-listener token target type f)))

  (-unlisten [target type listener opts]
    (let [token (make-token target type listener)]
      (when-let [key (get @listeners token)]
        (gevt/unlistenByKey key)
        (swap! listeners dissoc token))))

  ;; See goog.events.EventLike
  (-build-event [target event-data]
    (cond (keyword? event-data) (name event-data)
          (map? event-data) (clj->js event-data)
          :else event-data))

  (-dispatch-event [target event]
    (gevt/dispatchEvent target event)))

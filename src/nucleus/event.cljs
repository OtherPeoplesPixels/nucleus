(ns nucleus.event
  "Abstract browser event system."
  (:require [nucleus.event.proto :as proto]
            [nucleus.event.impl :as impl])
  (:import [goog.events Event BrowserEvent EventTarget]))


;; # Event

(defn event
  "Returns a new event."
  ([type]
     (Event. (name type)))
  ([type target]
     (Event. (name type) target)))

(defn event? [x]
  (satisfies? proto/Event x))

(defn prevent-default!
  "Prevent the default browser action."
  [event]
  (proto/-prevent-default event))

(defn stop-propagation!
  "Stop event propagation."
  [event]
  (proto/-stop-propagation event))


;; # BrowserEvent

(defn browser-event
  "Returns a new event wrapping a native browser event."
  ([native-event]
     (BrowserEvent. native-event))
  ([native-event target]
     (BrowserEvent. native-event target)))

(defn browser-event? [x]
  (satisfies? proto/BrowserEvent x))

(defn original-event
  "Returns the wrapped native browser event."
  [event]
  (proto/-original-event event))


;; # EventListener

(defn event-listener? [x]
  (satisfies? proto/EventListener x))

(defn listener-id
  "Returns the event listener identity."
  [listener]
  (proto/-listener-id listener))

(defn listener-fn
  "Returns the event listener function."
  [listener]
  (proto/-listener-fn listener))

(defn wrap-listener-id
  "Wraps an existing event-listener with the provided listener-id."
  [listener id]
  (if (not= id (listener-id listener))
    (reify
      proto/EventListener
      (-listener-id [_] id)
      (-listener-fn [_]
        (proto/-listener-fn listener)))
    listener))


;; # EventTarget

(defn event-target []
  (EventTarget.))

(defn event-target? [x]
  (satisfies? proto/EventTarget x))

(defn- listen*
  [target type listener opts]
  (if (coll? type)
    (doseq [t type]
      (proto/-listen target (keyword t) listener opts))
    (proto/-listen target (keyword type) listener opts))
  nil)

(defn- unlisten*
  [target type listener opts]
  (if (coll? type)
    (doseq [t type]
      (proto/-unlisten target (keyword t) listener opts))
    (proto/-unlisten target (keyword type) listener opts))
  nil)

(defn listen!
  "Add an event listener. Returns nil. See target implementaiton for supported
  options."
  ([target type listener & opts]
     (listen* target type listener opts)))

(defn unlisten!
  "Remove an event listener added with listen. Returns nil. See target
  implementaiton for supported options."
  ([target type listener & opts]
     (unlisten* target type listener opts)))

(defn dispatch!
  "Dispatch an event. See target implementation for supported events."
  ([target event-data]
     (let [event (proto/-build-event target event-data)]
       (proto/-dispatch-event target event))))

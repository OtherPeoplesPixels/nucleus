(ns nucleus.event.proto)

(defprotocol Event
  (-prevent-default [event])
  (-stop-propagation [event]))

(defprotocol BrowserEvent
  (-original-event [event])
  ;;(-is-button [event])
  ;;(-is-mouse-action-button [event])
  )

(defprotocol EventListener
  (-listener-id [this])
  (-listener-fn [this]))

(defprotocol EventTarget
  (-listen [this type listener opts])
  (-unlisten [this type listener opts])
  (-build-event [this event-data])
  (-dispatch-event [this event]))

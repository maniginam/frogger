(ns frogger.input.touch
  "Touch input handling for mobile devices with swipe detection.")

(defonce touch-state (atom {:start-x nil
                            :start-y nil
                            :direction nil
                            :active? false}))

(def swipe-threshold 30)  ; Minimum distance in pixels to register a swipe
(def swipe-timeout 300)   ; Max time in ms for a swipe gesture

(defonce input-callback (atom nil))
(defonce touch-start-time (atom nil))

(defn calculate-swipe-direction
  "Calculates swipe direction from start and end coordinates."
  [start-x start-y end-x end-y]
  (let [dx (- end-x start-x)
        dy (- end-y start-y)
        abs-dx (js/Math.abs dx)
        abs-dy (js/Math.abs dy)]
    (when (or (> abs-dx swipe-threshold)
              (> abs-dy swipe-threshold))
      (if (> abs-dx abs-dy)
        ;; Horizontal swipe
        (if (pos? dx) :right :left)
        ;; Vertical swipe
        (if (pos? dy) :down :up)))))

(defn handle-touch-start [e]
  (let [touch (aget (.-touches e) 0)
        x (.-clientX touch)
        y (.-clientY touch)]
    (reset! touch-start-time (.now js/Date))
    (swap! touch-state assoc
           :start-x x
           :start-y y
           :direction nil
           :active? true)))

(defn handle-touch-move [e]
  (when (:active? @touch-state)
    (.preventDefault e)
    (let [touch (aget (.-touches e) 0)
          end-x (.-clientX touch)
          end-y (.-clientY touch)
          {:keys [start-x start-y]} @touch-state
          direction (calculate-swipe-direction start-x start-y end-x end-y)]
      (when direction
        (swap! touch-state assoc :direction direction)))))

(defn handle-touch-end [e]
  (when (:active? @touch-state)
    (let [elapsed (- (.now js/Date) @touch-start-time)
          {:keys [direction]} @touch-state]
      ;; Only register swipe if completed within timeout
      (when (and direction (< elapsed swipe-timeout))
        (swap! touch-state assoc :direction direction)
        (when-let [callback @input-callback]
          (callback {:type :swipe :action direction})))
      ;; Reset state after a brief moment to allow the input to be read
      (js/setTimeout
       #(swap! touch-state assoc
               :start-x nil
               :start-y nil
               :direction nil
               :active? false)
       50))))

(defn get-touch-direction
  "Returns the current touch direction, or nil if none."
  []
  (:direction @touch-state))

(defn clear-touch-direction!
  "Clears the current touch direction after it's been consumed."
  []
  (swap! touch-state assoc :direction nil))

(defn set-touch-callback!
  "Sets a callback function for touch events."
  [callback]
  (reset! input-callback callback))

(defn setup-touch-listeners!
  "Sets up touch event listeners on the document."
  []
  (let [options #js {:passive false}]
    (.addEventListener js/document "touchstart" handle-touch-start options)
    (.addEventListener js/document "touchmove" handle-touch-move options)
    (.addEventListener js/document "touchend" handle-touch-end options)
    (.addEventListener js/document "touchcancel" handle-touch-end options)))

(defn teardown-touch-listeners!
  "Removes touch event listeners."
  []
  (.removeEventListener js/document "touchstart" handle-touch-start)
  (.removeEventListener js/document "touchmove" handle-touch-move)
  (.removeEventListener js/document "touchend" handle-touch-end)
  (.removeEventListener js/document "touchcancel" handle-touch-end))

(defn is-touch-device?
  "Returns true if the device supports touch."
  []
  (or (exists? js/ontouchstart)
      (and (exists? js/navigator.maxTouchPoints)
           (> js/navigator.maxTouchPoints 0))))

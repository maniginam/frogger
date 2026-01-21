(ns frogger.input.keyboard
  "Keyboard input handling for the game."
  (:require [frogger.input.touch :as touch]))

(defonce pressed-keys (atom #{}))
(defonce input-callback (atom nil))

(def key-mappings
  {"ArrowUp" :up
   "ArrowDown" :down
   "ArrowLeft" :left
   "ArrowRight" :right
   "KeyW" :up
   "KeyS" :down
   "KeyA" :left
   "KeyD" :right
   "Space" :ability
   "KeyP" :pause
   "Escape" :pause
   "Enter" :confirm})

(defn key->action [key-code]
  (get key-mappings key-code))

(defn handle-keydown [e]
  (let [code (.-code e)
        action (key->action code)]
    (when action
      (.preventDefault e)
      (swap! pressed-keys conj action)
      (when-let [callback @input-callback]
        (callback {:type :keydown :action action})))))

(defn handle-keyup [e]
  (let [code (.-code e)
        action (key->action code)]
    (when action
      (swap! pressed-keys disj action)
      (when-let [callback @input-callback]
        (callback {:type :keyup :action action})))))

(defn get-direction-input
  "Returns the current direction input, or nil if none.
   Checks both keyboard and touch inputs."
  []
  (let [touch-dir (touch/get-touch-direction)]
    (when touch-dir
      (touch/clear-touch-direction!))
    (or touch-dir
        (cond
          (@pressed-keys :up) :up
          (@pressed-keys :down) :down
          (@pressed-keys :left) :left
          (@pressed-keys :right) :right
          :else nil))))

(defn ability-pressed?
  "Returns true if the ability key is pressed."
  []
  (@pressed-keys :ability))

(defn pause-pressed?
  "Returns true if the pause key is pressed."
  []
  (@pressed-keys :pause))

(defn get-current-input
  "Returns the current input state."
  []
  {:direction (get-direction-input)
   :ability-pressed? (ability-pressed?)})

(defn set-input-callback!
  "Sets a callback function for input events."
  [callback]
  (reset! input-callback callback))

(defn setup-keyboard-listeners!
  "Sets up keyboard and touch event listeners."
  []
  (.addEventListener js/window "keydown" handle-keydown)
  (.addEventListener js/window "keyup" handle-keyup)
  (touch/setup-touch-listeners!))

(defn teardown-keyboard-listeners!
  "Removes keyboard and touch event listeners."
  []
  (.removeEventListener js/window "keydown" handle-keydown)
  (.removeEventListener js/window "keyup" handle-keyup)
  (touch/teardown-touch-listeners!))

(defn clear-pressed-keys!
  "Clears all pressed keys."
  []
  (reset! pressed-keys #{}))

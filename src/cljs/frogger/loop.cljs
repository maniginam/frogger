(ns frogger.loop
  "Game loop using requestAnimationFrame."
  (:require [frogger.core :as core]
            [frogger.render.canvas :as canvas]
            [frogger.render.effects :as effects]
            [frogger.input.keyboard :as keyboard]
            [frogger.ui.hud :as hud]
            [frogger.state :as state]))

(defonce game-state-atom (atom nil))
(defonce last-time-atom (atom nil))
(defonce running-atom (atom false))
(defonce animation-frame-id (atom nil))

(defn get-game-state []
  @game-state-atom)

(defn set-game-state! [new-state]
  (reset! game-state-atom new-state))

(defn apply-input [game-state]
  (let [input (keyboard/get-current-input)]
    (state/set-input game-state input)))

(defn game-loop [timestamp]
  (when @running-atom
    (let [last-time (or @last-time-atom timestamp)
          dt (min (- timestamp last-time) 100)
          current-state @game-state-atom]
      (reset! last-time-atom timestamp)
      (when (and current-state (= :playing (:screen current-state)))
        (let [state-with-input (apply-input current-state)
              new-state (core/game-tick state-with-input dt)]
          (reset! game-state-atom new-state)
          (canvas/render new-state)
          (effects/update-effects!)
          (effects/render-effects (canvas/get-ctx))
          (hud/update-hud! new-state)))
      (reset! animation-frame-id (js/requestAnimationFrame game-loop)))))

(defn start-loop! []
  (when-not @running-atom
    (reset! running-atom true)
    (reset! last-time-atom nil)
    (reset! animation-frame-id (js/requestAnimationFrame game-loop))))

(defn stop-loop! []
  (reset! running-atom false)
  (when @animation-frame-id
    (js/cancelAnimationFrame @animation-frame-id)
    (reset! animation-frame-id nil)))

(defn pause-loop! []
  (stop-loop!))

(defn resume-loop! []
  (start-loop!))

(defn restart-loop! [new-state]
  (stop-loop!)
  (reset! game-state-atom new-state)
  (reset! last-time-atom nil)
  (start-loop!))

(defn is-running? []
  @running-atom)

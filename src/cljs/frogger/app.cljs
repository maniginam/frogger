(ns frogger.app
  "Application state management and coordination."
  (:require [frogger.core :as core]
            [frogger.state :as state]
            [frogger.loop :as game-loop]
            [frogger.render.canvas :as canvas]
            [frogger.render.effects :as effects]
            [frogger.input.keyboard :as keyboard]
            [frogger.ui.menu :as menu]
            [frogger.ui.hud :as hud]
            [frogger.ui.screens :as screens]
            [frogger.game.events :as events]))

(defonce app-state (atom {:screen :menu
                          :character-id :forest
                          :theme-id :forest}))

(defn start-game! [character-id theme-id]
  (let [game-state (core/initialize-game character-id theme-id)]
    (swap! app-state assoc
           :screen :playing
           :character-id character-id
           :theme-id theme-id)
    (menu/hide-menu!)
    (hud/show-hud!)
    (game-loop/set-game-state! game-state)
    (game-loop/start-loop!)))

(defn restart-game! []
  (let [{:keys [character-id theme-id]} @app-state
        game-state (core/initialize-game character-id theme-id)]
    (effects/clear-effects!)
    (screens/hide-game-over!)
    (game-loop/restart-loop! game-state)))

(defn return-to-menu! []
  (game-loop/stop-loop!)
  (effects/clear-effects!)
  (screens/hide-game-over!)
  (hud/hide-hud!)
  (menu/show-menu!)
  (swap! app-state assoc :screen :menu))

(defn toggle-pause! []
  (let [current-state (game-loop/get-game-state)]
    (if (:paused? current-state)
      (do
        (screens/hide-pause-overlay!)
        (game-loop/set-game-state! (assoc current-state :paused? false))
        (game-loop/resume-loop!))
      (do
        (game-loop/pause-loop!)
        (game-loop/set-game-state! (assoc current-state :paused? true))
        (screens/show-pause-overlay!)))))

(defn handle-input-event [{:keys [type action]}]
  (when (= type :keydown)
    (case action
      :pause (when (= :playing (:screen @app-state))
               (toggle-pause!))
      nil)))

(defn watch-game-state []
  (add-watch game-loop/game-state-atom :app-watcher
             (fn [_ _ old-state new-state]
               (when (and old-state new-state)
                 (when (and (not= (:screen old-state) :game-over)
                            (= (:screen new-state) :game-over))
                   (game-loop/stop-loop!)
                   (screens/show-game-over! (:score new-state)))
                 (when (and (< (:lives old-state) (:lives new-state) 0)
                            (pos? (:lives new-state)))
                   (effects/trigger-death-effect! (:frog old-state))
                   (hud/flash-lives!))
                 (when (> (:score new-state) (:score old-state))
                   (hud/flash-score!))))))

(defn init-app! []
  (keyboard/setup-keyboard-listeners!)
  (keyboard/set-input-callback! handle-input-event)
  (menu/init-menu! start-game!)
  (screens/init-screens! restart-game!)
  (watch-game-state)
  (menu/show-menu!))

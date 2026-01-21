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
            [frogger.game.events :as events]
            [frogger.game.levels :as levels]
            [frogger.audio.system :as audio]))

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
    (audio/resume-audio!)
    (audio/start-theme-music! theme-id)
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
  (audio/stop-music!)
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

(defn- frog-moved?
  "Check if frog position changed significantly (a hop)."
  [old-state new-state]
  (when (and old-state new-state (:frog old-state) (:frog new-state))
    (let [old-frog (:frog old-state)
          new-frog (:frog new-state)
          dx (- (:x new-frog) (:x old-frog))
          dy (- (:y new-frog) (:y old-frog))]
      ;; Consider it a hop if moved more than half a cell (not just platform drift)
      (or (> (js/Math.abs dx) 20)
          (> (js/Math.abs dy) 20)))))

(defn- goals-changed?
  "Check if a new goal was reached."
  [old-state new-state]
  (let [old-reached (count (filter :reached? (:goals old-state)))
        new-reached (count (filter :reached? (:goals new-state)))]
    (> new-reached old-reached)))

(defn watch-game-state []
  (add-watch game-loop/game-state-atom :app-watcher
             (fn [_ _ old-state new-state]
               (when (and old-state new-state)
                 ;; Game over
                 (when (and (not= (:screen old-state) :game-over)
                            (= (:screen new-state) :game-over))
                   (game-loop/stop-loop!)
                   (audio/stop-music!)
                   (audio/play-game-over-sound)
                   (screens/show-game-over! (:score new-state)))
                 ;; Frog died (lost a life)
                 (when (and (> (:lives old-state) (:lives new-state))
                            (pos? (:lives new-state)))
                   (effects/trigger-death-effect! (:frog old-state))
                   (audio/play-death-sound (:character-id new-state))
                   (hud/flash-lives!))
                 ;; Score increased (checkpoint or time bonus)
                 (when (> (:score new-state) (:score old-state))
                   (hud/flash-score!))
                 ;; Goal reached
                 (when (goals-changed? old-state new-state)
                   (audio/play-goal-sound))
                 ;; Level complete
                 (when (and (not= (:level old-state) (:level new-state))
                            (> (:level new-state) (:level old-state)))
                   (let [theme-id (:theme-id new-state)
                        new-level (:level new-state)
                        level-name (levels/get-level-name theme-id new-level)]
                    (audio/play-level-complete-sound)
                    (screens/show-level-complete! new-level (:score new-state) level-name)))
                 ;; Frog hopped
                 (when (frog-moved? old-state new-state)
                   (audio/play-hop-sound (:character-id new-state)))))))

(defn init-app! []
  (audio/init-audio!)
  (keyboard/setup-keyboard-listeners!)
  (keyboard/set-input-callback! handle-input-event)
  (menu/init-menu! start-game!)
  (screens/init-screens! restart-game!)
  (watch-game-state)
  (menu/show-menu!))

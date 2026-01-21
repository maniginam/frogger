(ns frogger.core
  "Core game loop logic - pure game tick function."
  (:require [frogger.protocols :as p]
            [frogger.state :as state]
            [frogger.entities.frog :as frog]
            [frogger.entities.obstacle :as obstacle]
            [frogger.entities.platform :as platform]
            [frogger.entities.goal :as goal]
            [frogger.physics.collision :as collision]
            [frogger.physics.movement :as movement]
            [frogger.game.rules :as rules]
            [frogger.game.events :as events]
            [frogger.game.levels :as levels]
            [frogger.characters.registry :as characters]))

(defn process-input
  "Processes input and returns events to dispatch."
  [game-state]
  (let [input (:input game-state)
        direction (:direction input)
        ability-pressed? (:ability-pressed? input)]
    (cond-> []
      direction (conj (events/create-event :move :direction direction))
      ability-pressed? (conj (events/create-event :ability)))))

(defn update-entities
  "Updates all game entities for the time delta."
  [game-state dt]
  (-> game-state
      (update :frog #(when % (p/update-entity % dt)))
      (update :obstacles #(obstacle/update-obstacles % dt))
      (update :platforms #(platform/update-platforms % dt))))

(defn check-collisions
  "Checks all collisions and returns events."
  [game-state]
  (let [frog (:frog game-state)
        obstacles (:obstacles game-state)
        platforms (:platforms game-state)
        goals (:goals game-state)]
    (when frog
      (let [obstacle-hit (collision/check-obstacle-collisions frog obstacles)
            current-platform (collision/check-platform-collisions frog platforms)
            goal-reached (collision/check-goal-collisions frog goals)
            in-water? (movement/is-in-water-zone? (:y frog))
            drowned? (and in-water?
                          (not current-platform)
                          (not (:invincible? frog)))]
        {:obstacle-hit obstacle-hit
         :platform current-platform
         :goal-reached goal-reached
         :drowned? drowned?}))))

(defn apply-platform-movement
  "Applies platform velocity to frog when riding."
  [game-state platform dt]
  (if (and platform (:frog game-state))
    (let [vx (:vx platform 0)
          dx (* vx (/ dt 1000.0))]
      (update game-state :frog frog/apply-platform-velocity dx))
    game-state))

(defn handle-collision-results
  "Handles collision results and returns updated state."
  [game-state collision-results dt]
  (let [{:keys [obstacle-hit platform goal-reached drowned?]} collision-results]
    (cond
      obstacle-hit
      (events/dispatch-event game-state (events/create-event :death :cause :obstacle))

      drowned?
      (events/dispatch-event game-state (events/create-event :death :cause :water))

      goal-reached
      (let [goal-index (:index goal-reached)]
        (events/dispatch-event game-state (events/create-event :goal-reached :goal-index goal-index)))

      platform
      (-> game-state
          (assoc-in [:frog :on-platform?] true)
          (apply-platform-movement platform dt))

      :else
      game-state)))

(defn update-timer
  "Updates the game timer."
  [game-state dt]
  (let [new-time (- (:time-remaining game-state) dt)]
    (if (<= new-time 0)
      (events/dispatch-event game-state (events/create-event :death :cause :timeout))
      (assoc game-state :time-remaining new-time))))

(defn check-game-conditions
  "Checks for game over or level complete conditions."
  [game-state]
  (cond
    (rules/check-game-over game-state)
    (events/dispatch-event game-state (events/create-event :game-over))

    (rules/check-level-complete game-state)
    (events/dispatch-event game-state (events/create-event :level-complete))

    :else
    game-state))

(defn game-tick
  "Main game tick function. Pure function: (state, dt) -> state"
  [game-state dt]
  (if (or (:paused? game-state)
          (not= :playing (:screen game-state)))
    game-state
    (let [input-events (process-input game-state)
          state-after-input (events/dispatch-events game-state input-events)
          state-cleared-input (state/clear-input state-after-input)
          state-after-update (update-entities state-cleared-input dt)
          collision-results (check-collisions state-after-update)
          state-after-collisions (handle-collision-results state-after-update collision-results dt)
          state-after-timer (update-timer state-after-collisions dt)
          final-state (check-game-conditions state-after-timer)]
      final-state)))

(defn initialize-game
  "Initializes a new game with the selected character and theme."
  [character-id theme-id]
  (let [character (characters/get-character character-id)
        initial-state (state/make-initial-state {:character-id character-id
                                                 :theme-id theme-id})
        frog (frog/create-frog {:character-id character-id
                                :color (:color character)
                                :speed-multiplier (:speed-multiplier character 1.0)
                                :score-multiplier (:score-multiplier character 1.0)
                                :ability-type (:ability-type character)
                                :ability-state (:ability-state character {})})]
    (-> initial-state
        (assoc :frog frog)
        (levels/apply-level-to-state 1)
        (assoc :screen :playing))))

(defn restart-game
  "Restarts the game with the same character and theme."
  [game-state]
  (initialize-game (:character-id game-state) (:theme-id game-state)))

(ns frogger.game.events
  "Event dispatch and handling for game state transitions."
  (:require [frogger.protocols :as p]
            [frogger.state :as state]
            [frogger.entities.frog :as frog]
            [frogger.entities.goal :as goal]
            [frogger.game.rules :as rules]))

(defmulti handle-event
  "Handles game events by type."
  (fn [game-state event] (:type event)))

(defmethod handle-event :move
  [game-state {:keys [direction]}]
  (if-let [current-frog (:frog game-state)]
    (let [moved-frog (p/move current-frog direction)
          score-multiplier (frog/get-score-multiplier current-frog)
          hop-score (rules/calculate-hop-score direction score-multiplier)]
      (-> game-state
          (assoc :frog moved-frog)
          (update :score + hop-score)))
    game-state))

(defmethod handle-event :death
  [game-state {:keys [cause]}]
  (-> game-state
      (update :lives dec)
      (update :frog frog/reset-position)
      (assoc :death-cause cause)))

(defmethod handle-event :goal-reached
  [game-state {:keys [goal-index]}]
  (let [current-frog (:frog game-state)
        score-multiplier (frog/get-score-multiplier current-frog)
        time-remaining (:time-remaining game-state)
        goal-score (rules/calculate-goal-score score-multiplier time-remaining)]
    (-> game-state
        (update :score + goal-score)
        (update-in [:goals goal-index] goal/mark-goal-reached)
        (update :frog frog/reset-position))))

(defmethod handle-event :level-complete
  [game-state _]
  (let [current-frog (:frog game-state)
        score-multiplier (frog/get-score-multiplier current-frog)
        level-score (rules/calculate-level-complete-score score-multiplier)]
    (-> game-state
        (update :score + level-score)
        (update :level inc)
        (assoc :time-remaining 60000)
        (update :goals goal/reset-goals)
        (update :frog frog/reset-position))))

(defmethod handle-event :game-over
  [game-state _]
  (assoc game-state :screen :game-over))

(defmethod handle-event :restart
  [game-state _]
  (state/make-initial-state {:character-id (:character-id game-state)
                             :theme-id (:theme-id game-state)}))

(defmethod handle-event :pause
  [game-state _]
  (update game-state :paused? not))

(defmethod handle-event :ability
  [game-state _]
  (if-let [current-frog (:frog game-state)]
    (assoc game-state :frog (p/activate current-frog))
    game-state))

(defmethod handle-event :start-game
  [game-state {:keys [character-id theme-id]}]
  (-> game-state
      (assoc :character-id (or character-id (:character-id game-state)))
      (assoc :theme-id (or theme-id (:theme-id game-state)))
      (assoc :screen :playing)))

(defmethod handle-event :return-to-menu
  [game-state _]
  (state/make-initial-state {:character-id (:character-id game-state)
                             :theme-id (:theme-id game-state)}))

(defmethod handle-event :default
  [game-state event]
  game-state)

(defn dispatch-event
  "Dispatches a single event to the event handler."
  [game-state event]
  (handle-event game-state event))

(defn dispatch-events
  "Dispatches multiple events in sequence."
  [game-state events]
  (reduce handle-event game-state events))

(defn create-event
  "Creates an event map."
  [event-type & {:as params}]
  (merge {:type event-type} params))

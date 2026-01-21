(ns frogger.game.rules
  "Game rules for scoring, lives, and win/lose conditions."
  (:require [frogger.state :as state]
            [frogger.entities.goal :as goal]))

(def points-per-hop 10)
(def points-per-goal 200)
(def points-per-level 1000)
(def points-time-bonus-multiplier 10)

(defn calculate-hop-score
  "Calculates score for a hop based on direction and multiplier."
  [direction score-multiplier]
  (let [base-points (if (= direction :up) points-per-hop 0)]
    (int (* base-points score-multiplier))))

(defn calculate-goal-score
  "Calculates score for reaching a goal."
  [score-multiplier time-remaining]
  (let [time-bonus (int (* (/ time-remaining 1000) points-time-bonus-multiplier))]
    (int (* (+ points-per-goal time-bonus) score-multiplier))))

(defn calculate-level-complete-score
  "Calculates bonus score for completing a level."
  [score-multiplier]
  (int (* points-per-level score-multiplier)))

(defn check-death-conditions
  "Checks if the frog should die based on current state."
  [frog state]
  (let [y (:y frog)
        on-platform? (:on-platform? frog)
        invincible? (:invincible? frog)
        in-water? (and (>= y (* 1 state/cell-size))
                       (< y (* 7 state/cell-size)))]
    (cond
      (:dead? frog) :obstacle
      (and in-water? (not on-platform?) (not invincible?)) :water
      :else nil)))

(defn apply-death
  "Applies death effects to game state."
  [game-state cause]
  (-> game-state
      (update :lives dec)
      (assoc :death-cause cause)))

(defn apply-goal-reached
  "Applies effects when a goal is reached."
  [game-state goal-index score-multiplier time-remaining]
  (let [score-gain (calculate-goal-score score-multiplier time-remaining)]
    (-> game-state
        (update :score + score-gain)
        (update-in [:goals goal-index] goal/mark-goal-reached))))

(defn apply-level-complete
  "Applies effects when all goals are reached."
  [game-state score-multiplier]
  (let [score-gain (calculate-level-complete-score score-multiplier)]
    (-> game-state
        (update :score + score-gain)
        (update :level inc)
        (assoc :time-remaining 60000))))

(defn check-game-over
  "Returns true if the game is over."
  [game-state]
  (<= (:lives game-state) 0))

(defn check-level-complete
  "Returns true if all goals have been reached."
  [game-state]
  (goal/all-goals-reached? (:goals game-state)))

(defn get-difficulty-multiplier
  "Returns speed multiplier based on current level."
  [level]
  (+ 1.0 (* 0.1 (min (dec level) 9))))

(defn should-spawn-bonus?
  "Returns true if a bonus item should spawn (random chance)."
  []
  (< (rand) 0.001))

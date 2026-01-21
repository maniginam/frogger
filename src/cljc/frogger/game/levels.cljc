(ns frogger.game.levels
  "Level generation and configuration."
  (:require [frogger.state :as state]
            [frogger.entities.obstacle :as obstacle]
            [frogger.entities.platform :as platform]
            [frogger.entities.goal :as goal]
            [frogger.entities.checkpoint :as checkpoint]
            [frogger.game.rules :as rules]
            [frogger.themes.registry :as themes]))

(def base-obstacle-speed 80)
(def base-platform-speed 50)

(defn generate-road-obstacles
  "Generates obstacles for road lanes based on level."
  [level]
  (let [speed-mult (rules/get-difficulty-multiplier level)
        base-speed (* base-obstacle-speed speed-mult)]
    (concat
     (obstacle/create-row-obstacles
      {:row 13 :obstacle-type :car :direction :left
       :speed base-speed :count 3 :spacing 180})
     (obstacle/create-row-obstacles
      {:row 12 :obstacle-type :truck :direction :right
       :speed (* base-speed 0.7) :count 2 :spacing 250})
     (obstacle/create-row-obstacles
      {:row 11 :obstacle-type :car :direction :left
       :speed (* base-speed 1.2) :count 3 :spacing 160})
     (obstacle/create-row-obstacles
      {:row 10 :obstacle-type :bus :direction :right
       :speed (* base-speed 0.6) :count 2 :spacing 300})
     (obstacle/create-row-obstacles
      {:row 9 :obstacle-type :race-car :direction :left
       :speed (* base-speed 1.5) :count 4 :spacing 140}))))

(defn generate-river-platforms
  "Generates platforms for river lanes based on level."
  [level]
  (let [speed-mult (rules/get-difficulty-multiplier level)
        base-speed (* base-platform-speed speed-mult)]
    (concat
     (platform/create-row-platforms
      {:row 6 :platform-type :log-medium :direction :right
       :speed base-speed :count 3 :spacing 200})
     (platform/create-row-platforms
      {:row 5 :platform-type :turtle :direction :left
       :speed (* base-speed 0.8) :count 4 :spacing 150})
     (platform/create-row-platforms
      {:row 4 :platform-type :log-large :direction :right
       :speed (* base-speed 0.6) :count 2 :spacing 280})
     (platform/create-row-platforms
      {:row 3 :platform-type :log-small :direction :left
       :speed (* base-speed 1.1) :count 4 :spacing 160})
     (platform/create-row-platforms
      {:row 2 :platform-type :turtle :direction :right
       :speed base-speed :count 3 :spacing 180})
     (platform/create-row-platforms
      {:row 1 :platform-type :log-medium :direction :left
       :speed (* base-speed 0.9) :count 3 :spacing 190}))))

(defn generate-goals
  "Generates goal zones."
  []
  (goal/create-goals))

(defn generate-checkpoints
  "Generates checkpoints in the middle safe zone."
  []
  (checkpoint/create-checkpoints))

(defn generate-level
  "Generates a complete level with all entities."
  [level]
  {:obstacles (vec (generate-road-obstacles level))
   :platforms (vec (generate-river-platforms level))
   :goals (generate-goals)
   :checkpoints (generate-checkpoints)})

(defn apply-level-to-state
  "Applies generated level entities to game state."
  [game-state level-num]
  (let [level-data (generate-level level-num)]
    (-> game-state
        (assoc :obstacles (:obstacles level-data))
        (assoc :platforms (:platforms level-data))
        (assoc :goals (:goals level-data))
        (assoc :checkpoints (:checkpoints level-data))
        (assoc :level level-num))))

(defn reset-level
  "Resets the current level, keeping the same level number."
  [game-state]
  (apply-level-to-state game-state (:level game-state)))

(defn advance-level
  "Advances to the next level."
  [game-state]
  (apply-level-to-state game-state (inc (:level game-state))))

(defn get-level-config
  "Gets configuration for a specific level from theme or defaults."
  ([level]
   (get-level-config level :forest))
  ([level theme-id]
   (if-let [theme-config (themes/get-level-config theme-id level)]
     theme-config
     ;; Fallback defaults
     {:name (str "Level " level)
      :time-limit (max 30000 (- 60000 (* 5000 (dec level))))
      :speed-mult (+ 1.0 (* 0.15 (dec level)))})))

(defn get-level-name
  "Gets the name of the current level for a theme."
  [theme-id level]
  (:name (get-level-config level theme-id)))

(defn get-level-time-limit
  "Gets the time limit for a level in a theme."
  [theme-id level]
  (:time-limit (get-level-config level theme-id)))

(defn get-level-speed-multiplier
  "Gets the speed multiplier for a level in a theme."
  [theme-id level]
  (:speed-mult (get-level-config level theme-id)))

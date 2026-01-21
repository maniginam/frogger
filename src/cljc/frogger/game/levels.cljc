(ns frogger.game.levels
  "Level generation and configuration."
  (:require [frogger.state :as state]
            [frogger.entities.obstacle :as obstacle]
            [frogger.entities.platform :as platform]
            [frogger.entities.goal :as goal]
            [frogger.game.rules :as rules]))

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

(defn generate-level
  "Generates a complete level with all entities."
  [level]
  {:obstacles (vec (generate-road-obstacles level))
   :platforms (vec (generate-river-platforms level))
   :goals (generate-goals)})

(defn apply-level-to-state
  "Applies generated level entities to game state."
  [game-state level-num]
  (let [level-data (generate-level level-num)]
    (-> game-state
        (assoc :obstacles (:obstacles level-data))
        (assoc :platforms (:platforms level-data))
        (assoc :goals (:goals level-data))
        (assoc :level level-num))))

(defn reset-level
  "Resets the current level, keeping the same level number."
  [game-state]
  (apply-level-to-state game-state (:level game-state)))

(defn advance-level
  "Advances to the next level."
  [game-state]
  (apply-level-to-state game-state (inc (:level game-state))))

(def level-configs
  "Level-specific configurations for variety."
  {1 {:name "Classic Crossing"
      :time-limit 60000}
   2 {:name "Rush Hour"
      :time-limit 55000
      :obstacle-density 1.2}
   3 {:name "River Rapids"
      :time-limit 50000
      :platform-speed 1.3}
   4 {:name "Night Mode"
      :time-limit 55000
      :visibility :low}
   5 {:name "The Gauntlet"
      :time-limit 45000
      :obstacle-density 1.5
      :platform-speed 1.2}})

(defn get-level-config
  "Gets configuration for a specific level."
  [level]
  (get level-configs level (get level-configs 1)))

(ns frogger.entities.goal
  "Goal zone entities where frogs need to land."
  (:require [frogger.protocols :as p]
            [frogger.state :as state]))

(defrecord Goal [x y width height reached? goal-type color index]

  p/Entity
  (get-position [this]
    {:x x :y y})

  (get-bounds [this]
    {:x x :y y :width width :height height})

  (get-type [_]
    :goal)

  p/Collidable
  (collides? [this other]
    (when-not reached?
      (let [{ox :x oy :y ow :width oh :height} (p/get-bounds other)]
        (and (< x (+ ox ow))
             (< ox (+ x width))
             (< y (+ oy oh))
             (< oy (+ y height))))))

  (on-collision [this other]
    (assoc this :reached? true))

  p/Renderable
  (get-sprite-key [this]
    (if reached? :goal-reached :goal-empty))

  (get-color [this]
    (if reached? "#4CAF50" color)))

(def goal-width 50)
(def goal-height 40)
(def num-goals 5)

(defn calculate-goal-positions
  "Calculates x positions for goals evenly spaced across the top."
  []
  (let [total-width state/canvas-width
        spacing (/ total-width num-goals)
        offset (/ (- spacing goal-width) 2)]
    (for [i (range num-goals)]
      (+ (* i spacing) offset))))

(defn create-goal
  "Creates a goal entity."
  [{:keys [x y index]
    :or {y 0 index 0}}]
  (map->Goal {:x x
              :y y
              :width goal-width
              :height goal-height
              :reached? false
              :goal-type :lily-pad
              :color "#1B5E20"
              :index index}))

(defn create-goals
  "Creates all goal zones for a level."
  []
  (let [positions (calculate-goal-positions)]
    (vec (map-indexed
          (fn [i x]
            (create-goal {:x x :index i}))
          positions))))

(defn mark-goal-reached
  "Marks a goal as reached."
  [goal]
  (assoc goal :reached? true))

(defn reset-goals
  "Resets all goals to unreached state."
  [goals]
  (mapv #(assoc % :reached? false) goals))

(def goals-required-to-advance 3)

(defn count-reached-goals
  "Returns the number of goals that have been reached."
  [goals]
  (count (filter :reached? goals)))

(defn all-goals-reached?
  "Returns true if enough goals have been reached to advance (3 frogs)."
  [goals]
  (>= (count-reached-goals goals) goals-required-to-advance))

(defn find-unreached-goal
  "Finds the first unreached goal, or nil if all reached."
  [goals]
  (first (filter #(not (:reached? %)) goals)))

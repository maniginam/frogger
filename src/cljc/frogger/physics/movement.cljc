(ns frogger.physics.movement
  "Movement calculations for game entities."
  (:require [frogger.state :as state]))

(defn calculate-position
  "Calculates new position given current position, velocity, and delta time."
  [{:keys [x y]} {:keys [vx vy]} dt]
  {:x (+ x (* vx (/ dt 1000.0)))
   :y (+ y (* vy (/ dt 1000.0)))})

(defn clamp-to-bounds
  "Clamps position to game boundaries."
  [{:keys [x y width height]}]
  {:x (max 0 (min x (- state/canvas-width width)))
   :y (max 0 (min y (- state/canvas-height height)))})

(defn wrap-horizontal
  "Wraps entity horizontally (for obstacles/platforms that loop)."
  [{:keys [x width] :as entity}]
  (cond
    (> x state/canvas-width)
    (assoc entity :x (- 0 width))

    (< (+ x width) 0)
    (assoc entity :x state/canvas-width)

    :else entity))

(defn move-entity
  "Moves an entity by its velocity over delta time."
  [entity dt]
  (let [{:keys [x y vx vy width height]
         :or {vx 0 vy 0}} entity
        new-x (+ x (* vx (/ dt 1000.0)))
        new-y (+ y (* vy (/ dt 1000.0)))]
    (assoc entity :x new-x :y new-y)))

(defn move-and-wrap
  "Moves entity and wraps horizontally."
  [entity dt]
  (-> entity
      (move-entity dt)
      wrap-horizontal))

(defn direction-to-delta
  "Converts direction keyword to [dx dy] delta."
  [direction distance]
  (case direction
    :up [0 (- distance)]
    :down [0 distance]
    :left [(- distance) 0]
    :right [distance 0]
    [0 0]))

(defn apply-velocity-to-rider
  "Applies a platform's velocity to an entity riding on it."
  [rider platform dt]
  (let [vx (:vx platform 0)
        dx (* vx (/ dt 1000.0))
        new-x (+ (:x rider) dx)]
    (assoc rider :x (max 0 (min new-x (- state/canvas-width (:width rider)))))))

(defn is-off-screen?
  "Returns true if entity is completely off screen."
  [{:keys [x y width height]}]
  (or (< (+ x width) 0)
      (> x state/canvas-width)
      (< (+ y height) 0)
      (> y state/canvas-height)))

(defn is-in-water-zone?
  "Returns true if y position is in the water/river zone."
  [y]
  (and (>= y (* 1 state/cell-size))
       (< y (* 7 state/cell-size))))

(defn is-in-road-zone?
  "Returns true if y position is in the road zone."
  [y]
  (and (>= y (* 9 state/cell-size))
       (< y (* 14 state/cell-size))))

(defn is-in-safe-zone?
  "Returns true if y position is in a safe zone."
  [y]
  (or (< y state/cell-size)
      (and (>= y (* 7 state/cell-size))
           (< y (* 9 state/cell-size)))
      (>= y (* 14 state/cell-size))))

(defn get-row
  "Returns the row number for a y position."
  [y]
  (int (/ y state/cell-size)))

(ns frogger.entities.frog
  "Frog entity implementation with movement and abilities."
  (:require [frogger.protocols :as p]
            [frogger.state :as state]))

(def default-hop-distance state/cell-size)
(def default-hop-cooldown 150)
(def default-invincibility-duration 1000)

(defrecord Frog [x y width height
                 character-id color
                 direction facing
                 hop-cooldown hop-cooldown-remaining
                 invincible? invincibility-remaining
                 ability-state
                 speed-multiplier score-multiplier
                 on-platform?]

  p/Entity
  (get-position [this]
    {:x x :y y})

  (get-bounds [this]
    {:x x :y y :width width :height height})

  (get-type [_]
    :frog)

  p/Movable
  (move [this direction]
    (if (> hop-cooldown-remaining 0)
      this
      (let [hop-dist (* default-hop-distance speed-multiplier)
            [dx dy] (case direction
                      :up [0 (- hop-dist)]
                      :down [0 hop-dist]
                      :left [(- hop-dist) 0]
                      :right [hop-dist 0]
                      [0 0])
            new-x (+ x dx)
            new-y (+ y dy)
            clamped-x (max 0 (min new-x (- state/canvas-width width)))
            clamped-y (max 0 (min new-y (- state/canvas-height height)))]
        (assoc this
               :x clamped-x
               :y clamped-y
               :direction direction
               :facing direction
               :hop-cooldown-remaining hop-cooldown))))

  (get-velocity [this]
    {:vx 0 :vy 0})

  p/Collidable
  (collides? [this other]
    (let [{:keys [x y width height]} (p/get-bounds this)
          other-bounds (p/get-bounds other)
          ox (:x other-bounds)
          oy (:y other-bounds)
          ow (:width other-bounds)
          oh (:height other-bounds)]
      (and (< x (+ ox ow))
           (< ox (+ x width))
           (< y (+ oy oh))
           (< oy (+ y height)))))

  (on-collision [this other]
    (case (p/get-type other)
      :obstacle (if invincible?
                  this
                  (assoc this :dead? true))
      :platform (assoc this :on-platform? true)
      :goal (assoc this :reached-goal? true)
      this))

  p/Ability
  (can-activate? [this]
    (get-in ability-state [:can-activate?] false))

  (activate [this]
    (if (p/can-activate? this)
      (case (get-in ability-state [:type])
        :double-jump (if (> (get-in ability-state [:charges] 0) 0)
                       (-> this
                           (update-in [:ability-state :charges] dec)
                           (assoc :hop-cooldown-remaining 0))
                       this)
        :phase-through (assoc this
                              :invincible? true
                              :invincibility-remaining (* 2 default-invincibility-duration))
        :tank-invincibility (assoc this
                                   :invincible? true
                                   :invincibility-remaining (* 2 default-invincibility-duration))
        this)
      this))

  (get-ability-state [this]
    ability-state)

  p/Renderable
  (get-sprite-key [this]
    (keyword (str (name character-id) "-" (name (or facing :up)))))

  (get-color [this]
    color)

  p/Updateable
  (update-entity [this dt]
    (-> this
        (update :hop-cooldown-remaining #(max 0 (- % dt)))
        (update :invincibility-remaining #(max 0 (- % dt)))
        (assoc :invincible? (> invincibility-remaining 0))
        (assoc :on-platform? false)
        (assoc :dead? false)
        (assoc :reached-goal? false))))

(defn create-frog
  "Creates a new frog entity with the given character configuration."
  [{:keys [character-id color x y
           speed-multiplier score-multiplier
           ability-type ability-state]
    :or {x (- (/ state/canvas-width 2) (/ state/cell-size 2))
         y (- state/canvas-height state/cell-size)
         speed-multiplier 1.0
         score-multiplier 1.0
         ability-type nil
         ability-state {}}}]
  (map->Frog {:x x
              :y y
              :width (- state/cell-size 4)
              :height (- state/cell-size 4)
              :character-id character-id
              :color color
              :direction nil
              :facing :up
              :hop-cooldown default-hop-cooldown
              :hop-cooldown-remaining 0
              :invincible? false
              :invincibility-remaining 0
              :ability-state (merge {:type ability-type :can-activate? (some? ability-type)}
                                    ability-state)
              :speed-multiplier speed-multiplier
              :score-multiplier score-multiplier
              :on-platform? false
              :dead? false
              :reached-goal? false}))

(defn reset-position
  "Resets frog to starting position with brief invincibility."
  [frog]
  (assoc frog
         :x (- (/ state/canvas-width 2) (/ state/cell-size 2))
         :y (- state/canvas-height state/cell-size)
         :facing :up
         :direction nil
         :on-platform? false
         :dead? false
         :reached-goal? false
         :invincible? true
         :invincibility-remaining default-invincibility-duration))

(defn apply-platform-velocity
  "Applies platform velocity to frog position."
  [frog platform-vx]
  (let [new-x (+ (:x frog) platform-vx)
        clamped-x (max 0 (min new-x (- state/canvas-width (:width frog))))]
    (assoc frog :x clamped-x)))

(defn is-dead?
  "Returns true if frog is dead."
  [frog]
  (:dead? frog))

(defn reached-goal?
  "Returns true if frog reached a goal."
  [frog]
  (:reached-goal? frog))

(defn get-score-multiplier
  "Returns the frog's score multiplier."
  [frog]
  (:score-multiplier frog 1.0))

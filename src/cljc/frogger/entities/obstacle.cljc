(ns frogger.entities.obstacle
  "Obstacle entities (cars, trucks, hazards) that kill the frog on contact."
  (:require [frogger.protocols :as p]
            [frogger.state :as state]
            [frogger.physics.movement :as movement]))

(defrecord Obstacle [x y width height vx vy obstacle-type color row]

  p/Entity
  (get-position [this]
    {:x x :y y})

  (get-bounds [this]
    {:x x :y y :width width :height height})

  (get-type [_]
    :obstacle)

  p/Movable
  (move [this direction]
    this)

  (get-velocity [this]
    {:vx vx :vy vy})

  p/Collidable
  (collides? [this other]
    (let [{ox :x oy :y ow :width oh :height} (p/get-bounds other)]
      (and (< x (+ ox ow))
           (< ox (+ x width))
           (< y (+ oy oh))
           (< oy (+ y height)))))

  (on-collision [this other]
    this)

  p/Renderable
  (get-sprite-key [this]
    obstacle-type)

  (get-color [this]
    color)

  p/Updateable
  (update-entity [this dt]
    (movement/move-and-wrap this dt)))

(def obstacle-types
  {:car {:width 40 :height 36 :color "#E53935"}
   :truck {:width 80 :height 36 :color "#1565C0"}
   :bus {:width 120 :height 36 :color "#FFB300"}
   :motorcycle {:width 30 :height 36 :color "#7B1FA2"}
   :race-car {:width 50 :height 36 :color "#00C853"}})

(defn create-obstacle
  "Creates an obstacle entity."
  [{:keys [x y row obstacle-type direction speed]
    :or {obstacle-type :car direction :left speed 100}}]
  (let [type-data (get obstacle-types obstacle-type {:width 40 :height 36 :color "#E53935"})
        vx (* speed (if (= direction :left) -1 1))]
    (map->Obstacle {:x x
                    :y (or y (* row state/cell-size))
                    :width (:width type-data)
                    :height (:height type-data)
                    :vx vx
                    :vy 0
                    :obstacle-type obstacle-type
                    :color (:color type-data)
                    :row row})))

(defn create-row-obstacles
  "Creates a row of obstacles with specified spacing."
  [{:keys [row obstacle-type direction speed count spacing]}]
  (let [spacing (or spacing 150)]
    (for [i (range count)]
      (create-obstacle {:x (* i spacing)
                        :row row
                        :obstacle-type obstacle-type
                        :direction direction
                        :speed speed}))))

(defn update-obstacles
  "Updates all obstacles in a collection."
  [obstacles dt]
  (mapv #(p/update-entity % dt) obstacles))

(defn get-obstacle-color
  "Returns the color for an obstacle type."
  [obstacle-type]
  (get-in obstacle-types [obstacle-type :color] "#E53935"))

(ns frogger.entities.platform
  "Platform entities (logs, turtles, lily pads) that carry the frog."
  (:require [frogger.protocols :as p]
            [frogger.state :as state]
            [frogger.physics.movement :as movement]))

(defrecord Platform [x y width height vx vy platform-type color row
                     sinking? sink-timer sink-duration]

  p/Entity
  (get-position [this]
    {:x x :y y})

  (get-bounds [this]
    {:x x :y y :width width :height height})

  (get-type [_]
    :platform)

  p/Movable
  (move [this direction]
    this)

  (get-velocity [this]
    {:vx vx :vy vy})

  p/Collidable
  (collides? [this other]
    (when-not sinking?
      (let [{ox :x oy :y ow :width oh :height} (p/get-bounds other)]
        (and (< x (+ ox ow))
             (< ox (+ x width))
             (< y (+ oy oh))
             (< oy (+ y height))))))

  (on-collision [this other]
    this)

  p/Renderable
  (get-sprite-key [this]
    (if sinking?
      (keyword (str (name platform-type) "-sinking"))
      platform-type))

  (get-color [this]
    color)

  p/Updateable
  (update-entity [this dt]
    (let [moved (movement/move-and-wrap this dt)]
      (if (= platform-type :turtle)
        (let [new-timer (+ (or sink-timer 0) dt)]
          (if (>= new-timer (or sink-duration 3000))
            (assoc moved :sink-timer 0 :sinking? (not sinking?))
            (assoc moved :sink-timer new-timer)))
        moved))))

(def platform-types
  {:log-small {:width 80 :height 36 :color "#8B4513"}
   :log-medium {:width 120 :height 36 :color "#8B4513"}
   :log-large {:width 160 :height 36 :color "#8B4513"}
   :turtle {:width 100 :height 36 :color "#2E7D32" :can-sink? true}
   :lily-pad {:width 40 :height 36 :color "#4CAF50"}})

(defn create-platform
  "Creates a platform entity."
  [{:keys [x y row platform-type direction speed]
    :or {platform-type :log-medium direction :right speed 60}}]
  (let [type-data (get platform-types platform-type {:width 80 :height 36 :color "#8B4513"})
        vx (* speed (if (= direction :left) -1 1))]
    (map->Platform {:x x
                    :y (or y (* row state/cell-size))
                    :width (:width type-data)
                    :height (:height type-data)
                    :vx vx
                    :vy 0
                    :platform-type platform-type
                    :color (:color type-data)
                    :row row
                    :sinking? false
                    :sink-timer 0
                    :sink-duration (when (:can-sink? type-data) 3000)})))

(defn create-row-platforms
  "Creates a row of platforms with specified spacing."
  [{:keys [row platform-type direction speed count spacing]}]
  (let [spacing (or spacing 200)]
    (for [i (range count)]
      (create-platform {:x (* i spacing)
                        :row row
                        :platform-type platform-type
                        :direction direction
                        :speed speed}))))

(defn update-platforms
  "Updates all platforms in a collection."
  [platforms dt]
  (mapv #(p/update-entity % dt) platforms))

(defn is-sinking?
  "Returns true if platform is currently sinking."
  [platform]
  (:sinking? platform false))

(defn get-platform-velocity
  "Returns the x velocity of a platform."
  [platform]
  (:vx platform 0))

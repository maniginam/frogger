(ns frogger.entities.checkpoint
  "Checkpoint entity for respawn points with bonus lives."
  (:require [frogger.protocols :as p]
            [frogger.state :as state]))

(def checkpoint-bonus-lives 3)
(def checkpoint-row 7)  ; Middle safe zone (between cars and river)

(defrecord Checkpoint [x y width height index reached? color]
  p/Entity
  (get-position [this]
    {:x x :y y})

  (get-bounds [this]
    {:x x :y y :width width :height height})

  (get-type [_]
    :checkpoint)

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
    (if (= :frog (p/get-type other))
      (assoc this :reached? true)
      this)))

(defn create-checkpoint
  "Creates a checkpoint at the given position."
  [{:keys [x y index]}]
  (map->Checkpoint {:x x
                    :y y
                    :width state/cell-size
                    :height state/cell-size
                    :index index
                    :reached? false
                    :color "#FF5722"}))  ; Orange/red target color

(defn create-checkpoints
  "Creates 3 checkpoints in the middle safe zone."
  []
  (let [y (* checkpoint-row state/cell-size)
        ;; Space them evenly across the width
        ;; Canvas is 560px (14 cells), place at columns 3, 7, 11
        positions [3 7 11]]
    (vec (map-indexed
          (fn [idx col]
            (create-checkpoint {:x (* col state/cell-size)
                               :y y
                               :index idx}))
          positions))))

(defn mark-checkpoint-reached
  "Marks a checkpoint as reached."
  [checkpoint]
  (assoc checkpoint :reached? true))

(defn reset-checkpoints
  "Resets all checkpoints to unreached state."
  [checkpoints]
  (mapv #(assoc % :reached? false) checkpoints))

(defn get-last-reached-checkpoint
  "Returns the last reached checkpoint, or nil if none reached."
  [checkpoints]
  (->> checkpoints
       (filter :reached?)
       (sort-by :index)
       last))

(defn get-respawn-position
  "Returns the position to respawn at based on checkpoints.
   If a checkpoint has been reached, respawn there.
   Otherwise, respawn at the starting position."
  [checkpoints]
  (if-let [checkpoint (get-last-reached-checkpoint checkpoints)]
    {:x (:x checkpoint)
     :y (:y checkpoint)}
    nil))

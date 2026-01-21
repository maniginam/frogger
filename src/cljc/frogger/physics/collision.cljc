(ns frogger.physics.collision
  "Collision detection between game entities."
  (:require [frogger.protocols :as p]))

(defn get-bounds
  "Gets bounds from an entity, either via protocol or direct map access."
  [entity]
  (if (satisfies? p/Entity entity)
    (p/get-bounds entity)
    (select-keys entity [:x :y :width :height])))

(defn aabb-intersects?
  "Axis-aligned bounding box intersection test."
  [bounds-a bounds-b]
  (let [{ax :x ay :y aw :width ah :height} bounds-a
        {bx :x by :y bw :width bh :height} bounds-b]
    (and (< ax (+ bx bw))
         (< bx (+ ax aw))
         (< ay (+ by bh))
         (< by (+ ay ah)))))

(defn collides?
  "Returns true if two entities collide."
  [entity-a entity-b]
  (aabb-intersects? (get-bounds entity-a) (get-bounds entity-b)))

(defn find-collisions
  "Finds all entities in the collection that collide with the target."
  [target entities]
  (filter #(collides? target %) entities))

(defn any-collision?
  "Returns true if target collides with any entity in the collection."
  [target entities]
  (some #(collides? target %) entities))

(defn find-first-collision
  "Returns the first entity that collides with target, or nil."
  [target entities]
  (first (filter #(collides? target %) entities)))

(defn check-obstacle-collisions
  "Checks frog collision with obstacles. Returns :hit if collision, nil otherwise."
  [frog obstacles]
  (when (and (not (:invincible? frog))
             (any-collision? frog obstacles))
    :hit))

(defn check-platform-collisions
  "Checks frog collision with platforms. Returns the platform if on one, nil otherwise."
  [frog platforms]
  (find-first-collision frog platforms))

(defn check-goal-collisions
  "Checks frog collision with goals. Returns the goal if reached, nil otherwise."
  [frog goals]
  (find-first-collision frog (filter #(not (:reached? %)) goals)))

(defn check-checkpoint-collisions
  "Checks frog collision with checkpoints. Returns the checkpoint if reached (and not already reached), nil otherwise."
  [frog checkpoints]
  (find-first-collision frog (filter #(not (:reached? %)) checkpoints)))

(defn point-in-bounds?
  "Returns true if point is within bounds."
  [{:keys [px py]} {:keys [x y width height]}]
  (and (>= px x)
       (< px (+ x width))
       (>= py y)
       (< py (+ y height))))

(defn center-point
  "Returns the center point of an entity's bounds."
  [entity]
  (let [{:keys [x y width height]} (get-bounds entity)]
    {:px (+ x (/ width 2))
     :py (+ y (/ height 2))}))

(defn distance-between
  "Returns the distance between two entities' centers."
  [entity-a entity-b]
  (let [{ax :px ay :py} (center-point entity-a)
        {bx :px by :py} (center-point entity-b)
        dx (- bx ax)
        dy (- by ay)]
    #?(:clj (Math/sqrt (+ (* dx dx) (* dy dy)))
       :cljs (js/Math.sqrt (+ (* dx dx) (* dy dy))))))

(defn collision-response
  "Determines the collision response based on entity types."
  [frog other]
  (let [other-type (if (satisfies? p/Entity other)
                     (p/get-type other)
                     (:type other))]
    (case other-type
      :obstacle {:type :death :cause :obstacle}
      :platform {:type :ride :platform other}
      :goal {:type :score :goal other}
      :water {:type :death :cause :water}
      nil)))

(defn process-collisions
  "Processes all collisions for a frog and returns collision events."
  [frog {:keys [obstacles platforms goals]}]
  (let [obstacle-hit (check-obstacle-collisions frog obstacles)
        platform (check-platform-collisions frog platforms)
        goal (check-goal-collisions frog goals)]
    (cond-> []
      obstacle-hit (conj {:type :death :cause :obstacle})
      platform (conj {:type :ride :platform platform})
      goal (conj {:type :goal-reached :goal goal}))))

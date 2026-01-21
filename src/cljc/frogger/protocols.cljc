(ns frogger.protocols
  "Core protocols defining extensibility contracts for game entities.
   Following Interface Segregation Principle - small, focused protocols.")

(defprotocol Entity
  "Base protocol for all game entities with position and dimensions."
  (get-position [this] "Returns {:x x :y y} map")
  (get-bounds [this] "Returns {:x x :y y :width w :height h} for collision")
  (get-type [this] "Returns keyword identifying entity type"))

(defprotocol Movable
  "Protocol for entities that can move."
  (move [this direction] "Returns updated entity moved in direction")
  (get-velocity [this] "Returns {:vx vx :vy vy} velocity"))

(defprotocol Collidable
  "Protocol for entities that participate in collision detection."
  (collides? [this other] "Returns true if this entity collides with other")
  (on-collision [this other] "Returns updated entity after collision with other"))

(defprotocol Ability
  "Protocol for special abilities (temperaments)."
  (can-activate? [this] "Returns true if ability can be activated")
  (activate [this] "Returns updated entity with ability activated")
  (get-ability-state [this] "Returns ability state map"))

(defprotocol Renderable
  "Protocol for entities that can be rendered."
  (get-sprite-key [this] "Returns keyword for sprite lookup")
  (get-color [this] "Returns color string for rendering"))

(defprotocol Updateable
  "Protocol for entities that update over time."
  (update-entity [this dt] "Returns updated entity after dt milliseconds"))

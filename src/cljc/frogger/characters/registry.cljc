(ns frogger.characters.registry
  "Registry of frog characters with their attributes and abilities.")

(def characters
  {:forest {:id :forest
            :name "Forest Frog"
            :color "#228B22"
            :temperament :balanced
            :description "A well-rounded frog with no special abilities."
            :speed-multiplier 1.0
            :score-multiplier 1.0
            :ability-type nil}

   :river {:id :river
           :name "River Frog"
           :color "#4169E1"
           :temperament :speedy
           :description "A swift frog with faster hops and shorter cooldown."
           :speed-multiplier 1.5
           :score-multiplier 1.0
           :ability-type nil
           :hop-cooldown-multiplier 0.7}

   :desert {:id :desert
            :name "Desert Frog"
            :color "#FF6347"
            :temperament :tank
            :description "A tough frog with extended invincibility duration."
            :speed-multiplier 1.0
            :score-multiplier 1.0
            :ability-type :tank-invincibility
            :invincibility-multiplier 2.0}

   :magic {:id :magic
           :name "Magic Frog"
           :color "#9370DB"
           :temperament :mystical
           :description "A mystical frog that can double jump."
           :speed-multiplier 1.0
           :score-multiplier 1.0
           :ability-type :double-jump
           :ability-state {:charges 2 :max-charges 2}}

   :sun {:id :sun
         :name "Sun Frog"
         :color "#FFD700"
         :temperament :lucky
         :description "A lucky frog that earns 1.5x score."
         :speed-multiplier 1.0
         :score-multiplier 1.5
         :ability-type nil}

   :shadow {:id :shadow
            :name "Shadow Frog"
            :color "#2F2F2F"
            :temperament :stealthy
            :description "A stealthy frog that can briefly phase through obstacles."
            :speed-multiplier 1.0
            :score-multiplier 1.0
            :ability-type :phase-through
            :ability-state {:cooldown 5000 :duration 500}}})

(defn get-character
  "Returns character data by id."
  [character-id]
  (get characters character-id))

(defn get-all-characters
  "Returns all characters as a sequence."
  []
  (vals characters))

(defn get-character-ids
  "Returns all character ids."
  []
  (keys characters))

(defn character-exists?
  "Returns true if character exists."
  [character-id]
  (contains? characters character-id))

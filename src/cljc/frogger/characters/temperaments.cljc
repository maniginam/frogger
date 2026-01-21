(ns frogger.characters.temperaments
  "Temperament definitions and ability effect implementations.")

(def temperaments
  {:balanced {:id :balanced
              :name "Balanced"
              :description "No special modifiers"
              :effects {}}

   :speedy {:id :speedy
            :name "Speedy"
            :description "Faster movement with shorter hop cooldown"
            :effects {:speed-multiplier 1.5
                      :hop-cooldown-multiplier 0.7}}

   :tank {:id :tank
          :name "Tank"
          :description "Extended invincibility duration after hits"
          :effects {:invincibility-multiplier 2.0}}

   :mystical {:id :mystical
              :name "Mystical"
              :description "Can perform double jumps"
              :effects {:ability :double-jump
                        :charges 2}}

   :lucky {:id :lucky
           :name "Lucky"
           :description "Earns more points from all actions"
           :effects {:score-multiplier 1.5}}

   :stealthy {:id :stealthy
              :name "Stealthy"
              :description "Can phase through obstacles briefly"
              :effects {:ability :phase-through
                        :cooldown 5000
                        :duration 500}}})

(defn get-temperament
  "Returns temperament data by id."
  [temperament-id]
  (get temperaments temperament-id))

(defn apply-temperament-effects
  "Applies temperament effects to frog attributes."
  [frog-attrs temperament-id]
  (let [temp (get-temperament temperament-id)
        effects (:effects temp {})]
    (cond-> frog-attrs
      (:speed-multiplier effects)
      (assoc :speed-multiplier (:speed-multiplier effects))

      (:score-multiplier effects)
      (assoc :score-multiplier (:score-multiplier effects))

      (:hop-cooldown-multiplier effects)
      (assoc :hop-cooldown-multiplier (:hop-cooldown-multiplier effects))

      (:invincibility-multiplier effects)
      (assoc :invincibility-multiplier (:invincibility-multiplier effects))

      (:ability effects)
      (assoc :ability-type (:ability effects))

      (:charges effects)
      (assoc-in [:ability-state :charges] (:charges effects))

      (:cooldown effects)
      (assoc-in [:ability-state :cooldown] (:cooldown effects))

      (:duration effects)
      (assoc-in [:ability-state :duration] (:duration effects)))))

(defmulti activate-ability
  "Multimethod to activate character-specific abilities."
  (fn [ability-type frog] ability-type))

(defmethod activate-ability :double-jump
  [_ frog]
  (let [charges (get-in frog [:ability-state :charges] 0)]
    (if (> charges 0)
      (-> frog
          (update-in [:ability-state :charges] dec)
          (assoc :hop-cooldown-remaining 0))
      frog)))

(defmethod activate-ability :phase-through
  [_ frog]
  (let [cooldown-remaining (get-in frog [:ability-state :cooldown-remaining] 0)
        duration (get-in frog [:ability-state :duration] 500)]
    (if (<= cooldown-remaining 0)
      (-> frog
          (assoc :invincible? true)
          (assoc :invincibility-remaining duration)
          (assoc-in [:ability-state :cooldown-remaining]
                    (get-in frog [:ability-state :cooldown] 5000)))
      frog)))

(defmethod activate-ability :tank-invincibility
  [_ frog]
  (let [base-duration 1000
        multiplier (get frog :invincibility-multiplier 2.0)]
    (assoc frog
           :invincible? true
           :invincibility-remaining (* base-duration multiplier))))

(defmethod activate-ability :default
  [_ frog]
  frog)

(defn reset-ability-charges
  "Resets ability charges for double-jump on level completion."
  [frog]
  (if (= :double-jump (get-in frog [:ability-state :type]))
    (assoc-in frog [:ability-state :charges]
              (get-in frog [:ability-state :max-charges] 2))
    frog))

(defn update-ability-cooldown
  "Updates ability cooldown timer."
  [frog dt]
  (update-in frog [:ability-state :cooldown-remaining]
             (fn [cd] (max 0 (- (or cd 0) dt)))))

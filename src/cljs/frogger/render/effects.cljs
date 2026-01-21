(ns frogger.render.effects
  "Visual effects for the game.")

(defonce effects-atom (atom []))

(defn add-effect!
  "Adds a visual effect to be rendered."
  [effect]
  (swap! effects-atom conj (assoc effect :start-time (js/Date.now))))

(defn create-death-effect
  "Creates a death splash/explosion effect."
  [x y]
  {:type :death
   :x x
   :y y
   :duration 500
   :radius 20})

(defn create-goal-effect
  "Creates a goal reached celebration effect."
  [x y]
  {:type :goal
   :x x
   :y y
   :duration 800
   :particles 10})

(defn create-hop-effect
  "Creates a small hop dust effect."
  [x y]
  {:type :hop
   :x x
   :y y
   :duration 200
   :radius 5})

(defn update-effects!
  "Updates all active effects, removing expired ones."
  []
  (let [now (js/Date.now)]
    (swap! effects-atom
           (fn [effects]
             (filterv (fn [e]
                        (< (- now (:start-time e)) (:duration e)))
                      effects)))))

(defn render-death-effect
  "Renders a death effect."
  [ctx effect progress]
  (let [{:keys [x y radius]} effect
        current-radius (* radius (+ 1 progress))
        alpha (- 1 progress)]
    (set! (.-globalAlpha ctx) alpha)
    (set! (.-fillStyle ctx) "#FF0000")
    (.beginPath ctx)
    (.arc ctx x y current-radius 0 (* 2 js/Math.PI))
    (.fill ctx)
    (set! (.-globalAlpha ctx) 1)))

(defn render-goal-effect
  "Renders a goal celebration effect."
  [ctx effect progress]
  (let [{:keys [x y particles]} effect
        alpha (- 1 progress)]
    (set! (.-globalAlpha ctx) alpha)
    (doseq [i (range particles)]
      (let [angle (* (/ i particles) 2 js/Math.PI)
            dist (* 30 progress)
            px (+ x (* dist (js/Math.cos angle)))
            py (+ y (* dist (js/Math.sin angle)))]
        (set! (.-fillStyle ctx) "#FFD700")
        (.beginPath ctx)
        (.arc ctx px py 4 0 (* 2 js/Math.PI))
        (.fill ctx)))
    (set! (.-globalAlpha ctx) 1)))

(defn render-hop-effect
  "Renders a hop dust effect."
  [ctx effect progress]
  (let [{:keys [x y radius]} effect
        current-radius (* radius (+ 1 (* 2 progress)))
        alpha (* 0.5 (- 1 progress))]
    (set! (.-globalAlpha ctx) alpha)
    (set! (.-fillStyle ctx) "#8B7355")
    (.beginPath ctx)
    (.arc ctx x y current-radius 0 (* 2 js/Math.PI))
    (.fill ctx)
    (set! (.-globalAlpha ctx) 1)))

(defn render-effects
  "Renders all active effects."
  [ctx]
  (let [now (js/Date.now)]
    (doseq [effect @effects-atom]
      (let [elapsed (- now (:start-time effect))
            progress (/ elapsed (:duration effect))]
        (case (:type effect)
          :death (render-death-effect ctx effect progress)
          :goal (render-goal-effect ctx effect progress)
          :hop (render-hop-effect ctx effect progress)
          nil)))))

(defn trigger-death-effect!
  "Triggers a death effect at the frog's position."
  [frog]
  (when frog
    (add-effect! (create-death-effect
                  (+ (:x frog) (/ (:width frog) 2))
                  (+ (:y frog) (/ (:height frog) 2))))))

(defn trigger-goal-effect!
  "Triggers a goal celebration effect."
  [goal]
  (when goal
    (add-effect! (create-goal-effect
                  (+ (:x goal) (/ (:width goal) 2))
                  (+ (:y goal) (/ (:height goal) 2))))))

(defn clear-effects!
  "Clears all effects."
  []
  (reset! effects-atom []))

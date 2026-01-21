(ns frogger.state
  "Game state structure and initialization.")

(def canvas-width 560)
(def canvas-height 640)
(def cell-size 40)
(def grid-cols (/ canvas-width cell-size))
(def grid-rows (/ canvas-height cell-size))

(def initial-lives 3)
(def initial-score 0)

(defn make-initial-state
  "Creates the initial game state structure."
  [{:keys [character-id theme-id] :or {character-id :forest theme-id :forest}}]
  {:screen :menu
   :character-id character-id
   :theme-id theme-id
   :frog nil
   :obstacles []
   :platforms []
   :goals []
   :score initial-score
   :lives initial-lives
   :level 1
   :time-remaining 60000
   :paused? false
   :input {:direction nil :ability-pressed? false}})

(defn game-over?
  "Returns true if the game is over (no lives remaining)."
  [state]
  (<= (:lives state) 0))

(defn level-complete?
  "Returns true if all goals have been reached."
  [state]
  (every? :reached? (:goals state)))

(defn get-frog
  "Returns the frog entity from state."
  [state]
  (:frog state))

(defn set-frog
  "Returns state with updated frog."
  [state frog]
  (assoc state :frog frog))

(defn get-obstacles
  "Returns obstacles from state."
  [state]
  (:obstacles state))

(defn set-obstacles
  "Returns state with updated obstacles."
  [state obstacles]
  (assoc state :obstacles obstacles))

(defn get-platforms
  "Returns platforms from state."
  [state]
  (:platforms state))

(defn set-platforms
  "Returns state with updated platforms."
  [state platforms]
  (assoc state :platforms platforms))

(defn get-goals
  "Returns goals from state."
  [state]
  (:goals state))

(defn set-goals
  "Returns state with updated goals."
  [state goals]
  (assoc state :goals goals))

(defn add-score
  "Returns state with points added to score."
  [state points]
  (update state :score + points))

(defn lose-life
  "Returns state with one less life."
  [state]
  (update state :lives dec))

(defn set-screen
  "Returns state with updated screen."
  [state screen]
  (assoc state :screen screen))

(defn next-level
  "Returns state advanced to next level."
  [state]
  (-> state
      (update :level inc)
      (assoc :time-remaining 60000)))

(defn reset-frog-position
  "Returns state with frog reset to starting position."
  [state start-x start-y]
  (if-let [frog (:frog state)]
    (assoc state :frog (assoc frog :x start-x :y start-y))
    state))

(defn set-input
  "Returns state with updated input."
  [state input]
  (assoc state :input input))

(defn clear-input
  "Returns state with input cleared."
  [state]
  (assoc state :input {:direction nil :ability-pressed? false}))

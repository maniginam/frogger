(ns frogger.ui.hud
  "Heads-up display for score, lives, and level.")

(defn update-score! [score]
  (when-let [el (.getElementById js/document "score")]
    (set! (.-textContent el) (str "Score: " score))))

(defn update-lives! [lives]
  (when-let [el (.getElementById js/document "lives")]
    (set! (.-textContent el) (str "Lives: " lives))))

(defn update-level! [level]
  (when-let [el (.getElementById js/document "level")]
    (set! (.-textContent el) (str "Level: " level))))

(defn update-time! [time-ms]
  (when-let [el (.getElementById js/document "time")]
    (let [seconds (js/Math.ceil (/ time-ms 1000))]
      (set! (.-textContent el) (str "Time: " seconds)))))

(defn update-hud! [game-state]
  (update-score! (:score game-state 0))
  (update-lives! (:lives game-state 3))
  (update-level! (:level game-state 1)))

(defn show-hud! []
  (when-let [hud (.getElementById js/document "hud")]
    (.remove (.-classList hud) "hidden")))

(defn hide-hud! []
  (when-let [hud (.getElementById js/document "hud")]
    (.add (.-classList hud) "hidden")))

(defn flash-lives! []
  (when-let [el (.getElementById js/document "lives")]
    (.add (.-classList el) "flash")
    (js/setTimeout #(.remove (.-classList el) "flash") 500)))

(defn flash-score! []
  (when-let [el (.getElementById js/document "score")]
    (.add (.-classList el) "flash")
    (js/setTimeout #(.remove (.-classList el) "flash") 300)))

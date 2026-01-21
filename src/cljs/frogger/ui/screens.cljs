(ns frogger.ui.screens
  "Game screens: game over, pause, etc.")

(declare hide-level-complete!)

(defonce restart-callback (atom nil))

(defn show-game-over! [final-score]
  (when-let [screen (.getElementById js/document "game-over-screen")]
    (.remove (.-classList screen) "hidden"))
  (when-let [score-el (.getElementById js/document "final-score")]
    (set! (.-textContent score-el) (str final-score))))

(defn hide-game-over! []
  (when-let [screen (.getElementById js/document "game-over-screen")]
    (.add (.-classList screen) "hidden")))

(defn setup-restart-button! [callback]
  (reset! restart-callback callback)
  (when-let [btn (.getElementById js/document "restart-btn")]
    (.addEventListener btn "click"
                       (fn [_]
                         (hide-game-over!)
                         (when @restart-callback
                           (@restart-callback))))))

(defn show-pause-overlay! []
  (let [overlay (.createElement js/document "div")]
    (set! (.-id overlay) "pause-overlay")
    (set! (.-innerHTML overlay)
          "<div class='pause-content'>
             <h2>PAUSED</h2>
             <p>Press P or ESC to continue</p>
           </div>")
    (set! (.-style.cssText (.-style overlay))
          "position: absolute;
           top: 0;
           left: 0;
           width: 100%;
           height: 100%;
           background: rgba(0,0,0,0.8);
           display: flex;
           justify-content: center;
           align-items: center;
           color: white;
           font-family: inherit;
           z-index: 100;")
    (when-let [app (.getElementById js/document "app")]
      (.appendChild app overlay))))

(defn hide-pause-overlay! []
  (when-let [overlay (.getElementById js/document "pause-overlay")]
    (.remove overlay)))

(defn show-level-complete! [level score]
  (let [overlay (.createElement js/document "div")]
    (set! (.-id overlay) "level-complete-overlay")
    (set! (.-innerHTML overlay)
          (str "<div class='level-content'>
                  <h2>Level " level " Complete!</h2>
                  <p>Score: " score "</p>
                </div>"))
    (set! (.-style.cssText (.-style overlay))
          "position: absolute;
           top: 0;
           left: 0;
           width: 100%;
           height: 100%;
           background: rgba(0,100,0,0.8);
           display: flex;
           justify-content: center;
           align-items: center;
           color: white;
           font-family: inherit;
           z-index: 100;")
    (when-let [app (.getElementById js/document "app")]
      (.appendChild app overlay))
    (js/setTimeout hide-level-complete! 2000)))

(defn hide-level-complete! []
  (when-let [overlay (.getElementById js/document "level-complete-overlay")]
    (.remove overlay)))

(defn init-screens! [restart-callback-fn]
  (setup-restart-button! restart-callback-fn))

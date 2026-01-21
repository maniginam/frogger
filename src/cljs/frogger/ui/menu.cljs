(ns frogger.ui.menu
  "Character and theme selection menu."
  (:require [frogger.characters.registry :as characters]
            [frogger.themes.registry :as themes]
            [frogger.render.sprites :as sprites]))

(declare update-character-selection! update-theme-selection!)

(defonce selected-character (atom :forest))
(defonce selected-theme (atom :forest))
(defonce start-callback (atom nil))

(defn draw-frog-preview
  "Draws a frog sprite on a canvas element."
  [canvas color]
  (let [ctx (.getContext canvas "2d")
        width (.-width canvas)
        height (.-height canvas)]
    (.clearRect ctx 0 0 width height)
    (sprites/draw-frog-sprite ctx 5 5 (- width 10) (- height 10) color)))

(defn draw-theme-preview
  "Draws a mini environment preview for a theme."
  [canvas theme-id]
  (let [ctx (.getContext canvas "2d")
        width (.-width canvas)
        height (.-height canvas)
        theme (themes/get-theme theme-id)
        colors (:colors theme)]
    (.clearRect ctx 0 0 width height)

    ;; Draw mini scene based on theme
    ;; Sky/background
    (set! (.-fillStyle ctx) (:background colors "#2D5016"))
    (.fillRect ctx 0 0 width height)

    ;; Goal area (top)
    (set! (.-fillStyle ctx) (:goal-area colors "#1B5E20"))
    (.fillRect ctx 0 0 width (* height 0.12))

    ;; Water area
    (set! (.-fillStyle ctx) (:water colors "#1565C0"))
    (.fillRect ctx 0 (* height 0.12) width (* height 0.35))

    ;; Draw a log on the water
    (let [log-color (get-in theme [:platforms :log] "#6D4C41")]
      (set! (.-fillStyle ctx) log-color)
      (.beginPath ctx)
      (.roundRect ctx 8 (* height 0.22) 35 10 3)
      (.fill ctx))

    ;; Draw a turtle
    (let [turtle-color (get-in theme [:platforms :turtle] "#388E3C")]
      (set! (.-fillStyle ctx) turtle-color)
      (.beginPath ctx)
      (.ellipse ctx 60 (* height 0.35) 10 6 0 0 (* 2 js/Math.PI))
      (.fill ctx))

    ;; Safe zone (middle)
    (set! (.-fillStyle ctx) (:safe-zone colors "#4A7023"))
    (.fillRect ctx 0 (* height 0.47) width (* height 0.12))

    ;; Road area
    (set! (.-fillStyle ctx) (:road colors "#404040"))
    (.fillRect ctx 0 (* height 0.59) width (* height 0.30))

    ;; Road lines
    (set! (.-fillStyle ctx) (:road-lines colors "#FFFFFF"))
    (doseq [i (range 0 width 15)]
      (.fillRect ctx i (+ (* height 0.59) 18) 8 2))

    ;; Draw a car
    (let [car-color (get-in theme [:obstacles :car] "#E53935")]
      (set! (.-fillStyle ctx) car-color)
      (.beginPath ctx)
      (.roundRect ctx 10 (* height 0.65) 25 12 3)
      (.fill ctx)
      ;; Car windows
      (set! (.-fillStyle ctx) "#87CEEB")
      (.fillRect ctx 18 (+ (* height 0.65) 2) 8 5))

    ;; Draw a truck
    (let [truck-color (get-in theme [:obstacles :truck] "#1565C0")]
      (set! (.-fillStyle ctx) truck-color)
      (.beginPath ctx)
      (.roundRect ctx 50 (* height 0.75) 30 12 2)
      (.fill ctx))

    ;; Safe zone (bottom)
    (set! (.-fillStyle ctx) (:safe-zone colors "#4A7023"))
    (.fillRect ctx 0 (* height 0.89) width (* height 0.11))

    ;; Draw a tiny frog at bottom
    (set! (.-fillStyle ctx) "#228B22")
    (.beginPath ctx)
    (.ellipse ctx (/ width 2) (* height 0.94) 6 5 0 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Frog eyes
    (set! (.-fillStyle ctx) "#FFFFFF")
    (.beginPath ctx)
    (.arc ctx (- (/ width 2) 2) (- (* height 0.94) 2) 2 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ (/ width 2) 2) (- (* height 0.94) 2) 2 0 (* 2 js/Math.PI))
    (.fill ctx)))

(defn create-character-option [character]
  (let [{:keys [id name color description]} character
        div (.createElement js/document "div")
        canvas (.createElement js/document "canvas")]
    (set! (.-className div) "selection-item")
    (set! (.-dataset.id div) (clj->js id))
    (set! (.-width canvas) 70)
    (set! (.-height canvas) 70)
    (set! (.-className canvas) "preview-canvas")
    (.appendChild div canvas)
    (let [name-div (.createElement js/document "div")]
      (set! (.-className name-div) "name")
      (set! (.-textContent name-div) name)
      (.appendChild div name-div))
    ;; Draw the frog after canvas is created
    (js/setTimeout #(draw-frog-preview canvas color) 10)
    (.addEventListener div "click"
                       (fn [_]
                         (reset! selected-character id)
                         (update-character-selection!)))
    div))

(defn create-theme-option [theme]
  (let [{:keys [id name colors]} theme
        div (.createElement js/document "div")
        canvas (.createElement js/document "canvas")]
    (set! (.-className div) "selection-item theme-item")
    (set! (.-dataset.id div) (clj->js id))
    (set! (.-width canvas) 90)
    (set! (.-height canvas) 80)
    (set! (.-className canvas) "preview-canvas")
    (.appendChild div canvas)
    (let [name-div (.createElement js/document "div")]
      (set! (.-className name-div) "name")
      (set! (.-textContent name-div) name)
      (.appendChild div name-div))
    ;; Draw the theme preview after canvas is created
    (js/setTimeout #(draw-theme-preview canvas id) 10)
    (.addEventListener div "click"
                       (fn [_]
                         (reset! selected-theme id)
                         (update-theme-selection!)))
    div))

(defn update-character-selection! []
  (let [items (.querySelectorAll js/document "#character-select .selection-item")]
    (.forEach items
              (fn [item]
                (let [item-id (keyword (.-id (.-dataset item)))]
                  (if (= item-id @selected-character)
                    (.add (.-classList item) "selected")
                    (.remove (.-classList item) "selected")))))))

(defn update-theme-selection! []
  (let [items (.querySelectorAll js/document "#theme-select .selection-item")]
    (.forEach items
              (fn [item]
                (let [item-id (keyword (.-id (.-dataset item)))]
                  (if (= item-id @selected-theme)
                    (.add (.-classList item) "selected")
                    (.remove (.-classList item) "selected")))))))

(defn render-character-select! []
  (when-let [container (.getElementById js/document "character-select")]
    (set! (.-innerHTML container) "<h3>Select Your Frog</h3>")
    (let [grid (.createElement js/document "div")]
      (set! (.-className grid) "selection-grid")
      (doseq [character (characters/get-all-characters)]
        (.appendChild grid (create-character-option character)))
      (.appendChild container grid))
    (update-character-selection!)))

(defn render-theme-select! []
  (when-let [container (.getElementById js/document "theme-select")]
    (set! (.-innerHTML container) "<h3>Select Theme</h3>")
    (let [grid (.createElement js/document "div")]
      (set! (.-className grid) "selection-grid theme-grid")
      (doseq [theme (themes/get-all-themes)]
        (.appendChild grid (create-theme-option theme)))
      (.appendChild container grid))
    (update-theme-selection!)))

(defn setup-start-button! [callback]
  (reset! start-callback callback)
  (when-let [btn (.getElementById js/document "start-btn")]
    (.addEventListener btn "click"
                       (fn [_]
                         (when @start-callback
                           (@start-callback @selected-character @selected-theme))))))

(defn show-menu! []
  (when-let [menu (.getElementById js/document "menu-container")]
    (.remove (.-classList menu) "hidden"))
  (when-let [canvas (.getElementById js/document "game-canvas")]
    (.add (.-classList canvas) "hidden"))
  (when-let [hud (.getElementById js/document "hud")]
    (.add (.-classList hud) "hidden")))

(defn hide-menu! []
  (when-let [menu (.getElementById js/document "menu-container")]
    (.add (.-classList menu) "hidden"))
  (when-let [canvas (.getElementById js/document "game-canvas")]
    (.remove (.-classList canvas) "hidden"))
  (when-let [hud (.getElementById js/document "hud")]
    (.remove (.-classList hud) "hidden")))

(defn init-menu! [start-callback-fn]
  (render-character-select!)
  (render-theme-select!)
  (setup-start-button! start-callback-fn))

(defn get-selections []
  {:character-id @selected-character
   :theme-id @selected-theme})

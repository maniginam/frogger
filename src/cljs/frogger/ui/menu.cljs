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

(defn- draw-stars
  "Draws twinkling stars on canvas."
  [ctx width height count]
  (set! (.-fillStyle ctx) "#FFFFFF")
  (doseq [_ (range count)]
    (let [x (rand-int width)
          y (rand-int (int (* height 0.5)))
          size (+ 1 (rand-int 2))]
      (.beginPath ctx)
      (.arc ctx x y size 0 (* 2 js/Math.PI))
      (.fill ctx))))

(defn- draw-trees
  "Draws simple triangular trees."
  [ctx positions color trunk-color]
  (doseq [[x y size] positions]
    ;; Trunk
    (set! (.-fillStyle ctx) trunk-color)
    (.fillRect ctx (- x 2) y 4 (/ size 2))
    ;; Foliage
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.moveTo ctx x (- y size))
    (.lineTo ctx (+ x (/ size 2)) y)
    (.lineTo ctx (- x (/ size 2)) y)
    (.closePath ctx)
    (.fill ctx)))

(defn- draw-buildings
  "Draws city buildings in background."
  [ctx width max-height colors]
  (doseq [i (range 0 width 12)]
    (let [h (+ 8 (rand-int (int max-height)))
          y (- max-height h)]
      (set! (.-fillStyle ctx) (nth colors (mod i (count colors))))
      (.fillRect ctx i y 10 h)
      ;; Windows
      (set! (.-fillStyle ctx) "#FFEB3B")
      (doseq [wy (range (+ y 2) max-height 4)]
        (when (< (rand) 0.6)
          (.fillRect ctx (+ i 2) wy 2 2)
          (.fillRect ctx (+ i 6) wy 2 2))))))

(defn- draw-bubbles
  "Draws underwater bubbles."
  [ctx width height count]
  (set! (.-fillStyle ctx) "rgba(255,255,255,0.3)")
  (doseq [_ (range count)]
    (let [x (rand-int width)
          y (rand-int height)
          size (+ 2 (rand-int 4))]
      (.beginPath ctx)
      (.arc ctx x y size 0 (* 2 js/Math.PI))
      (.fill ctx))))

(defn- draw-candy-decorations
  "Draws candy themed decorations."
  [ctx width height]
  ;; Lollipops
  (doseq [[x y] [[10 20] [75 25]]]
    (set! (.-fillStyle ctx) "#8B4513")
    (.fillRect ctx x y 2 12)
    (set! (.-fillStyle ctx) "#FF69B4")
    (.beginPath ctx)
    (.arc ctx (+ x 1) (- y 1) 5 0 (* 2 js/Math.PI))
    (.fill ctx)
    (set! (.-strokeStyle ctx) "#FFFFFF")
    (set! (.-lineWidth ctx) 1)
    (.beginPath ctx)
    (.arc ctx (+ x 1) (- y 1) 3 0 js/Math.PI)
    (.stroke ctx))
  ;; Candy canes at edges
  (set! (.-strokeStyle ctx) "#FF0000")
  (set! (.-lineWidth ctx) 3)
  (.beginPath ctx)
  (.moveTo ctx 3 (- height 5))
  (.quadraticCurveTo ctx 3 (- height 15) 8 (- height 15))
  (.stroke ctx))

(defn- draw-seaweed
  "Draws underwater seaweed."
  [ctx positions color]
  (set! (.-fillStyle ctx) color)
  (doseq [[x y height] positions]
    (.beginPath ctx)
    (.moveTo ctx x y)
    (.quadraticCurveTo ctx (+ x 3) (- y (/ height 2)) (- x 2) (- y height))
    (.quadraticCurveTo ctx (+ x 4) (- y (/ height 2)) x y)
    (.fill ctx)))

(defn draw-theme-preview
  "Draws a mini environment preview for a theme with unique decorations."
  [canvas theme-id]
  (let [ctx (.getContext canvas "2d")
        width (.-width canvas)
        height (.-height canvas)
        theme (themes/get-theme theme-id)
        colors (:colors theme)]
    (.clearRect ctx 0 0 width height)

    ;; Background
    (set! (.-fillStyle ctx) (:background colors "#2D5016"))
    (.fillRect ctx 0 0 width height)

    ;; Theme-specific background decorations
    (case theme-id
      :space (do
               (draw-stars ctx width height 30)
               ;; Planet
               (set! (.-fillStyle ctx) "#FF6B6B")
               (.beginPath ctx)
               (.arc ctx 75 15 8 0 (* 2 js/Math.PI))
               (.fill ctx)
               ;; Saturn ring
               (set! (.-strokeStyle ctx) "#FFE4B5")
               (set! (.-lineWidth ctx) 2)
               (.beginPath ctx)
               (.ellipse ctx 75 15 12 3 0.3 0 (* 2 js/Math.PI))
               (.stroke ctx))

      :magical (do
                 (draw-stars ctx width height 15)
                 ;; Glowing mushrooms
                 (doseq [[x y] [[15 38] [70 35]]]
                   (set! (.-fillStyle ctx) "#E040FB")
                   (.beginPath ctx)
                   (.arc ctx x y 4 js/Math.PI 0)
                   (.fill ctx)
                   (set! (.-fillStyle ctx) "#9C27B0")
                   (.fillRect ctx (- x 1) y 2 4)))

      :candy (draw-candy-decorations ctx width height)

      :underwater (do
                    (draw-bubbles ctx width height 20)
                    (draw-seaweed ctx [[5 height 15] [85 height 12] [45 height 10]] "#26A69A"))

      :city (draw-buildings ctx width 25 ["#37474F" "#455A64" "#546E7A"])

      :forest (draw-trees ctx [[15 45 12] [75 42 14] [50 48 10]] "#228B22" "#6D4C41")

      :pond (draw-trees ctx [[10 40 10] [80 38 12]] "#2E7D32" "#5D4037")

      :highway nil)

    ;; Goal area (top)
    (set! (.-fillStyle ctx) (:goal-area colors "#1B5E20"))
    (.fillRect ctx 0 0 width (* height 0.12))

    ;; Water area with wave effect
    (set! (.-fillStyle ctx) (:water colors "#1565C0"))
    (.fillRect ctx 0 (* height 0.12) width (* height 0.35))
    ;; Wave highlights
    (set! (.-strokeStyle ctx) "rgba(255,255,255,0.2)")
    (set! (.-lineWidth ctx) 1)
    (doseq [i (range 3)]
      (let [y (+ (* height 0.15) (* i 8))]
        (.beginPath ctx)
        (.moveTo ctx 0 y)
        (doseq [x (range 0 width 10)]
          (.lineTo ctx x (+ y (* 2 (js/Math.sin (/ x 5))))))
        (.stroke ctx)))

    ;; Draw a detailed log on the water
    (let [log-color (get-in theme [:platforms :log] "#6D4C41")]
      (set! (.-fillStyle ctx) log-color)
      (.beginPath ctx)
      (.roundRect ctx 8 (* height 0.22) 35 10 4)
      (.fill ctx)
      ;; Log end
      (set! (.-fillStyle ctx) "#8D6E63")
      (.beginPath ctx)
      (.ellipse ctx 10 (+ (* height 0.22) 5) 3 5 0 0 (* 2 js/Math.PI))
      (.fill ctx)
      ;; Wood grain
      (set! (.-strokeStyle ctx) "#5D4037")
      (set! (.-lineWidth ctx) 1)
      (.beginPath ctx)
      (.moveTo ctx 15 (+ (* height 0.22) 2))
      (.lineTo ctx 38 (+ (* height 0.22) 2))
      (.stroke ctx))

    ;; Draw a turtle with shell pattern
    (let [turtle-color (get-in theme [:platforms :turtle] "#388E3C")
          tx 58
          ty (* height 0.35)]
      ;; Shell
      (set! (.-fillStyle ctx) turtle-color)
      (.beginPath ctx)
      (.ellipse ctx tx ty 10 7 0 0 (* 2 js/Math.PI))
      (.fill ctx)
      ;; Shell pattern
      (set! (.-strokeStyle ctx) "#2E7D32")
      (set! (.-lineWidth ctx) 1)
      (.beginPath ctx)
      (.ellipse ctx tx ty 5 3 0 0 (* 2 js/Math.PI))
      (.stroke ctx)
      ;; Head
      (set! (.-fillStyle ctx) "#4CAF50")
      (.beginPath ctx)
      (.arc ctx (- tx 10) ty 3 0 (* 2 js/Math.PI))
      (.fill ctx)
      ;; Flippers
      (doseq [dx [-7 7]]
        (.beginPath ctx)
        (.ellipse ctx (+ tx dx) (+ ty 5) 3 2 0 0 (* 2 js/Math.PI))
        (.fill ctx)))

    ;; Safe zone (middle) with grass texture
    (set! (.-fillStyle ctx) (:safe-zone colors "#4A7023"))
    (.fillRect ctx 0 (* height 0.47) width (* height 0.12))
    ;; Grass blades
    (set! (.-strokeStyle ctx) "#5D8B32")
    (set! (.-lineWidth ctx) 1)
    (doseq [x (range 0 width 6)]
      (.beginPath ctx)
      (.moveTo ctx x (+ (* height 0.47) 8))
      (.lineTo ctx (+ x 1) (* height 0.47))
      (.stroke ctx))

    ;; Road area
    (set! (.-fillStyle ctx) (:road colors "#404040"))
    (.fillRect ctx 0 (* height 0.59) width (* height 0.30))

    ;; Road lines
    (set! (.-fillStyle ctx) (:road-lines colors "#FFFFFF"))
    (doseq [i (range 0 width 15)]
      (.fillRect ctx i (+ (* height 0.59) 12) 8 2)
      (.fillRect ctx (+ i 4) (+ (* height 0.59) 22) 8 2))

    ;; Draw a detailed car (facing left)
    (let [car-color (get-in theme [:obstacles :car] "#E53935")
          cx 12
          cy (* height 0.65)]
      (set! (.-fillStyle ctx) car-color)
      (.beginPath ctx)
      (.roundRect ctx cx cy 22 10 3)
      (.fill ctx)
      ;; Roof
      (.beginPath ctx)
      (.roundRect ctx (+ cx 4) (- cy 4) 12 5 2)
      (.fill ctx)
      ;; Windows
      (set! (.-fillStyle ctx) "#87CEEB")
      (.fillRect ctx (+ cx 5) (- cy 3) 4 4)
      (.fillRect ctx (+ cx 11) (- cy 3) 4 4)
      ;; Headlight
      (set! (.-fillStyle ctx) "#FFEB3B")
      (.fillRect ctx cx (+ cy 2) 2 3)
      ;; Wheels
      (set! (.-fillStyle ctx) "#1A1A1A")
      (.beginPath ctx)
      (.arc ctx (+ cx 5) (+ cy 10) 3 0 (* 2 js/Math.PI))
      (.fill ctx)
      (.beginPath ctx)
      (.arc ctx (+ cx 17) (+ cy 10) 3 0 (* 2 js/Math.PI))
      (.fill ctx))

    ;; Draw a truck (facing right)
    (let [truck-color (get-in theme [:obstacles :truck] "#1565C0")
          tx 52
          ty (* height 0.75)]
      ;; Cab
      (set! (.-fillStyle ctx) truck-color)
      (.beginPath ctx)
      (.roundRect ctx tx ty 12 10 2)
      (.fill ctx)
      ;; Trailer
      (.beginPath ctx)
      (.roundRect ctx (- tx 18) (- ty 2) 20 12 2)
      (.fill ctx)
      ;; Windows
      (set! (.-fillStyle ctx) "#87CEEB")
      (.fillRect ctx (+ tx 7) (+ ty 1) 4 4)
      ;; Taillight (on left since facing right)
      (set! (.-fillStyle ctx) "#D32F2F")
      (.fillRect ctx (- tx 18) (+ ty 3) 2 3)
      ;; Wheels
      (set! (.-fillStyle ctx) "#1A1A1A")
      (.beginPath ctx)
      (.arc ctx (- tx 12) (+ ty 10) 3 0 (* 2 js/Math.PI))
      (.fill ctx)
      (.beginPath ctx)
      (.arc ctx (+ tx 6) (+ ty 10) 3 0 (* 2 js/Math.PI))
      (.fill ctx))

    ;; Safe zone (bottom)
    (set! (.-fillStyle ctx) (:safe-zone colors "#4A7023"))
    (.fillRect ctx 0 (* height 0.89) width (* height 0.11))

    ;; Draw a detailed tiny frog at bottom
    (let [fx (/ width 2)
          fy (* height 0.94)]
      ;; Body
      (set! (.-fillStyle ctx) "#228B22")
      (.beginPath ctx)
      (.ellipse ctx fx fy 6 4 0 0 (* 2 js/Math.PI))
      (.fill ctx)
      ;; Head
      (.beginPath ctx)
      (.ellipse ctx fx (- fy 4) 4 3 0 0 (* 2 js/Math.PI))
      (.fill ctx)
      ;; Eyes
      (set! (.-fillStyle ctx) "#FFFFFF")
      (.beginPath ctx)
      (.arc ctx (- fx 2) (- fy 5) 2 0 (* 2 js/Math.PI))
      (.fill ctx)
      (.beginPath ctx)
      (.arc ctx (+ fx 2) (- fy 5) 2 0 (* 2 js/Math.PI))
      (.fill ctx)
      ;; Pupils
      (set! (.-fillStyle ctx) "#000000")
      (.beginPath ctx)
      (.arc ctx (- fx 2) (- fy 5) 1 0 (* 2 js/Math.PI))
      (.fill ctx)
      (.beginPath ctx)
      (.arc ctx (+ fx 2) (- fy 5) 1 0 (* 2 js/Math.PI))
      (.fill ctx))))

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

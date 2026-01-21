(ns frogger.render.canvas
  "Canvas rendering for the game."
  (:require [frogger.state :as state]
            [frogger.themes.registry :as themes]
            [frogger.render.sprites :as sprites]))

(defonce canvas-atom (atom nil))
(defonce ctx-atom (atom nil))
(defonce decoration-cache (atom {}))

(defn get-canvas []
  (or @canvas-atom
      (when-let [c (.getElementById js/document "game-canvas")]
        (reset! canvas-atom c)
        c)))

(defn get-ctx []
  (or @ctx-atom
      (when-let [canvas (get-canvas)]
        (reset! ctx-atom (.getContext canvas "2d"))
        @ctx-atom)))

(defn clear-canvas [ctx]
  (.clearRect ctx 0 0 state/canvas-width state/canvas-height))

(defn draw-rect [ctx {:keys [x y width height color]}]
  (set! (.-fillStyle ctx) color)
  (.fillRect ctx x y width height))

;; Seeded random for consistent decoration placement
(defn seeded-random [seed]
  (let [x (js/Math.sin seed)]
    (- (* x 10000) (js/Math.floor (* x 10000)))))

(defn generate-decoration-positions [theme-id density]
  "Generates consistent decoration positions based on theme."
  (let [cache-key (str theme-id "-" density)
        cached (get @decoration-cache cache-key)]
    (if cached
      cached
      (let [count (case density :low 8 :medium 15 :high 25 15)
            positions (for [i (range count)]
                        {:x (* (seeded-random (* i 7.3)) state/canvas-width)
                         :y (+ (* 7 state/cell-size)
                               (* (seeded-random (* i 13.7)) (* 2 state/cell-size)))
                         :size (+ 10 (* (seeded-random (* i 17.1)) 20))
                         :variant (int (* (seeded-random (* i 23.9)) 4))})]
        (swap! decoration-cache assoc cache-key positions)
        positions))))

;; Decoration drawing functions
(defn draw-tree [ctx x y size color]
  (let [trunk-width (* size 0.2)
        trunk-height (* size 0.4)]
    ;; Trunk
    (set! (.-fillStyle ctx) "#5D4037")
    (.fillRect ctx (- x (/ trunk-width 2)) (- y trunk-height) trunk-width trunk-height)
    ;; Foliage (triangle)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.moveTo ctx x (- y trunk-height size))
    (.lineTo ctx (- x (/ size 2)) (- y trunk-height))
    (.lineTo ctx (+ x (/ size 2)) (- y trunk-height))
    (.closePath ctx)
    (.fill ctx)))

(defn draw-reed [ctx x y size color]
  (set! (.-strokeStyle ctx) color)
  (set! (.-lineWidth ctx) 2)
  (.beginPath ctx)
  (.moveTo ctx x y)
  ;; Curved reed
  (.quadraticCurveTo ctx (+ x (* size 0.3)) (- y (* size 0.5)) (+ x (* size 0.1)) (- y size))
  (.stroke ctx)
  ;; Reed tip
  (set! (.-fillStyle ctx) "#8D6E63")
  (.beginPath ctx)
  (.ellipse ctx (+ x (* size 0.1)) (- y size (* size 0.1)) 3 8 0 0 (* 2 js/Math.PI))
  (.fill ctx))

(defn draw-building [ctx x y size colors has-lights]
  (let [width (* size 0.8)
        height size
        color (nth colors (mod (int (* x 0.1)) (count colors)))]
    (set! (.-fillStyle ctx) color)
    (.fillRect ctx (- x (/ width 2)) (- y height) width height)
    ;; Windows
    (when has-lights
      (doseq [wy (range 3)]
        (doseq [wx (range 2)]
          (let [lit? (> (seeded-random (+ (* x wy) wx)) 0.4)]
            (set! (.-fillStyle ctx) (if lit? "#FFF59D" "#37474F"))
            (.fillRect ctx
                       (+ (- x (/ width 2)) 3 (* wx 12))
                       (+ (- y height) 5 (* wy 15))
                       8 10)))))))

(defn draw-sign [ctx x y size colors]
  (let [color (nth colors (mod (int x) (count colors)))]
    ;; Post
    (set! (.-fillStyle ctx) "#757575")
    (.fillRect ctx (- x 2) (- y size) 4 size)
    ;; Sign
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.moveTo ctx (- x 15) (- y size 5))
    (.lineTo ctx (+ x 15) (- y size 5))
    (.lineTo ctx (+ x 15) (- y size 25))
    (.lineTo ctx (- x 15) (- y size 25))
    (.closePath ctx)
    (.fill ctx)))

(defn draw-mushroom [ctx x y size colors glow?]
  (let [color (nth colors (mod (int (* x 0.3)) (count colors)))]
    (when glow?
      (set! (.-shadowColor ctx) color)
      (set! (.-shadowBlur ctx) 15))
    ;; Stem
    (set! (.-fillStyle ctx) "#E8E8E8")
    (.fillRect ctx (- x 4) (- y (* size 0.5)) 8 (* size 0.5))
    ;; Cap
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.ellipse ctx x (- y (* size 0.5)) (* size 0.5) (* size 0.3) 0 js/Math.PI (* 2 js/Math.PI))
    (.fill ctx)
    ;; Spots
    (set! (.-fillStyle ctx) "#FFFFFF")
    (.beginPath ctx)
    (.arc ctx (- x 5) (- y (* size 0.6)) 3 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ x 3) (- y (* size 0.55)) 2 0 (* 2 js/Math.PI))
    (.fill ctx)
    (set! (.-shadowBlur ctx) 0)))

(defn draw-lollipop [ctx x y size colors]
  (let [color (nth colors (mod (int (* x 0.2)) (count colors)))]
    ;; Stick
    (set! (.-fillStyle ctx) "#E8E8E8")
    (.fillRect ctx (- x 2) (- y (* size 0.6)) 4 (* size 0.6))
    ;; Candy swirl
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.arc ctx x (- y (* size 0.6) (* size 0.25)) (* size 0.25) 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Swirl pattern
    (set! (.-strokeStyle ctx) "#FFFFFF")
    (set! (.-lineWidth ctx) 2)
    (.beginPath ctx)
    (.arc ctx x (- y (* size 0.6) (* size 0.25)) (* size 0.12) 0 (* 1.5 js/Math.PI))
    (.stroke ctx)))

(defn draw-star [ctx x y size color twinkle?]
  (let [brightness (if twinkle?
                     (+ 0.5 (* 0.5 (js/Math.sin (/ (js/Date.now) (+ 500 (* x 10))))))
                     1.0)
        alpha (* brightness 1.0)]
    (set! (.-fillStyle ctx) color)
    (set! (.-globalAlpha ctx) alpha)
    (.beginPath ctx)
    (.arc ctx x y (/ size 3) 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Star rays
    (when (> size 5)
      (set! (.-strokeStyle ctx) color)
      (set! (.-lineWidth ctx) 1)
      (doseq [angle [0 (/ js/Math.PI 2) js/Math.PI (* 1.5 js/Math.PI)]]
        (.beginPath ctx)
        (.moveTo ctx x y)
        (.lineTo ctx (+ x (* (js/Math.cos angle) size)) (+ y (* (js/Math.sin angle) size)))
        (.stroke ctx)))
    (set! (.-globalAlpha ctx) 1.0)))

(defn draw-nebula [ctx colors]
  (set! (.-globalAlpha ctx) 0.15)
  (doseq [[i color] (map-indexed vector colors)]
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.ellipse ctx
              (+ 100 (* i 150))
              (+ 100 (* (seeded-random i) 200))
              (+ 100 (* (seeded-random (* i 2)) 100))
              (+ 60 (* (seeded-random (* i 3)) 60))
              (* i 0.5)
              0 (* 2 js/Math.PI))
    (.fill ctx))
  (set! (.-globalAlpha ctx) 1.0))

(defn draw-bubble [ctx x y size color]
  (set! (.-strokeStyle ctx) color)
  (set! (.-lineWidth ctx) 1)
  (set! (.-globalAlpha ctx) 0.6)
  (.beginPath ctx)
  (.arc ctx x y size 0 (* 2 js/Math.PI))
  (.stroke ctx)
  ;; Highlight
  (set! (.-fillStyle ctx) "#FFFFFF")
  (set! (.-globalAlpha ctx) 0.4)
  (.beginPath ctx)
  (.arc ctx (- x (* size 0.3)) (- y (* size 0.3)) (* size 0.2) 0 (* 2 js/Math.PI))
  (.fill ctx)
  (set! (.-globalAlpha ctx) 1.0))

(defn draw-seaweed [ctx x y height colors]
  (let [color (nth colors (mod (int x) (count colors)))
        wave (js/Math.sin (+ (/ (js/Date.now) 1000) (* x 0.1)))]
    (set! (.-strokeStyle ctx) color)
    (set! (.-lineWidth ctx) 4)
    (set! (.-lineCap ctx) "round")
    (.beginPath ctx)
    (.moveTo ctx x y)
    (.quadraticCurveTo ctx (+ x (* wave 15)) (- y (/ height 2)) (+ x (* wave 10)) (- y height))
    (.stroke ctx)))

(defn draw-decorations [ctx theme-id level]
  "Draws theme-specific background decorations."
  (let [decorations (themes/get-decorations theme-id)
        dec-type (:type decorations)
        colors (:colors decorations)
        density (:density decorations)
        positions (generate-decoration-positions theme-id density)]

    ;; Draw theme-specific nebula first (behind everything)
    (when (and (= dec-type :stars) (:nebula decorations))
      (draw-nebula ctx (:nebula decorations)))

    ;; Draw seaweed along bottom for underwater
    (when (and (= dec-type :bubbles) (:seaweed decorations))
      (doseq [i (range 0 state/canvas-width 40)]
        (draw-seaweed ctx i state/canvas-height (+ 40 (* 30 (seeded-random i))) (:seaweed decorations))))

    ;; Draw main decorations in safe zone areas
    (doseq [{:keys [x y size variant]} positions]
      (case dec-type
        :trees (draw-tree ctx x y size (nth colors variant))
        :reeds (draw-reed ctx x y size (nth colors variant))
        :buildings (draw-building ctx x y size colors (:lights decorations))
        :signs (draw-sign ctx x y size colors)
        :mushrooms (draw-mushroom ctx x y size colors (:glow decorations))
        :lollipops (draw-lollipop ctx x y size colors)
        :stars (draw-star ctx
                          (* (seeded-random (* x 2)) state/canvas-width)
                          (* (seeded-random (* y 3)) (* 7 state/cell-size))
                          (+ 2 (* (seeded-random size) 4))
                          (nth colors variant)
                          (:twinkle decorations))
        :bubbles (draw-bubble ctx
                              (+ x (* (js/Math.sin (/ (js/Date.now) 2000)) 5))
                              (- y (* (mod (/ (js/Date.now) 50) 200)))
                              (+ 3 (* size 0.2))
                              (nth colors variant))
        nil))))

(defn draw-background [ctx theme-id]
  (let [theme (themes/get-theme theme-id)
        colors (:colors theme)]
    (draw-rect ctx {:x 0 :y 0
                    :width state/canvas-width
                    :height state/canvas-height
                    :color (:background colors "#2D5016")})
    (draw-rect ctx {:x 0 :y 0
                    :width state/canvas-width
                    :height state/cell-size
                    :color (:goal-area colors "#1B5E20")})
    (draw-rect ctx {:x 0 :y state/cell-size
                    :width state/canvas-width
                    :height (* 6 state/cell-size)
                    :color (:water colors "#1565C0")})
    (draw-rect ctx {:x 0 :y (* 7 state/cell-size)
                    :width state/canvas-width
                    :height (* 2 state/cell-size)
                    :color (:safe-zone colors "#4A7023")})
    (draw-rect ctx {:x 0 :y (* 9 state/cell-size)
                    :width state/canvas-width
                    :height (* 5 state/cell-size)
                    :color (:road colors "#404040")})
    (draw-rect ctx {:x 0 :y (* 14 state/cell-size)
                    :width state/canvas-width
                    :height (* 2 state/cell-size)
                    :color (:safe-zone colors "#4A7023")})))

(defn draw-road-lines [ctx theme-id]
  (let [theme (themes/get-theme theme-id)
        line-color (get-in theme [:colors :road-lines] "#FFFFFF")]
    (set! (.-fillStyle ctx) line-color)
    (doseq [row (range 9 14)]
      (let [y (+ (* row state/cell-size) (/ state/cell-size 2) -2)]
        (doseq [x (range 0 state/canvas-width 40)]
          (.fillRect ctx x y 20 4))))))

(defn draw-goals [ctx goals theme-id]
  (let [theme (themes/get-theme theme-id)]
    (doseq [goal goals]
      (let [{:keys [x y width height reached?]} goal
            color (if reached? "#4CAF50" (get-in theme [:colors :goal-area] "#1B5E20"))]
        (draw-rect ctx {:x x :y y :width width :height height :color color})
        (when reached?
          (sprites/draw-frog-sprite ctx (+ x 5) (+ y 2) 30 30 "#4CAF50"))))))

(defn draw-obstacles [ctx obstacles theme-id]
  (let [theme (themes/get-theme theme-id)]
    (doseq [obstacle obstacles]
      (let [{:keys [x y width height obstacle-type vx]} obstacle
            color (or (get-in theme [:obstacles obstacle-type])
                      (:color obstacle))
            direction (if (pos? vx) :right :left)]
        (case obstacle-type
          :car (sprites/draw-car-sprite ctx x y width height color :direction direction)
          :truck (sprites/draw-truck-sprite ctx x y width height color :direction direction)
          :bus (sprites/draw-bus-sprite ctx x y width height color :direction direction)
          :motorcycle (sprites/draw-motorcycle-sprite ctx x y width height color :direction direction)
          :race-car (sprites/draw-race-car-sprite ctx x y width height color :direction direction)
          ;; Default fallback for unknown types
          (sprites/draw-car-sprite ctx x y width height color :direction direction))))))

(defn draw-platforms [ctx platforms theme-id]
  (let [theme (themes/get-theme theme-id)]
    (doseq [platform platforms]
      (let [{:keys [x y width height platform-type sinking?]} platform
            base-color (or (get-in theme [:platforms platform-type])
                           (:color platform))
            color (if sinking? "rgba(100,100,100,0.5)" base-color)]
        (case platform-type
          (:log-small :log-medium :log-large)
          (sprites/draw-log-sprite ctx x y width height color)

          :lily-pad
          (sprites/draw-lily-pad ctx x y width height color)

          :turtle
          (do
            (draw-rect ctx {:x x :y y :width width :height height :color color})
            ;; Draw multiple turtle shells across the platform
            (let [turtle-size 25
                  turtle-count (max 1 (int (/ width (+ turtle-size 10))))]
              (doseq [i (range turtle-count)]
                (let [tx (+ x 5 (* i (+ turtle-size 10)))]
                  (sprites/draw-turtle-shell ctx tx (+ y 5) turtle-size turtle-size color)))))

          ;; Default fallback
          (draw-rect ctx {:x x :y y :width width :height height :color color}))))))

(defn draw-checkpoints [ctx checkpoints]
  (doseq [checkpoint checkpoints]
    (let [{:keys [x y width height reached? color]} checkpoint]
      (sprites/draw-target-sprite ctx x y width height color reached?))))

(defn draw-frog [ctx frog]
  (when frog
    (let [{:keys [x y width height color invincible?]} frog
          draw-color (if invincible?
                       (if (< (mod (js/Date.now) 200) 100) color "transparent")
                       color)]
      (when (not= draw-color "transparent")
        (sprites/draw-frog-sprite ctx x y width height draw-color)))))

(defn render [game-state]
  (when-let [ctx (get-ctx)]
    (let [{:keys [frog obstacles platforms goals checkpoints theme-id level]} game-state]
      (clear-canvas ctx)
      (draw-background ctx theme-id)
      (draw-decorations ctx theme-id level)
      (draw-road-lines ctx theme-id)
      (draw-goals ctx goals theme-id)
      (draw-checkpoints ctx checkpoints)
      (draw-platforms ctx platforms theme-id)
      (draw-obstacles ctx obstacles theme-id)
      (draw-frog ctx frog))))

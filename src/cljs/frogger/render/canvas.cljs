(ns frogger.render.canvas
  "Canvas rendering for the game."
  (:require [frogger.state :as state]
            [frogger.themes.registry :as themes]
            [frogger.render.sprites :as sprites]))

(defonce canvas-atom (atom nil))
(defonce ctx-atom (atom nil))

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
    (let [{:keys [frog obstacles platforms goals checkpoints theme-id]} game-state]
      (clear-canvas ctx)
      (draw-background ctx theme-id)
      (draw-road-lines ctx theme-id)
      (draw-goals ctx goals theme-id)
      (draw-checkpoints ctx checkpoints)
      (draw-platforms ctx platforms theme-id)
      (draw-obstacles ctx obstacles theme-id)
      (draw-frog ctx frog))))

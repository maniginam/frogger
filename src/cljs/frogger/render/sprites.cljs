(ns frogger.render.sprites
  "Sprite drawing utilities for game entities.")

(defn- darken-color
  "Darkens a hex color by a percentage."
  [color amount]
  (let [hex (if (= (first color) "#") (subs color 1) color)
        r (js/parseInt (subs hex 0 2) 16)
        g (js/parseInt (subs hex 2 4) 16)
        b (js/parseInt (subs hex 4 6) 16)
        factor (- 1 amount)
        new-r (js/Math.floor (* r factor))
        new-g (js/Math.floor (* g factor))
        new-b (js/Math.floor (* b factor))]
    (str "#"
         (.padStart (.toString new-r 16) 2 "0")
         (.padStart (.toString new-g 16) 2 "0")
         (.padStart (.toString new-b 16) 2 "0"))))

(defn- lighten-color
  "Lightens a hex color by a percentage."
  [color amount]
  (let [hex (if (= (first color) "#") (subs color 1) color)
        r (js/parseInt (subs hex 0 2) 16)
        g (js/parseInt (subs hex 2 4) 16)
        b (js/parseInt (subs hex 4 6) 16)
        new-r (js/Math.min 255 (js/Math.floor (+ r (* (- 255 r) amount))))
        new-g (js/Math.min 255 (js/Math.floor (+ g (* (- 255 g) amount))))
        new-b (js/Math.min 255 (js/Math.floor (+ b (* (- 255 b) amount))))]
    (str "#"
         (.padStart (.toString new-r 16) 2 "0")
         (.padStart (.toString new-g 16) 2 "0")
         (.padStart (.toString new-b 16) 2 "0"))))

(defn draw-frog-sprite
  "Draws a detailed frog sprite with proper anatomy."
  [ctx x y width height color]
  (let [cx (+ x (/ width 2))
        cy (+ y (/ height 2))
        scale-x (/ width 40)
        scale-y (/ height 40)
        dark-color (darken-color color 0.25)
        light-color (lighten-color color 0.3)]

    ;; Back legs (drawn first, behind body)
    (set! (.-fillStyle ctx) dark-color)
    ;; Left back leg - thigh
    (.beginPath ctx)
    (.ellipse ctx (- cx (* 12 scale-x)) (+ cy (* 8 scale-y)) (* 8 scale-x) (* 5 scale-y) -0.3 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Left back leg - calf/foot
    (.beginPath ctx)
    (.moveTo ctx (- cx (* 18 scale-x)) (+ cy (* 6 scale-y)))
    (.quadraticCurveTo ctx (- cx (* 22 scale-x)) (+ cy (* 12 scale-y)) (- cx (* 16 scale-x)) (+ cy (* 16 scale-y)))
    (.quadraticCurveTo ctx (- cx (* 10 scale-x)) (+ cy (* 14 scale-y)) (- cx (* 8 scale-x)) (+ cy (* 10 scale-y)))
    (.closePath ctx)
    (.fill ctx)
    ;; Left webbed foot
    (.beginPath ctx)
    (.ellipse ctx (- cx (* 18 scale-x)) (+ cy (* 15 scale-y)) (* 5 scale-x) (* 3 scale-y) -0.4 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Right back leg - thigh
    (.beginPath ctx)
    (.ellipse ctx (+ cx (* 12 scale-x)) (+ cy (* 8 scale-y)) (* 8 scale-x) (* 5 scale-y) 0.3 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Right back leg - calf/foot
    (.beginPath ctx)
    (.moveTo ctx (+ cx (* 18 scale-x)) (+ cy (* 6 scale-y)))
    (.quadraticCurveTo ctx (+ cx (* 22 scale-x)) (+ cy (* 12 scale-y)) (+ cx (* 16 scale-x)) (+ cy (* 16 scale-y)))
    (.quadraticCurveTo ctx (+ cx (* 10 scale-x)) (+ cy (* 14 scale-y)) (+ cx (* 8 scale-x)) (+ cy (* 10 scale-y)))
    (.closePath ctx)
    (.fill ctx)
    ;; Right webbed foot
    (.beginPath ctx)
    (.ellipse ctx (+ cx (* 18 scale-x)) (+ cy (* 15 scale-y)) (* 5 scale-x) (* 3 scale-y) 0.4 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Main body (oval shape)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.ellipse ctx cx (+ cy (* 2 scale-y)) (* 14 scale-x) (* 11 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Body highlight/belly
    (set! (.-fillStyle ctx) light-color)
    (.beginPath ctx)
    (.ellipse ctx cx (+ cy (* 5 scale-y)) (* 9 scale-x) (* 6 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Front legs
    (set! (.-fillStyle ctx) dark-color)
    ;; Left front leg
    (.beginPath ctx)
    (.moveTo ctx (- cx (* 10 scale-x)) (+ cy (* 2 scale-y)))
    (.quadraticCurveTo ctx (- cx (* 16 scale-x)) (+ cy (* 4 scale-y)) (- cx (* 14 scale-x)) (+ cy (* 10 scale-y)))
    (.quadraticCurveTo ctx (- cx (* 12 scale-x)) (+ cy (* 8 scale-y)) (- cx (* 8 scale-x)) (+ cy (* 4 scale-y)))
    (.closePath ctx)
    (.fill ctx)
    ;; Left front foot
    (.beginPath ctx)
    (.ellipse ctx (- cx (* 14 scale-x)) (+ cy (* 10 scale-y)) (* 4 scale-x) (* 2.5 scale-y) -0.3 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Right front leg
    (.beginPath ctx)
    (.moveTo ctx (+ cx (* 10 scale-x)) (+ cy (* 2 scale-y)))
    (.quadraticCurveTo ctx (+ cx (* 16 scale-x)) (+ cy (* 4 scale-y)) (+ cx (* 14 scale-x)) (+ cy (* 10 scale-y)))
    (.quadraticCurveTo ctx (+ cx (* 12 scale-x)) (+ cy (* 8 scale-y)) (+ cx (* 8 scale-x)) (+ cy (* 4 scale-y)))
    (.closePath ctx)
    (.fill ctx)
    ;; Right front foot
    (.beginPath ctx)
    (.ellipse ctx (+ cx (* 14 scale-x)) (+ cy (* 10 scale-y)) (* 4 scale-x) (* 2.5 scale-y) 0.3 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Head (slightly overlapping body at top)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.ellipse ctx cx (- cy (* 6 scale-y)) (* 11 scale-x) (* 8 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Eye bumps (raised areas on head)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.ellipse ctx (- cx (* 6 scale-x)) (- cy (* 10 scale-y)) (* 5 scale-x) (* 4 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.ellipse ctx (+ cx (* 6 scale-x)) (- cy (* 10 scale-y)) (* 5 scale-x) (* 4 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Eyes (white with black pupil)
    (set! (.-fillStyle ctx) "#FFFFFF")
    (.beginPath ctx)
    (.arc ctx (- cx (* 6 scale-x)) (- cy (* 10 scale-y)) (* 4 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ cx (* 6 scale-x)) (- cy (* 10 scale-y)) (* 4 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Pupils
    (set! (.-fillStyle ctx) "#000000")
    (.beginPath ctx)
    (.arc ctx (- cx (* 6 scale-x)) (- cy (* 10 scale-y)) (* 2 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ cx (* 6 scale-x)) (- cy (* 10 scale-y)) (* 2 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Eye shine
    (set! (.-fillStyle ctx) "#FFFFFF")
    (.beginPath ctx)
    (.arc ctx (- cx (* 7 scale-x)) (- cy (* 11 scale-y)) (* 1 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ cx (* 5 scale-x)) (- cy (* 11 scale-y)) (* 1 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Nostrils
    (set! (.-fillStyle ctx) dark-color)
    (.beginPath ctx)
    (.arc ctx (- cx (* 2 scale-x)) (- cy (* 4 scale-y)) (* 1 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ cx (* 2 scale-x)) (- cy (* 4 scale-y)) (* 1 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Mouth line (subtle smile)
    (set! (.-strokeStyle ctx) dark-color)
    (set! (.-lineWidth ctx) (* 1.5 scale-x))
    (set! (.-lineCap ctx) "round")
    (.beginPath ctx)
    (.moveTo ctx (- cx (* 6 scale-x)) (- cy (* 2 scale-y)))
    (.quadraticCurveTo ctx cx (+ cy (* 1 scale-y)) (+ cx (* 6 scale-x)) (- cy (* 2 scale-y)))
    (.stroke ctx)))

(defn draw-turtle-shell
  "Draws a detailed turtle with shell pattern, head, and flippers."
  [ctx x y width height color]
  (let [cx (+ x (/ width 2))
        cy (+ y (/ height 2))
        scale-x (/ width 25)
        scale-y (/ height 25)
        dark-color (darken-color color 0.25)
        light-color (lighten-color color 0.2)]

    ;; Flippers (drawn behind shell)
    (set! (.-fillStyle ctx) dark-color)
    ;; Front left flipper
    (.beginPath ctx)
    (.ellipse ctx (- cx (* 10 scale-x)) (- cy (* 2 scale-y)) (* 5 scale-x) (* 3 scale-y) -0.5 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Front right flipper
    (.beginPath ctx)
    (.ellipse ctx (+ cx (* 10 scale-x)) (- cy (* 2 scale-y)) (* 5 scale-x) (* 3 scale-y) 0.5 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Back left flipper
    (.beginPath ctx)
    (.ellipse ctx (- cx (* 8 scale-x)) (+ cy (* 6 scale-y)) (* 4 scale-x) (* 3 scale-y) -0.3 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Back right flipper
    (.beginPath ctx)
    (.ellipse ctx (+ cx (* 8 scale-x)) (+ cy (* 6 scale-y)) (* 4 scale-x) (* 3 scale-y) 0.3 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Tail
    (.beginPath ctx)
    (.ellipse ctx cx (+ cy (* 10 scale-y)) (* 2 scale-x) (* 3 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Main shell (dome shape)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.ellipse ctx cx cy (* 10 scale-x) (* 8 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Shell ridge (center hexagon-ish pattern)
    (set! (.-fillStyle ctx) light-color)
    (.beginPath ctx)
    (.ellipse ctx cx cy (* 5 scale-x) (* 4 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Shell pattern lines
    (set! (.-strokeStyle ctx) dark-color)
    (set! (.-lineWidth ctx) 1)

    ;; Horizontal line through center
    (.beginPath ctx)
    (.moveTo ctx (- cx (* 9 scale-x)) cy)
    (.lineTo ctx (+ cx (* 9 scale-x)) cy)
    (.stroke ctx)

    ;; Vertical segments
    (.beginPath ctx)
    (.moveTo ctx cx (- cy (* 7 scale-y)))
    (.lineTo ctx cx (- cy (* 4 scale-y)))
    (.stroke ctx)
    (.beginPath ctx)
    (.moveTo ctx cx (+ cy (* 4 scale-y)))
    (.lineTo ctx cx (+ cy (* 7 scale-y)))
    (.stroke ctx)

    ;; Diagonal lines forming shell pattern
    (.beginPath ctx)
    (.moveTo ctx (- cx (* 4 scale-x)) (- cy (* 4 scale-y)))
    (.lineTo ctx (- cx (* 8 scale-x)) cy)
    (.stroke ctx)
    (.beginPath ctx)
    (.moveTo ctx (+ cx (* 4 scale-x)) (- cy (* 4 scale-y)))
    (.lineTo ctx (+ cx (* 8 scale-x)) cy)
    (.stroke ctx)
    (.beginPath ctx)
    (.moveTo ctx (- cx (* 4 scale-x)) (+ cy (* 4 scale-y)))
    (.lineTo ctx (- cx (* 8 scale-x)) cy)
    (.stroke ctx)
    (.beginPath ctx)
    (.moveTo ctx (+ cx (* 4 scale-x)) (+ cy (* 4 scale-y)))
    (.lineTo ctx (+ cx (* 8 scale-x)) cy)
    (.stroke ctx)

    ;; Shell edge highlight
    (set! (.-strokeStyle ctx) light-color)
    (set! (.-lineWidth ctx) 1.5)
    (.beginPath ctx)
    (.ellipse ctx cx cy (* 9 scale-x) (* 7 scale-y) 0 3.5 5.8)
    (.stroke ctx)

    ;; Head
    (set! (.-fillStyle ctx) dark-color)
    (.beginPath ctx)
    (.ellipse ctx cx (- cy (* 10 scale-y)) (* 4 scale-x) (* 3 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Eyes
    (set! (.-fillStyle ctx) "#000000")
    (.beginPath ctx)
    (.arc ctx (- cx (* 2 scale-x)) (- cy (* 10 scale-y)) (* 1 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ cx (* 2 scale-x)) (- cy (* 10 scale-y)) (* 1 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)))

(defn draw-car-sprite
  "Draws a detailed sedan-style car sprite. Direction: :left or :right (default :left)"
  [ctx orig-x y width height color & {:keys [direction] :or {direction :left}}]
  (when (= direction :right)
    (.save ctx)
    (.translate ctx (+ orig-x width) 0)
    (.scale ctx -1 1))
  (let [x (if (= direction :right) 0 orig-x)
        cx (+ x (/ width 2))
        cy (+ y (/ height 2))
        scale-x (/ width 40)
        scale-y (/ height 36)
        dark-color (darken-color color 0.3)
        light-color (lighten-color color 0.2)]

    ;; Shadow under car
    (set! (.-fillStyle ctx) "rgba(0,0,0,0.2)")
    (.beginPath ctx)
    (.ellipse ctx cx (+ y height (* -2 scale-y)) (* 18 scale-x) (* 3 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Main body (lower)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 2 scale-x)) (+ y (* 12 scale-y)) (- width (* 4 scale-x)) (* 18 scale-y) (* 4 scale-x))
    (.fill ctx)

    ;; Roof/cabin (upper)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 8 scale-x)) (+ y (* 12 scale-y)))
    (.lineTo ctx (+ x (* 12 scale-x)) (+ y (* 4 scale-y)))
    (.lineTo ctx (+ x width (* -10 scale-x)) (+ y (* 4 scale-y)))
    (.lineTo ctx (+ x width (* -6 scale-x)) (+ y (* 12 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Front windshield
    (set! (.-fillStyle ctx) "#87CEEB")
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 9 scale-x)) (+ y (* 12 scale-y)))
    (.lineTo ctx (+ x (* 13 scale-x)) (+ y (* 5 scale-y)))
    (.lineTo ctx (+ x (* 18 scale-x)) (+ y (* 5 scale-y)))
    (.lineTo ctx (+ x (* 18 scale-x)) (+ y (* 12 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Rear windshield
    (.beginPath ctx)
    (.moveTo ctx (+ x width (* -9 scale-x)) (+ y (* 12 scale-y)))
    (.lineTo ctx (+ x width (* -11 scale-x)) (+ y (* 5 scale-y)))
    (.lineTo ctx (+ x width (* -18 scale-x)) (+ y (* 5 scale-y)))
    (.lineTo ctx (+ x width (* -18 scale-x)) (+ y (* 12 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Window shine
    (set! (.-fillStyle ctx) "rgba(255,255,255,0.3)")
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 10 scale-x)) (+ y (* 11 scale-y)))
    (.lineTo ctx (+ x (* 13 scale-x)) (+ y (* 6 scale-y)))
    (.lineTo ctx (+ x (* 15 scale-x)) (+ y (* 6 scale-y)))
    (.lineTo ctx (+ x (* 15 scale-x)) (+ y (* 11 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Body trim line
    (set! (.-strokeStyle ctx) dark-color)
    (set! (.-lineWidth ctx) (* 1 scale-y))
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 3 scale-x)) (+ y (* 20 scale-y)))
    (.lineTo ctx (+ x width (* -3 scale-x)) (+ y (* 20 scale-y)))
    (.stroke ctx)

    ;; Headlight
    (set! (.-fillStyle ctx) "#FFEB3B")
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 2 scale-x)) (+ y (* 14 scale-y)) (* 4 scale-x) (* 6 scale-y) (* 1 scale-x))
    (.fill ctx)

    ;; Taillight
    (set! (.-fillStyle ctx) "#D32F2F")
    (.beginPath ctx)
    (.roundRect ctx (+ x width (* -6 scale-x)) (+ y (* 14 scale-y)) (* 4 scale-x) (* 6 scale-y) (* 1 scale-x))
    (.fill ctx)

    ;; Wheels
    (set! (.-fillStyle ctx) "#1A1A1A")
    (.beginPath ctx)
    (.ellipse ctx (+ x (* 10 scale-x)) (+ y (* 28 scale-y)) (* 6 scale-x) (* 5 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.ellipse ctx (+ x width (* -10 scale-x)) (+ y (* 28 scale-y)) (* 6 scale-x) (* 5 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Wheel hubcaps
    (set! (.-fillStyle ctx) "#9E9E9E")
    (.beginPath ctx)
    (.arc ctx (+ x (* 10 scale-x)) (+ y (* 28 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ x width (* -10 scale-x)) (+ y (* 28 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx))
  (when (= direction :right)
    (.restore ctx)))

(defn draw-truck-sprite
  "Draws a detailed truck/pickup sprite. Direction: :left or :right (default :left)"
  [ctx orig-x y width height color & {:keys [direction] :or {direction :left}}]
  (when (= direction :right)
    (.save ctx)
    (.translate ctx (+ orig-x width) 0)
    (.scale ctx -1 1))
  (let [x (if (= direction :right) 0 orig-x)
        scale-x (/ width 80)
        scale-y (/ height 36)
        dark-color (darken-color color 0.3)]

    ;; Shadow
    (set! (.-fillStyle ctx) "rgba(0,0,0,0.2)")
    (.beginPath ctx)
    (.ellipse ctx (+ x (/ width 2)) (+ y height (* -2 scale-y)) (* 38 scale-x) (* 3 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Truck bed
    (set! (.-fillStyle ctx) dark-color)
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 35 scale-x)) (+ y (* 8 scale-y)) (* 42 scale-x) (* 20 scale-y) (* 3 scale-x))
    (.fill ctx)

    ;; Truck bed interior
    (set! (.-fillStyle ctx) "#424242")
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 38 scale-x)) (+ y (* 10 scale-y)) (* 36 scale-x) (* 16 scale-y) (* 2 scale-x))
    (.fill ctx)

    ;; Cab body
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 2 scale-x)) (+ y (* 10 scale-y)) (* 36 scale-x) (* 20 scale-y) (* 4 scale-x))
    (.fill ctx)

    ;; Cab roof
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 6 scale-x)) (+ y (* 10 scale-y)))
    (.lineTo ctx (+ x (* 10 scale-x)) (+ y (* 3 scale-y)))
    (.lineTo ctx (+ x (* 32 scale-x)) (+ y (* 3 scale-y)))
    (.lineTo ctx (+ x (* 34 scale-x)) (+ y (* 10 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Windshield
    (set! (.-fillStyle ctx) "#87CEEB")
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 7 scale-x)) (+ y (* 10 scale-y)))
    (.lineTo ctx (+ x (* 11 scale-x)) (+ y (* 4 scale-y)))
    (.lineTo ctx (+ x (* 22 scale-x)) (+ y (* 4 scale-y)))
    (.lineTo ctx (+ x (* 22 scale-x)) (+ y (* 10 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Side window
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 24 scale-x)) (+ y (* 10 scale-y)))
    (.lineTo ctx (+ x (* 24 scale-x)) (+ y (* 4 scale-y)))
    (.lineTo ctx (+ x (* 31 scale-x)) (+ y (* 4 scale-y)))
    (.lineTo ctx (+ x (* 33 scale-x)) (+ y (* 10 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Headlight
    (set! (.-fillStyle ctx) "#FFEB3B")
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 2 scale-x)) (+ y (* 14 scale-y)) (* 4 scale-x) (* 8 scale-y) (* 1 scale-x))
    (.fill ctx)

    ;; Taillight
    (set! (.-fillStyle ctx) "#D32F2F")
    (.beginPath ctx)
    (.roundRect ctx (+ x width (* -5 scale-x)) (+ y (* 14 scale-y)) (* 3 scale-x) (* 6 scale-y) (* 1 scale-x))
    (.fill ctx)

    ;; Wheels
    (set! (.-fillStyle ctx) "#1A1A1A")
    (.beginPath ctx)
    (.ellipse ctx (+ x (* 14 scale-x)) (+ y (* 28 scale-y)) (* 7 scale-x) (* 5 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.ellipse ctx (+ x width (* -14 scale-x)) (+ y (* 28 scale-y)) (* 7 scale-x) (* 5 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Hubcaps
    (set! (.-fillStyle ctx) "#9E9E9E")
    (.beginPath ctx)
    (.arc ctx (+ x (* 14 scale-x)) (+ y (* 28 scale-y)) (* 3.5 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ x width (* -14 scale-x)) (+ y (* 28 scale-y)) (* 3.5 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx))
  (when (= direction :right)
    (.restore ctx)))

(defn draw-bus-sprite
  "Draws a detailed bus sprite. Direction: :left or :right (default :left)"
  [ctx orig-x y width height color & {:keys [direction] :or {direction :left}}]
  (when (= direction :right)
    (.save ctx)
    (.translate ctx (+ orig-x width) 0)
    (.scale ctx -1 1))
  (let [x (if (= direction :right) 0 orig-x)
        scale-x (/ width 120)
        scale-y (/ height 36)
        dark-color (darken-color color 0.25)
        light-color (lighten-color color 0.15)]

    ;; Shadow
    (set! (.-fillStyle ctx) "rgba(0,0,0,0.2)")
    (.beginPath ctx)
    (.ellipse ctx (+ x (/ width 2)) (+ y height (* -2 scale-y)) (* 58 scale-x) (* 3 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Main body
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 3 scale-x)) (+ y (* 4 scale-y)) (- width (* 6 scale-x)) (* 26 scale-y) (* 5 scale-x))
    (.fill ctx)

    ;; Roof
    (set! (.-fillStyle ctx) light-color)
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 5 scale-x)) (+ y (* 2 scale-y)) (- width (* 10 scale-x)) (* 6 scale-y) (* 3 scale-x))
    (.fill ctx)

    ;; Windows (multiple)
    (set! (.-fillStyle ctx) "#87CEEB")
    (doseq [i (range 5)]
      (let [win-x (+ x (* (+ 12 (* i 20)) scale-x))]
        (.beginPath ctx)
        (.roundRect ctx win-x (+ y (* 6 scale-y)) (* 16 scale-x) (* 12 scale-y) (* 2 scale-x))
        (.fill ctx)))

    ;; Front windshield
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 4 scale-x)) (+ y (* 6 scale-y)) (* 10 scale-x) (* 14 scale-y) (* 2 scale-x))
    (.fill ctx)

    ;; Door
    (set! (.-fillStyle ctx) dark-color)
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 15 scale-x)) (+ y (* 6 scale-y)) (* 12 scale-x) (* 22 scale-y) (* 2 scale-x))
    (.fill ctx)

    ;; Door windows
    (set! (.-fillStyle ctx) "#87CEEB")
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 17 scale-x)) (+ y (* 8 scale-y)) (* 8 scale-x) (* 10 scale-y) (* 1 scale-x))
    (.fill ctx)

    ;; Body stripe
    (set! (.-fillStyle ctx) dark-color)
    (.beginPath ctx)
    (.fillRect ctx (+ x (* 3 scale-x)) (+ y (* 22 scale-y)) (- width (* 6 scale-x)) (* 3 scale-y))

    ;; Headlights
    (set! (.-fillStyle ctx) "#FFEB3B")
    (.beginPath ctx)
    (.arc ctx (+ x (* 6 scale-x)) (+ y (* 24 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Taillights
    (set! (.-fillStyle ctx) "#D32F2F")
    (.beginPath ctx)
    (.arc ctx (+ x width (* -6 scale-x)) (+ y (* 24 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Wheels
    (set! (.-fillStyle ctx) "#1A1A1A")
    (.beginPath ctx)
    (.ellipse ctx (+ x (* 20 scale-x)) (+ y (* 28 scale-y)) (* 7 scale-x) (* 5 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.ellipse ctx (+ x width (* -20 scale-x)) (+ y (* 28 scale-y)) (* 7 scale-x) (* 5 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Hubcaps
    (set! (.-fillStyle ctx) "#9E9E9E")
    (.beginPath ctx)
    (.arc ctx (+ x (* 20 scale-x)) (+ y (* 28 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ x width (* -20 scale-x)) (+ y (* 28 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx))
  (when (= direction :right)
    (.restore ctx)))

(defn draw-motorcycle-sprite
  "Draws a detailed motorcycle sprite. Direction: :left or :right (default :left)"
  [ctx orig-x y width height color & {:keys [direction] :or {direction :left}}]
  (when (= direction :right)
    (.save ctx)
    (.translate ctx (+ orig-x width) 0)
    (.scale ctx -1 1))
  (let [x (if (= direction :right) 0 orig-x)
        cx (+ x (/ width 2))
        scale-x (/ width 30)
        scale-y (/ height 36)
        dark-color (darken-color color 0.3)]

    ;; Shadow
    (set! (.-fillStyle ctx) "rgba(0,0,0,0.15)")
    (.beginPath ctx)
    (.ellipse ctx cx (+ y height (* -3 scale-y)) (* 12 scale-x) (* 2 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Rear wheel
    (set! (.-fillStyle ctx) "#1A1A1A")
    (.beginPath ctx)
    (.arc ctx (+ x width (* -7 scale-x)) (+ y (* 26 scale-y)) (* 6 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Rear wheel hub
    (set! (.-fillStyle ctx) "#616161")
    (.beginPath ctx)
    (.arc ctx (+ x width (* -7 scale-x)) (+ y (* 26 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Front wheel
    (set! (.-fillStyle ctx) "#1A1A1A")
    (.beginPath ctx)
    (.arc ctx (+ x (* 7 scale-x)) (+ y (* 26 scale-y)) (* 6 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    ;; Front wheel hub
    (set! (.-fillStyle ctx) "#616161")
    (.beginPath ctx)
    (.arc ctx (+ x (* 7 scale-x)) (+ y (* 26 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Frame/body
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 4 scale-x)) (+ y (* 22 scale-y)))
    (.lineTo ctx (+ x (* 8 scale-x)) (+ y (* 12 scale-y)))
    (.lineTo ctx (+ x (* 18 scale-x)) (+ y (* 10 scale-y)))
    (.lineTo ctx (+ x (* 24 scale-x)) (+ y (* 18 scale-y)))
    (.lineTo ctx (+ x (* 20 scale-x)) (+ y (* 22 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Gas tank
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.ellipse ctx (+ x (* 14 scale-x)) (+ y (* 14 scale-y)) (* 6 scale-x) (* 4 scale-y) -0.2 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Tank stripe
    (set! (.-fillStyle ctx) dark-color)
    (.beginPath ctx)
    (.ellipse ctx (+ x (* 14 scale-x)) (+ y (* 14 scale-y)) (* 4 scale-x) (* 2 scale-y) -0.2 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Seat
    (set! (.-fillStyle ctx) "#2C2C2C")
    (.beginPath ctx)
    (.ellipse ctx (+ x (* 20 scale-x)) (+ y (* 12 scale-y)) (* 5 scale-x) (* 3 scale-y) 0.3 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Handlebars
    (set! (.-strokeStyle ctx) "#424242")
    (set! (.-lineWidth ctx) (* 2 scale-x))
    (set! (.-lineCap ctx) "round")
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 6 scale-x)) (+ y (* 8 scale-y)))
    (.lineTo ctx (+ x (* 10 scale-x)) (+ y (* 6 scale-y)))
    (.stroke ctx)

    ;; Headlight
    (set! (.-fillStyle ctx) "#FFEB3B")
    (.beginPath ctx)
    (.arc ctx (+ x (* 5 scale-x)) (+ y (* 14 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Taillight
    (set! (.-fillStyle ctx) "#D32F2F")
    (.beginPath ctx)
    (.arc ctx (+ x width (* -5 scale-x)) (+ y (* 16 scale-y)) (* 2 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Exhaust pipe
    (set! (.-fillStyle ctx) "#757575")
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 16 scale-x)) (+ y (* 24 scale-y)) (* 10 scale-x) (* 3 scale-y) (* 1 scale-x))
    (.fill ctx))
  (when (= direction :right)
    (.restore ctx)))

(defn draw-race-car-sprite
  "Draws a detailed race car/sports car sprite. Direction: :left or :right (default :left)"
  [ctx orig-x y width height color & {:keys [direction] :or {direction :left}}]
  (when (= direction :right)
    (.save ctx)
    (.translate ctx (+ orig-x width) 0)
    (.scale ctx -1 1))
  (let [x (if (= direction :right) 0 orig-x)
        cx (+ x (/ width 2))
        scale-x (/ width 50)
        scale-y (/ height 36)
        dark-color (darken-color color 0.25)
        light-color (lighten-color color 0.2)]

    ;; Shadow
    (set! (.-fillStyle ctx) "rgba(0,0,0,0.2)")
    (.beginPath ctx)
    (.ellipse ctx cx (+ y height (* -2 scale-y)) (* 24 scale-x) (* 3 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Main body (low, sleek profile)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 2 scale-x)) (+ y (* 20 scale-y)))
    (.quadraticCurveTo ctx (+ x (* 4 scale-x)) (+ y (* 12 scale-y)) (+ x (* 14 scale-x)) (+ y (* 10 scale-y)))
    (.lineTo ctx (+ x (* 36 scale-x)) (+ y (* 10 scale-y)))
    (.quadraticCurveTo ctx (+ x (* 46 scale-x)) (+ y (* 12 scale-y)) (+ x (* 48 scale-x)) (+ y (* 20 scale-y)))
    (.lineTo ctx (+ x (* 48 scale-x)) (+ y (* 26 scale-y)))
    (.lineTo ctx (+ x (* 2 scale-x)) (+ y (* 26 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Cockpit/cabin
    (set! (.-fillStyle ctx) dark-color)
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 18 scale-x)) (+ y (* 10 scale-y)))
    (.quadraticCurveTo ctx (+ x (* 20 scale-x)) (+ y (* 6 scale-y)) (+ x (* 25 scale-x)) (+ y (* 5 scale-y)))
    (.lineTo ctx (+ x (* 32 scale-x)) (+ y (* 5 scale-y)))
    (.quadraticCurveTo ctx (+ x (* 36 scale-x)) (+ y (* 6 scale-y)) (+ x (* 38 scale-x)) (+ y (* 10 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Windshield
    (set! (.-fillStyle ctx) "#87CEEB")
    (.beginPath ctx)
    (.moveTo ctx (+ x (* 19 scale-x)) (+ y (* 10 scale-y)))
    (.quadraticCurveTo ctx (+ x (* 21 scale-x)) (+ y (* 7 scale-y)) (+ x (* 25 scale-x)) (+ y (* 6 scale-y)))
    (.lineTo ctx (+ x (* 32 scale-x)) (+ y (* 6 scale-y)))
    (.quadraticCurveTo ctx (+ x (* 35 scale-x)) (+ y (* 7 scale-y)) (+ x (* 37 scale-x)) (+ y (* 10 scale-y)))
    (.closePath ctx)
    (.fill ctx)

    ;; Racing stripe
    (set! (.-fillStyle ctx) "#FFFFFF")
    (.beginPath ctx)
    (.fillRect ctx (+ x (* 6 scale-x)) (+ y (* 14 scale-y)) (* 38 scale-x) (* 3 scale-y))

    ;; Air intake
    (set! (.-fillStyle ctx) "#1A1A1A")
    (.beginPath ctx)
    (.roundRect ctx (+ x (* 4 scale-x)) (+ y (* 18 scale-y)) (* 6 scale-x) (* 4 scale-y) (* 1 scale-x))
    (.fill ctx)

    ;; Spoiler
    (set! (.-fillStyle ctx) dark-color)
    (.beginPath ctx)
    (.fillRect ctx (+ x (* 44 scale-x)) (+ y (* 6 scale-y)) (* 4 scale-x) (* 3 scale-y))
    (.beginPath ctx)
    (.fillRect ctx (+ x (* 43 scale-x)) (+ y (* 4 scale-y)) (* 6 scale-x) (* 2 scale-y))

    ;; Headlights
    (set! (.-fillStyle ctx) "#FFEB3B")
    (.beginPath ctx)
    (.ellipse ctx (+ x (* 5 scale-x)) (+ y (* 22 scale-y)) (* 3 scale-x) (* 2 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Taillights
    (set! (.-fillStyle ctx) "#D32F2F")
    (.beginPath ctx)
    (.ellipse ctx (+ x width (* -5 scale-x)) (+ y (* 22 scale-y)) (* 3 scale-x) (* 2 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Wheels
    (set! (.-fillStyle ctx) "#1A1A1A")
    (.beginPath ctx)
    (.ellipse ctx (+ x (* 12 scale-x)) (+ y (* 26 scale-y)) (* 6 scale-x) (* 4 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.ellipse ctx (+ x width (* -12 scale-x)) (+ y (* 26 scale-y)) (* 6 scale-x) (* 4 scale-y) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Wheel spokes/rims (sporty)
    (set! (.-fillStyle ctx) "#BDBDBD")
    (.beginPath ctx)
    (.arc ctx (+ x (* 12 scale-x)) (+ y (* 26 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx)
    (.beginPath ctx)
    (.arc ctx (+ x width (* -12 scale-x)) (+ y (* 26 scale-y)) (* 3 scale-x) 0 (* 2 js/Math.PI))
    (.fill ctx))
  (when (= direction :right)
    (.restore ctx)))

(defn draw-log-sprite
  "Draws a detailed log sprite with wood grain and end caps."
  [ctx x y width height color]
  (let [dark-color (darken-color color 0.2)
        light-color (lighten-color color 0.15)
        end-radius (/ height 2.2)
        scale-x (/ width 80)]

    ;; Main log body (rounded rectangle)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.roundRect ctx (+ x end-radius) y (- width (* end-radius 2)) height (/ height 4))
    (.fill ctx)

    ;; Left end cap (ellipse to show cut end)
    (set! (.-fillStyle ctx) light-color)
    (.beginPath ctx)
    (.ellipse ctx (+ x end-radius) (+ y (/ height 2)) end-radius (/ height 2.2) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Left end cap rings (tree rings)
    (set! (.-strokeStyle ctx) dark-color)
    (set! (.-lineWidth ctx) 1)
    (.beginPath ctx)
    (.ellipse ctx (+ x end-radius) (+ y (/ height 2)) (* end-radius 0.6) (* (/ height 2.2) 0.6) 0 0 (* 2 js/Math.PI))
    (.stroke ctx)
    (.beginPath ctx)
    (.ellipse ctx (+ x end-radius) (+ y (/ height 2)) (* end-radius 0.3) (* (/ height 2.2) 0.3) 0 0 (* 2 js/Math.PI))
    (.stroke ctx)

    ;; Right end cap
    (set! (.-fillStyle ctx) light-color)
    (.beginPath ctx)
    (.ellipse ctx (+ x width (- end-radius)) (+ y (/ height 2)) end-radius (/ height 2.2) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Right end cap rings
    (set! (.-strokeStyle ctx) dark-color)
    (.beginPath ctx)
    (.ellipse ctx (+ x width (- end-radius)) (+ y (/ height 2)) (* end-radius 0.6) (* (/ height 2.2) 0.6) 0 0 (* 2 js/Math.PI))
    (.stroke ctx)
    (.beginPath ctx)
    (.ellipse ctx (+ x width (- end-radius)) (+ y (/ height 2)) (* end-radius 0.3) (* (/ height 2.2) 0.3) 0 0 (* 2 js/Math.PI))
    (.stroke ctx)

    ;; Wood grain lines on body
    (set! (.-strokeStyle ctx) dark-color)
    (set! (.-lineWidth ctx) 1.5)
    (doseq [i (range 3 (- width 20) 20)]
      (when (and (> i (* end-radius 1.5)) (< i (- width (* end-radius 1.5))))
        (.beginPath ctx)
        (.moveTo ctx (+ x i) (+ y 3))
        (.quadraticCurveTo ctx (+ x i 3) (+ y (/ height 2)) (+ x i) (+ y height -3))
        (.stroke ctx)))

    ;; Bark texture highlights
    (set! (.-strokeStyle ctx) light-color)
    (set! (.-lineWidth ctx) 2)
    (.beginPath ctx)
    (.moveTo ctx (+ x (* end-radius 1.5)) (+ y 4))
    (.lineTo ctx (+ x width (* end-radius -1.5)) (+ y 4))
    (.stroke ctx)))

(defn draw-lily-pad
  "Draws a detailed lily pad sprite with veins and notch."
  [ctx x y width height color]
  (let [cx (+ x (/ width 2))
        cy (+ y (/ height 2))
        radius-x (/ width 2.2)
        radius-y (/ height 2.2)
        dark-color (darken-color color 0.2)
        light-color (lighten-color color 0.25)]

    ;; Main pad (ellipse)
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (.ellipse ctx cx cy radius-x radius-y 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Cut out notch (pie slice from center to edge)
    (set! (.-fillStyle ctx) "#1565C0") ;; Water color behind
    (.beginPath ctx)
    (.moveTo ctx cx cy)
    (.lineTo ctx (+ cx radius-x) (- cy (* radius-y 0.15)))
    (.lineTo ctx (+ cx radius-x) (+ cy (* radius-y 0.15)))
    (.closePath ctx)
    (.fill ctx)

    ;; Lighter center area
    (set! (.-fillStyle ctx) light-color)
    (.beginPath ctx)
    (.ellipse ctx cx cy (* radius-x 0.3) (* radius-y 0.3) 0 0 (* 2 js/Math.PI))
    (.fill ctx)

    ;; Vein lines radiating from center
    (set! (.-strokeStyle ctx) dark-color)
    (set! (.-lineWidth ctx) 1)
    (doseq [angle [0.5 1.0 1.5 2.5 3.0 3.5 4.0 4.5 5.0 5.5]]
      (let [end-x (+ cx (* radius-x 0.85 (js/Math.cos angle)))
            end-y (+ cy (* radius-y 0.85 (js/Math.sin angle)))]
        (.beginPath ctx)
        (.moveTo ctx cx cy)
        (.quadraticCurveTo ctx
                           (+ cx (* radius-x 0.4 (js/Math.cos angle)))
                           (+ cy (* radius-y 0.4 (js/Math.sin angle)))
                           end-x end-y)
        (.stroke ctx)))

    ;; Outer edge highlight
    (set! (.-strokeStyle ctx) light-color)
    (set! (.-lineWidth ctx) 2)
    (.beginPath ctx)
    (.ellipse ctx cx cy (* radius-x 0.95) (* radius-y 0.95) 0 2.5 5.5)
    (.stroke ctx)))

(defn draw-target-sprite
  "Draws a target/bullseye checkpoint sprite with concentric rings."
  [ctx x y width height color reached?]
  (let [cx (+ x (/ width 2))
        cy (+ y (/ height 2))
        radius (/ (min width height) 2.2)
        ring-colors (if reached?
                      ;; Greyed out when reached
                      ["#666666" "#888888" "#666666" "#888888" "#AAAAAA"]
                      ;; Red/white target rings
                      ["#D32F2F" "#FFFFFF" "#D32F2F" "#FFFFFF" "#D32F2F"])]

    ;; Draw shadow/glow underneath
    (when (not reached?)
      (set! (.-shadowColor ctx) "#FF5722")
      (set! (.-shadowBlur ctx) 8))

    ;; Draw concentric rings from outside in
    (doseq [[idx ring-color] (map-indexed vector ring-colors)]
      (let [ring-radius (* radius (- 1 (* idx 0.18)))]
        (set! (.-fillStyle ctx) ring-color)
        (.beginPath ctx)
        (.arc ctx cx cy ring-radius 0 (* 2 js/Math.PI))
        (.fill ctx)))

    ;; Reset shadow
    (set! (.-shadowColor ctx) "transparent")
    (set! (.-shadowBlur ctx) 0)

    ;; Draw border ring
    (set! (.-strokeStyle ctx) (if reached? "#444444" "#B71C1C"))
    (set! (.-lineWidth ctx) 2)
    (.beginPath ctx)
    (.arc ctx cx cy radius 0 (* 2 js/Math.PI))
    (.stroke ctx)

    ;; Draw a checkmark if reached
    (when reached?
      (set! (.-strokeStyle ctx) "#4CAF50")
      (set! (.-lineWidth ctx) 3)
      (set! (.-lineCap ctx) "round")
      (.beginPath ctx)
      (.moveTo ctx (- cx (* radius 0.3)) cy)
      (.lineTo ctx (- cx (* radius 0.05)) (+ cy (* radius 0.25)))
      (.lineTo ctx (+ cx (* radius 0.35)) (- cy (* radius 0.2)))
      (.stroke ctx))))

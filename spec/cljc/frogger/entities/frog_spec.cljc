(ns frogger.entities.frog-spec
  (:require [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should should= should-be-nil should-not should-not-be-nil before]]
            [frogger.entities.frog :as frog]
            [frogger.protocols :as p]
            [frogger.state :as state]))

(describe "frogger.entities.frog"

  (context "create-frog"
    (it "creates a frog with default position"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22"})]
        (should= (- (/ state/canvas-width 2) (/ state/cell-size 2)) (:x f))
        (should= (- state/canvas-height state/cell-size) (:y f))))

    (it "creates a frog with specified position"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 100 :y 200})]
        (should= 100 (:x f))
        (should= 200 (:y f))))

    (it "creates a frog with correct dimensions"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22"})]
        (should= (- state/cell-size 4) (:width f))
        (should= (- state/cell-size 4) (:height f))))

    (it "creates a frog facing up by default"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22"})]
        (should= :up (:facing f))))

    (it "creates a frog with specified speed multiplier"
      (let [f (frog/create-frog {:character-id :river :color "#4169E1" :speed-multiplier 1.5})]
        (should= 1.5 (:speed-multiplier f))))

    (it "creates a frog with specified score multiplier"
      (let [f (frog/create-frog {:character-id :sun :color "#FFD700" :score-multiplier 1.5})]
        (should= 1.5 (:score-multiplier f)))))

  (context "Entity protocol"
    (it "returns position"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 100 :y 200})]
        (should= {:x 100 :y 200} (p/get-position f))))

    (it "returns bounds"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 100 :y 200})]
        (should= {:x 100 :y 200 :width 36 :height 36} (p/get-bounds f))))

    (it "returns type :frog"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22"})]
        (should= :frog (p/get-type f)))))

  (context "Movable protocol"
    (it "moves up"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y 200})
            moved (p/move f :up)]
        (should= 200 (int (:x moved)))
        (should= (- 200 state/cell-size) (int (:y moved)))
        (should= :up (:facing moved))))

    (it "moves down"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y 200})
            moved (p/move f :down)]
        (should= 200 (int (:x moved)))
        (should= (+ 200 state/cell-size) (int (:y moved)))
        (should= :down (:facing moved))))

    (it "moves left"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y 200})
            moved (p/move f :left)]
        (should= (- 200 state/cell-size) (int (:x moved)))
        (should= 200 (int (:y moved)))
        (should= :left (:facing moved))))

    (it "moves right"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y 200})
            moved (p/move f :right)]
        (should= (+ 200 state/cell-size) (int (:x moved)))
        (should= 200 (int (:y moved)))
        (should= :right (:facing moved))))

    (it "clamps position to left edge"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 10 :y 200})
            moved (p/move f :left)]
        (should= 0 (:x moved))))

    (it "clamps position to right edge"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x (- state/canvas-width 50) :y 200})
            moved (p/move f :right)]
        (should= (- state/canvas-width (:width moved)) (:x moved))))

    (it "clamps position to top edge"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y 10})
            moved (p/move f :up)]
        (should= 0 (:y moved))))

    (it "clamps position to bottom edge"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y (- state/canvas-height 10)})
            moved (p/move f :down)]
        (should= (- state/canvas-height (:height moved)) (:y moved))))

    (it "does not move while on cooldown"
      (let [f (-> (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y 200})
                  (p/move :up))
            moved (p/move f :right)]
        (should= (:x f) (:x moved))
        (should= (:y f) (:y moved))))

    (it "moves faster with speed multiplier"
      (let [f (frog/create-frog {:character-id :river :color "#4169E1" :x 200 :y 200 :speed-multiplier 1.5})
            moved (p/move f :up)]
        (should= (- 200 (* state/cell-size 1.5)) (:y moved)))))

  (context "Updateable protocol"
    (it "decreases hop cooldown over time"
      (let [f (-> (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y 200})
                  (p/move :up))
            updated (p/update-entity f 100)]
        (should= (- frog/default-hop-cooldown 100) (:hop-cooldown-remaining updated))))

    (it "hop cooldown does not go below zero"
      (let [f (-> (frog/create-frog {:character-id :forest :color "#228B22" :x 200 :y 200})
                  (p/move :up))
            updated (p/update-entity f 1000)]
        (should= 0 (:hop-cooldown-remaining updated))))

    (it "decreases invincibility over time"
      (let [f (-> (frog/create-frog {:character-id :forest :color "#228B22"})
                  (assoc :invincible? true :invincibility-remaining 500))
            updated (p/update-entity f 100)]
        (should= 400 (:invincibility-remaining updated)))))

  (context "Renderable protocol"
    (it "returns sprite key based on character and facing"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22"})]
        (should= :forest-up (p/get-sprite-key f))))

    (it "returns color"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22"})]
        (should= "#228B22" (p/get-color f)))))

  (context "reset-position"
    (it "resets frog to starting position"
      (let [f (-> (frog/create-frog {:character-id :forest :color "#228B22" :x 100 :y 100})
                  frog/reset-position)]
        (should= (- (/ state/canvas-width 2) (/ state/cell-size 2)) (:x f))
        (should= (- state/canvas-height state/cell-size) (:y f))
        (should= :up (:facing f)))))

  (context "apply-platform-velocity"
    (it "moves frog with platform"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 100 :y 200})
            moved (frog/apply-platform-velocity f 5)]
        (should= 105 (:x moved))))

    (it "clamps to left edge"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x 5 :y 200})
            moved (frog/apply-platform-velocity f -10)]
        (should= 0 (:x moved))))

    (it "clamps to right edge"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22" :x (- state/canvas-width 40) :y 200})
            moved (frog/apply-platform-velocity f 10)]
        (should= (- state/canvas-width (:width f)) (:x moved)))))

  (context "helper functions"
    (it "is-dead? returns dead state"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22"})]
        (should-not (frog/is-dead? f))
        (should (frog/is-dead? (assoc f :dead? true)))))

    (it "reached-goal? returns reached state"
      (let [f (frog/create-frog {:character-id :forest :color "#228B22"})]
        (should-not (frog/reached-goal? f))
        (should (frog/reached-goal? (assoc f :reached-goal? true)))))

    (it "get-score-multiplier returns multiplier"
      (let [f (frog/create-frog {:character-id :sun :color "#FFD700" :score-multiplier 1.5})]
        (should= 1.5 (frog/get-score-multiplier f))))))

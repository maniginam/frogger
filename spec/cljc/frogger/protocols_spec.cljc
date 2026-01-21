(ns frogger.protocols-spec
  (:require [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should should= should-not-be-nil]]
            [frogger.protocols :as p]))

(describe "frogger.protocols"
  (context "Entity protocol"
    (it "should be defined"
      (should-not-be-nil p/Entity))

    (it "should have get-position method"
      (should-not-be-nil (:get-position (:sigs p/Entity))))

    (it "should have get-bounds method"
      (should-not-be-nil (:get-bounds (:sigs p/Entity))))

    (it "should have get-type method"
      (should-not-be-nil (:get-type (:sigs p/Entity)))))

  (context "Movable protocol"
    (it "should be defined"
      (should-not-be-nil p/Movable))

    (it "should have move method"
      (should-not-be-nil (:move (:sigs p/Movable))))

    (it "should have get-velocity method"
      (should-not-be-nil (:get-velocity (:sigs p/Movable)))))

  (context "Collidable protocol"
    (it "should be defined"
      (should-not-be-nil p/Collidable))

    (it "should have collides? method"
      (should-not-be-nil (:collides? (:sigs p/Collidable))))

    (it "should have on-collision method"
      (should-not-be-nil (:on-collision (:sigs p/Collidable)))))

  (context "Ability protocol"
    (it "should be defined"
      (should-not-be-nil p/Ability))

    (it "should have can-activate? method"
      (should-not-be-nil (:can-activate? (:sigs p/Ability))))

    (it "should have activate method"
      (should-not-be-nil (:activate (:sigs p/Ability))))

    (it "should have get-ability-state method"
      (should-not-be-nil (:get-ability-state (:sigs p/Ability)))))

  (context "Renderable protocol"
    (it "should be defined"
      (should-not-be-nil p/Renderable))

    (it "should have get-sprite-key method"
      (should-not-be-nil (:get-sprite-key (:sigs p/Renderable))))

    (it "should have get-color method"
      (should-not-be-nil (:get-color (:sigs p/Renderable)))))

  (context "Updateable protocol"
    (it "should be defined"
      (should-not-be-nil p/Updateable))

    (it "should have update-entity method"
      (should-not-be-nil (:update-entity (:sigs p/Updateable))))))

(ns frogger.protocols-spec
  (:require [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should should= should-not-be-nil]]
            [frogger.protocols :as p]))

(describe "frogger.protocols"
  (context "Entity protocol"
    (it "should be defined"
      (should-not-be-nil p/Entity))

    (it "should have get-position method"
      (should (fn? p/get-position)))

    (it "should have get-bounds method"
      (should (fn? p/get-bounds)))

    (it "should have get-type method"
      (should (fn? p/get-type))))

  (context "Movable protocol"
    (it "should be defined"
      (should-not-be-nil p/Movable))

    (it "should have move method"
      (should (fn? p/move)))

    (it "should have get-velocity method"
      (should (fn? p/get-velocity))))

  (context "Collidable protocol"
    (it "should be defined"
      (should-not-be-nil p/Collidable))

    (it "should have collides? method"
      (should (fn? p/collides?)))

    (it "should have on-collision method"
      (should (fn? p/on-collision))))

  (context "Ability protocol"
    (it "should be defined"
      (should-not-be-nil p/Ability))

    (it "should have can-activate? method"
      (should (fn? p/can-activate?)))

    (it "should have activate method"
      (should (fn? p/activate)))

    (it "should have get-ability-state method"
      (should (fn? p/get-ability-state))))

  (context "Renderable protocol"
    (it "should be defined"
      (should-not-be-nil p/Renderable))

    (it "should have get-sprite-key method"
      (should (fn? p/get-sprite-key)))

    (it "should have get-color method"
      (should (fn? p/get-color))))

  (context "Updateable protocol"
    (it "should be defined"
      (should-not-be-nil p/Updateable))

    (it "should have update-entity method"
      (should (fn? p/update-entity)))))

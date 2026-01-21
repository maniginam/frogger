(ns frogger.physics.collision-spec
  (:require [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should should= should-not should-be-nil]]
            [frogger.physics.collision :as collision]))

(describe "frogger.physics.collision"

  (context "aabb-intersects?"
    (it "returns true when boxes overlap"
      (should (collision/aabb-intersects?
               {:x 0 :y 0 :width 10 :height 10}
               {:x 5 :y 5 :width 10 :height 10})))

    (it "returns false when boxes don't overlap"
      (should-not (collision/aabb-intersects?
                   {:x 0 :y 0 :width 10 :height 10}
                   {:x 20 :y 20 :width 10 :height 10})))

    (it "returns false when boxes touch but don't overlap"
      (should-not (collision/aabb-intersects?
                   {:x 0 :y 0 :width 10 :height 10}
                   {:x 10 :y 0 :width 10 :height 10})))

    (it "returns true when one box contains another"
      (should (collision/aabb-intersects?
               {:x 0 :y 0 :width 100 :height 100}
               {:x 25 :y 25 :width 10 :height 10}))))

  (context "collides?"
    (it "detects collision between two entities"
      (let [a {:x 0 :y 0 :width 20 :height 20}
            b {:x 10 :y 10 :width 20 :height 20}]
        (should (collision/collides? a b))))

    (it "returns false for non-colliding entities"
      (let [a {:x 0 :y 0 :width 10 :height 10}
            b {:x 100 :y 100 :width 10 :height 10}]
        (should-not (collision/collides? a b)))))

  (context "find-collisions"
    (it "finds all colliding entities"
      (let [target {:x 50 :y 50 :width 20 :height 20}
            entities [{:x 0 :y 0 :width 10 :height 10}
                      {:x 45 :y 45 :width 20 :height 20}
                      {:x 60 :y 60 :width 20 :height 20}
                      {:x 200 :y 200 :width 10 :height 10}]
            collisions (collision/find-collisions target entities)]
        (should= 2 (count collisions))))

    (it "returns empty when no collisions"
      (let [target {:x 50 :y 50 :width 10 :height 10}
            entities [{:x 0 :y 0 :width 10 :height 10}
                      {:x 200 :y 200 :width 10 :height 10}]
            collisions (collision/find-collisions target entities)]
        (should= 0 (count collisions)))))

  (context "any-collision?"
    (it "returns true when at least one collision exists"
      (let [target {:x 50 :y 50 :width 20 :height 20}
            entities [{:x 0 :y 0 :width 10 :height 10}
                      {:x 55 :y 55 :width 10 :height 10}]]
        (should (collision/any-collision? target entities))))

    (it "returns false when no collisions"
      (let [target {:x 50 :y 50 :width 10 :height 10}
            entities [{:x 0 :y 0 :width 10 :height 10}]]
        (should-not (collision/any-collision? target entities)))))

  (context "check-obstacle-collisions"
    (it "returns :hit when frog hits obstacle"
      (let [frog {:x 50 :y 50 :width 20 :height 20 :invincible? false}
            obstacles [{:x 55 :y 55 :width 30 :height 30}]]
        (should= :hit (collision/check-obstacle-collisions frog obstacles))))

    (it "returns nil when invincible frog hits obstacle"
      (let [frog {:x 50 :y 50 :width 20 :height 20 :invincible? true}
            obstacles [{:x 55 :y 55 :width 30 :height 30}]]
        (should-be-nil (collision/check-obstacle-collisions frog obstacles))))

    (it "returns nil when no collision"
      (let [frog {:x 50 :y 50 :width 20 :height 20 :invincible? false}
            obstacles [{:x 200 :y 200 :width 30 :height 30}]]
        (should-be-nil (collision/check-obstacle-collisions frog obstacles)))))

  (context "center-point"
    (it "calculates center point of entity"
      (let [entity {:x 0 :y 0 :width 20 :height 40}
            center (collision/center-point entity)]
        (should= 10 (:px center))
        (should= 20 (:py center))))))

(ns frogger.test-runner
  (:require [speclj.core]
            [speclj.run.standard :as runner]
            [frogger.protocols-spec]
            [frogger.physics.collision-spec]
            [frogger.state-spec]
            [frogger.characters.registry-spec]
            [frogger.entities.frog-spec]))

(defn ^:export init []
  (runner/run-specs))

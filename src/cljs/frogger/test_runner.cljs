(ns frogger.test-runner
  (:require [speclj.core]
            [speclj.config :as config]
            [speclj.run.standard :as runner]
            [speclj.report.progress :as progress]
            [frogger.protocols-spec]
            [frogger.physics.collision-spec]
            [frogger.state-spec]
            [frogger.characters.registry-spec]
            [frogger.entities.frog-spec]))

(defn ^:export init []
  (set! runner/armed true)
  (runner/run-specs))

(defn run-all []
  (set! runner/armed true)
  (runner/run-specs))

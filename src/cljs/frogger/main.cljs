(ns frogger.main
  "Application entry point."
  (:require [frogger.app :as app]))

(defn ^:export init []
  (js/console.log "Frogger game initializing...")
  (app/init-app!)
  (js/console.log "Frogger game ready!"))

(defn ^:dev/before-load on-reload-start []
  (js/console.log "Reloading..."))

(defn ^:dev/after-load on-reload-complete []
  (js/console.log "Reloaded!"))

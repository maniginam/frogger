(ns frogger.ui.menu
  "Character and theme selection menu."
  (:require [frogger.characters.registry :as characters]
            [frogger.themes.registry :as themes]))

(declare update-character-selection! update-theme-selection!)

(defonce selected-character (atom :forest))
(defonce selected-theme (atom :forest))
(defonce start-callback (atom nil))

(defn create-character-option [character]
  (let [{:keys [id name color description]} character
        div (.createElement js/document "div")]
    (set! (.-className div) "selection-item")
    (set! (.-dataset.id div) (clj->js id))
    (set! (.-innerHTML div)
          (str "<div class='preview' style='background-color: " color "'></div>"
               "<div class='name'>" name "</div>"))
    (.addEventListener div "click"
                       (fn [_]
                         (reset! selected-character id)
                         (update-character-selection!)))
    div))

(defn create-theme-option [theme]
  (let [{:keys [id name colors]} theme
        div (.createElement js/document "div")
        bg-color (get colors :background "#333")]
    (set! (.-className div) "selection-item")
    (set! (.-dataset.id div) (clj->js id))
    (set! (.-innerHTML div)
          (str "<div class='preview' style='background-color: " bg-color "'></div>"
               "<div class='name'>" name "</div>"))
    (.addEventListener div "click"
                       (fn [_]
                         (reset! selected-theme id)
                         (update-theme-selection!)))
    div))

(defn update-character-selection! []
  (let [items (.querySelectorAll js/document "#character-select .selection-item")]
    (.forEach items
              (fn [item]
                (let [item-id (keyword (.-id (.-dataset item)))]
                  (if (= item-id @selected-character)
                    (.add (.-classList item) "selected")
                    (.remove (.-classList item) "selected")))))))

(defn update-theme-selection! []
  (let [items (.querySelectorAll js/document "#theme-select .selection-item")]
    (.forEach items
              (fn [item]
                (let [item-id (keyword (.-id (.-dataset item)))]
                  (if (= item-id @selected-theme)
                    (.add (.-classList item) "selected")
                    (.remove (.-classList item) "selected")))))))

(defn render-character-select! []
  (when-let [container (.getElementById js/document "character-select")]
    (set! (.-innerHTML container) "<h3>Select Your Frog</h3>")
    (let [grid (.createElement js/document "div")]
      (set! (.-className grid) "selection-grid")
      (doseq [character (characters/get-all-characters)]
        (.appendChild grid (create-character-option character)))
      (.appendChild container grid))
    (update-character-selection!)))

(defn render-theme-select! []
  (when-let [container (.getElementById js/document "theme-select")]
    (set! (.-innerHTML container) "<h3>Select Theme</h3>")
    (let [grid (.createElement js/document "div")]
      (set! (.-className grid) "selection-grid")
      (doseq [theme (themes/get-all-themes)]
        (.appendChild grid (create-theme-option theme)))
      (.appendChild container grid))
    (update-theme-selection!)))

(defn setup-start-button! [callback]
  (reset! start-callback callback)
  (when-let [btn (.getElementById js/document "start-btn")]
    (.addEventListener btn "click"
                       (fn [_]
                         (when @start-callback
                           (@start-callback @selected-character @selected-theme))))))

(defn show-menu! []
  (when-let [menu (.getElementById js/document "menu-container")]
    (.remove (.-classList menu) "hidden"))
  (when-let [canvas (.getElementById js/document "game-canvas")]
    (.add (.-classList canvas) "hidden"))
  (when-let [hud (.getElementById js/document "hud")]
    (.add (.-classList hud) "hidden")))

(defn hide-menu! []
  (when-let [menu (.getElementById js/document "menu-container")]
    (.add (.-classList menu) "hidden"))
  (when-let [canvas (.getElementById js/document "game-canvas")]
    (.remove (.-classList canvas) "hidden"))
  (when-let [hud (.getElementById js/document "hud")]
    (.remove (.-classList hud) "hidden")))

(defn init-menu! [start-callback-fn]
  (render-character-select!)
  (render-theme-select!)
  (setup-start-button! start-callback-fn))

(defn get-selections []
  {:character-id @selected-character
   :theme-id @selected-theme})

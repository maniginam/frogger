(ns frogger.themes.registry
  "Registry of background themes with visual configurations.")

(def themes
  {:forest {:id :forest
            :name "Forest"
            :category :nature
            :colors {:background "#2D5016"
                     :road "#404040"
                     :road-lines "#FFFFFF"
                     :water "#1565C0"
                     :safe-zone "#4A7023"
                     :goal-area "#1B5E20"}
            :obstacles {:car "#D32F2F"
                        :truck "#1976D2"
                        :bus "#FFA000"}
            :platforms {:log "#6D4C41"
                        :turtle "#388E3C"
                        :lily-pad "#66BB6A"}}

   :pond {:id :pond
          :name "Pond"
          :category :nature
          :colors {:background "#1A472A"
                   :road "#5D4037"
                   :road-lines "#8D6E63"
                   :water "#0D47A1"
                   :safe-zone "#2E7D32"
                   :goal-area "#004D40"}
          :obstacles {:car "#6D4C41"
                      :truck "#4E342E"
                      :bus "#3E2723"}
          :platforms {:log "#5D4037"
                      :turtle "#2E7D32"
                      :lily-pad "#4CAF50"}}

   :city {:id :city
          :name "City Streets"
          :category :urban
          :colors {:background "#263238"
                   :road "#37474F"
                   :road-lines "#FFD54F"
                   :water "#0277BD"
                   :safe-zone "#455A64"
                   :goal-area "#1B5E20"}
          :obstacles {:car "#F44336"
                      :truck "#2196F3"
                      :bus "#FF9800"
                      :motorcycle "#9C27B0"
                      :race-car "#4CAF50"}
          :platforms {:log "#6D4C41"
                      :turtle "#388E3C"
                      :lily-pad "#81C784"}}

   :highway {:id :highway
             :name "Highway"
             :category :urban
             :colors {:background "#1A1A1A"
                      :road "#212121"
                      :road-lines "#FFEB3B"
                      :water "#01579B"
                      :safe-zone "#424242"
                      :goal-area "#1B5E20"}
             :obstacles {:car "#E53935"
                         :truck "#1E88E5"
                         :bus "#FDD835"
                         :race-car "#00E676"}
             :platforms {:log "#5D4037"
                         :turtle "#43A047"
                         :lily-pad "#66BB6A"}}

   :magical {:id :magical
             :name "Magical Forest"
             :category :fantasy
             :colors {:background "#1A237E"
                      :road "#311B92"
                      :road-lines "#E040FB"
                      :water "#7C4DFF"
                      :safe-zone "#4527A0"
                      :goal-area "#AA00FF"}
             :obstacles {:car "#FF4081"
                         :truck "#536DFE"
                         :bus "#FFAB40"}
             :platforms {:log "#7E57C2"
                         :turtle "#00BFA5"
                         :lily-pad "#69F0AE"}}

   :candy {:id :candy
           :name "Candy Land"
           :category :fantasy
           :colors {:background "#FCE4EC"
                    :road "#F8BBD9"
                    :road-lines "#FFFFFF"
                    :water "#81D4FA"
                    :safe-zone "#F48FB1"
                    :goal-area "#EC407A"}
           :obstacles {:car "#E91E63"
                       :truck "#9C27B0"
                       :bus "#FF9800"}
           :platforms {:log "#8D6E63"
                       :turtle "#4DB6AC"
                       :lily-pad "#AED581"}}

   :space {:id :space
           :name "Space"
           :category :fantasy
           :colors {:background "#000000"
                    :road "#1A237E"
                    :road-lines "#448AFF"
                    :water "#311B92"
                    :safe-zone "#0D47A1"
                    :goal-area "#00BCD4"}
           :obstacles {:car "#F44336"
                       :truck "#3F51B5"
                       :bus "#FFEB3B"}
           :platforms {:log "#455A64"
                       :turtle "#00BFA5"
                       :lily-pad "#69F0AE"}
           :special {:stars true :nebula true}}

   :underwater {:id :underwater
                :name "Underwater"
                :category :fantasy
                :colors {:background "#006064"
                         :road "#004D40"
                         :road-lines "#80DEEA"
                         :water "#00838F"
                         :safe-zone "#00695C"
                         :goal-area "#00BCD4"}
                :obstacles {:car "#FF5722"
                            :truck "#FF7043"
                            :bus "#FFAB40"}
                :platforms {:log "#5D4037"
                            :turtle "#26A69A"
                            :lily-pad "#4DB6AC"}}})

(defn get-theme
  "Returns theme data by id."
  [theme-id]
  (get themes theme-id))

(defn get-all-themes
  "Returns all themes as a sequence."
  []
  (vals themes))

(defn get-theme-ids
  "Returns all theme ids."
  []
  (keys themes))

(defn theme-exists?
  "Returns true if theme exists."
  [theme-id]
  (contains? themes theme-id))

(defn get-themes-by-category
  "Returns themes filtered by category."
  [category]
  (filter #(= category (:category %)) (vals themes)))

(defn get-color
  "Gets a specific color from a theme."
  [theme-id color-key]
  (get-in themes [theme-id :colors color-key]))

(defn get-obstacle-color
  "Gets obstacle color from a theme."
  [theme-id obstacle-type]
  (get-in themes [theme-id :obstacles obstacle-type]))

(defn get-platform-color
  "Gets platform color from a theme."
  [theme-id platform-type]
  (get-in themes [theme-id :platforms platform-type]))

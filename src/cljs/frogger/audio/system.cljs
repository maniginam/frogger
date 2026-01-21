(ns frogger.audio.system
  "Audio system using Web Audio API for music and sound effects.")

(declare stop-music!)

(defonce audio-context (atom nil))
(defonce master-gain (atom nil))
(defonce music-gain (atom nil))
(defonce sfx-gain (atom nil))
(defonce current-music (atom nil))
(defonce music-enabled? (atom true))
(defonce sfx-enabled? (atom true))

(defn init-audio!
  "Initialize the Web Audio API context."
  []
  (when-not @audio-context
    (let [ctx (js/AudioContext.)
          master (.createGain ctx)
          music (.createGain ctx)
          sfx (.createGain ctx)]
      ;; Connect audio graph
      (.connect music master)
      (.connect sfx master)
      (.connect master (.-destination ctx))
      ;; Set default volumes
      (set! (.-value (.-gain master)) 0.5)
      (set! (.-value (.-gain music)) 0.3)
      (set! (.-value (.-gain sfx)) 0.6)
      ;; Store references
      (reset! audio-context ctx)
      (reset! master-gain master)
      (reset! music-gain music)
      (reset! sfx-gain sfx))))

(defn resume-audio!
  "Resume audio context if suspended (required for user gesture)."
  []
  (when-let [ctx @audio-context]
    (when (= "suspended" (.-state ctx))
      (.resume ctx))))

;; Note frequencies for music
(def note-freqs
  {:C3 130.81 :D3 146.83 :E3 164.81 :F3 174.61 :G3 196.00 :A3 220.00 :B3 246.94
   :C4 261.63 :D4 293.66 :E4 329.63 :F4 349.23 :G4 392.00 :A4 440.00 :B4 493.88
   :C5 523.25 :D5 587.33 :E5 659.25 :F5 698.46 :G5 783.99 :A5 880.00 :B5 987.77
   :C6 1046.50
   ;; Sharps/flats
   :Cs4 277.18 :Ds4 311.13 :Fs4 369.99 :Gs4 415.30 :As4 466.16
   :Cs5 554.37 :Ds5 622.25 :Fs5 739.99 :Gs5 830.61 :As5 932.33})

(defn play-note
  "Play a single note with given parameters."
  [freq duration & {:keys [wave-type attack decay gain-val delay-time]
                    :or {wave-type "sine" attack 0.02 decay 0.1 gain-val 0.3 delay-time 0}}]
  (when-let [ctx @audio-context]
    (let [osc (.createOscillator ctx)
          gain (.createGain ctx)
          now (.-currentTime ctx)
          start-time (+ now delay-time)]
      (set! (.-type osc) wave-type)
      (set! (.-value (.-frequency osc)) freq)
      (.connect osc gain)
      (.connect gain @music-gain)
      ;; Envelope
      (set! (.-value (.-gain gain)) 0)
      (.setValueAtTime (.-gain gain) 0 start-time)
      (.linearRampToValueAtTime (.-gain gain) gain-val (+ start-time attack))
      (.linearRampToValueAtTime (.-gain gain) (* gain-val 0.7) (+ start-time attack decay))
      (.linearRampToValueAtTime (.-gain gain) 0 (+ start-time duration))
      ;; Play
      (.start osc start-time)
      (.stop osc (+ start-time duration 0.1)))))

;; Theme melodies - each returns a sequence of [note duration delay] tuples
(def theme-melodies
  {:forest
   ;; Cheerful forest melody - folk-like
   [[:E4 0.3] [:G4 0.3] [:A4 0.3] [:G4 0.3] [:E4 0.3] [:D4 0.3] [:E4 0.6]
    [:G4 0.3] [:A4 0.3] [:B4 0.3] [:A4 0.3] [:G4 0.3] [:E4 0.3] [:G4 0.6]]

   :pond
   ;; Peaceful pond melody - gentle and flowing
   [[:C4 0.4] [:E4 0.4] [:G4 0.4] [:E4 0.4] [:C4 0.4] [:D4 0.4] [:E4 0.8]
    [:G4 0.4] [:E4 0.4] [:C4 0.4] [:D4 0.4] [:E4 0.4] [:C4 0.8]]

   :city
   ;; Urban city melody - jazzy, upbeat
   [[:C4 0.2] [:E4 0.2] [:G4 0.2] [:As4 0.4] [:G4 0.2] [:E4 0.2] [:C4 0.4]
    [:D4 0.2] [:F4 0.2] [:A4 0.2] [:C5 0.4] [:A4 0.2] [:F4 0.2] [:D4 0.4]]

   :highway
   ;; Fast-paced highway melody - driving rhythm
   [[:E4 0.15] [:E4 0.15] [:G4 0.3] [:E4 0.15] [:E4 0.15] [:A4 0.3]
    [:G4 0.15] [:G4 0.15] [:B4 0.3] [:A4 0.15] [:G4 0.15] [:E4 0.3]]

   :magical
   ;; Mystical melody - ethereal and dreamy
   [[:E4 0.5] [:Gs4 0.5] [:B4 0.5] [:E5 0.5] [:Ds5 0.5] [:B4 0.5] [:Gs4 0.5] [:E4 0.5]
    [:Fs4 0.5] [:A4 0.5] [:Cs5 0.5] [:E5 0.5] [:Cs5 0.5] [:A4 0.5] [:Fs4 0.5] [:E4 0.5]]

   :candy
   ;; Sweet candy melody - playful and bouncy
   [[:C5 0.2] [:E5 0.2] [:G5 0.2] [:E5 0.2] [:C5 0.2] [:G4 0.2] [:E4 0.4]
    [:D5 0.2] [:F5 0.2] [:A5 0.2] [:F5 0.2] [:D5 0.2] [:A4 0.2] [:F4 0.4]]

   :space
   ;; Space melody - sci-fi, atmospheric
   [[:C4 0.6] [:E4 0.6] [:G4 0.6] [:C5 0.6] [:B4 0.3] [:G4 0.3] [:E4 0.6]
    [:D4 0.6] [:Fs4 0.6] [:A4 0.6] [:D5 0.6] [:Cs5 0.3] [:A4 0.3] [:Fs4 0.6]]

   :underwater
   ;; Underwater melody - flowing and bubbly
   [[:G3 0.5] [:C4 0.5] [:E4 0.5] [:G4 0.5] [:E4 0.3] [:C4 0.3] [:G3 0.4]
    [:A3 0.5] [:D4 0.5] [:F4 0.5] [:A4 0.5] [:F4 0.3] [:D4 0.3] [:A3 0.4]]})

(def theme-wave-types
  {:forest "triangle"
   :pond "sine"
   :city "square"
   :highway "sawtooth"
   :magical "sine"
   :candy "triangle"
   :space "sine"
   :underwater "sine"})

(defn play-theme-melody
  "Play the melody for a theme once."
  [theme-id]
  (when (and @audio-context @music-enabled?)
    (let [melody (get theme-melodies theme-id (:forest theme-melodies))
          wave-type (get theme-wave-types theme-id "sine")]
      (loop [notes melody
             time 0]
        (when (seq notes)
          (let [[note duration] (first notes)
                freq (get note-freqs note 440)]
            (play-note freq duration
                       :wave-type wave-type
                       :delay-time time
                       :gain-val 0.25)
            (recur (rest notes) (+ time duration))))))))

(defn start-theme-music!
  "Start looping theme music for a world."
  [theme-id]
  (stop-music!)
  (when (and @audio-context @music-enabled?)
    (let [melody (get theme-melodies theme-id (:forest theme-melodies))
          total-duration (reduce + (map second melody))
          loop-interval (* total-duration 1000)]
      ;; Play immediately
      (play-theme-melody theme-id)
      ;; Schedule loop
      (reset! current-music
              (js/setInterval #(play-theme-melody theme-id) loop-interval)))))

(defn stop-music!
  "Stop the current music."
  []
  (when @current-music
    (js/clearInterval @current-music)
    (reset! current-music nil)))

;; Frog sounds based on character temperament
(def frog-sounds
  {:forest    {:hop [392 0.08] :jump [440 0.1] :land [330 0.06] :death [220 0.3]}   ;; Balanced - normal pitch
   :river     {:hop [523 0.05] :jump [587 0.07] :land [440 0.04] :death [294 0.25]} ;; Speedy - higher, quicker
   :desert    {:hop [294 0.12] :jump [330 0.15] :land [247 0.1] :death [165 0.4]}   ;; Tank - lower, deeper
   :magic     {:hop [659 0.1] :jump [784 0.12] :land [523 0.08] :death [392 0.35]}  ;; Mystical - high, ethereal
   :sun       {:hop [440 0.08] :jump [494 0.1] :land [370 0.06] :death [247 0.3]}   ;; Lucky - bright, cheerful
   :shadow    {:hop [196 0.15] :jump [220 0.18] :land [165 0.12] :death [110 0.5]}  ;; Stealthy - very low, long
   })

(defn play-frog-sound
  "Play a frog sound effect for a given character and action."
  [character-id action]
  (when (and @audio-context @sfx-enabled?)
    (let [sounds (get frog-sounds character-id (:forest frog-sounds))
          [freq duration] (get sounds action [400 0.1])
          ctx @audio-context
          osc (.createOscillator ctx)
          gain (.createGain ctx)
          now (.-currentTime ctx)]
      ;; Different wave types for different characters
      (set! (.-type osc)
            (case character-id
              :shadow "sine"
              :magic "sine"
              :sun "triangle"
              :river "triangle"
              :desert "square"
              "triangle"))
      (set! (.-value (.-frequency osc)) freq)
      (.connect osc gain)
      (.connect gain @sfx-gain)
      ;; Quick envelope for sound effects
      (set! (.-value (.-gain gain)) 0.4)
      (.setValueAtTime (.-gain gain) 0.4 now)
      ;; Pitch slide for jump/hop
      (when (#{:hop :jump} action)
        (.exponentialRampToValueAtTime (.-frequency osc) (* freq 1.3) (+ now (* duration 0.5))))
      ;; Pitch drop for death
      (when (= action :death)
        (.exponentialRampToValueAtTime (.-frequency osc) (* freq 0.5) (+ now duration)))
      (.linearRampToValueAtTime (.-gain gain) 0 (+ now duration))
      (.start osc now)
      (.stop osc (+ now duration 0.1)))))

(defn play-hop-sound
  "Play hop sound for the current character."
  [character-id]
  (play-frog-sound character-id :hop))

(defn play-jump-sound
  "Play jump sound (bigger hop)."
  [character-id]
  (play-frog-sound character-id :jump))

(defn play-land-sound
  "Play landing sound."
  [character-id]
  (play-frog-sound character-id :land))

(defn play-death-sound
  "Play death/squash sound."
  [character-id]
  (play-frog-sound character-id :death))

(defn play-goal-sound
  "Play goal reached sound."
  []
  (when (and @audio-context @sfx-enabled?)
    (let [ctx @audio-context]
      ;; Victory arpeggio
      (doseq [[note delay] [[:C5 0] [:E5 0.08] [:G5 0.16] [:C6 0.24]]]
        (play-note (get note-freqs note) 0.3
                   :wave-type "triangle"
                   :delay-time delay
                   :gain-val 0.4)))))

(defn play-level-complete-sound
  "Play level complete fanfare."
  []
  (when (and @audio-context @sfx-enabled?)
    (doseq [[note delay] [[:C4 0] [:E4 0.1] [:G4 0.2] [:C5 0.3]
                          [:G4 0.5] [:C5 0.6] [:E5 0.7] [:G5 0.8]]]
      (play-note (get note-freqs note) 0.4
                 :wave-type "triangle"
                 :delay-time delay
                 :gain-val 0.35))))

(defn play-game-over-sound
  "Play game over sound."
  []
  (when (and @audio-context @sfx-enabled?)
    (doseq [[note delay] [[:E4 0] [:D4 0.3] [:C4 0.6] [:B3 0.9]]]
      (play-note (get note-freqs note) 0.5
                 :wave-type "sine"
                 :delay-time delay
                 :gain-val 0.3))))

(defn toggle-music!
  "Toggle music on/off."
  []
  (swap! music-enabled? not)
  (when-not @music-enabled?
    (stop-music!)))

(defn toggle-sfx!
  "Toggle sound effects on/off."
  []
  (swap! sfx-enabled? not))

(defn set-master-volume!
  "Set master volume (0.0 to 1.0)."
  [vol]
  (when @master-gain
    (set! (.-value (.-gain @master-gain)) vol)))

(defn set-music-volume!
  "Set music volume (0.0 to 1.0)."
  [vol]
  (when @music-gain
    (set! (.-value (.-gain @music-gain)) vol)))

(defn set-sfx-volume!
  "Set sound effects volume (0.0 to 1.0)."
  [vol]
  (when @sfx-gain
    (set! (.-value (.-gain @sfx-gain)) vol)))

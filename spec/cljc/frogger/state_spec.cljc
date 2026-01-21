(ns frogger.state-spec
  (:require [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should should= should-not]]
            [frogger.state :as state]))

(describe "frogger.state"

  (context "make-initial-state"
    (it "creates state with default values"
      (let [s (state/make-initial-state {})]
        (should= :menu (:screen s))
        (should= :forest (:character-id s))
        (should= :forest (:theme-id s))
        (should= 0 (:score s))
        (should= 3 (:lives s))
        (should= 1 (:level s))
        (should-not (:paused? s))))

    (it "accepts custom character and theme"
      (let [s (state/make-initial-state {:character-id :river :theme-id :city})]
        (should= :river (:character-id s))
        (should= :city (:theme-id s)))))

  (context "game-over?"
    (it "returns true when no lives"
      (let [s (assoc (state/make-initial-state {}) :lives 0)]
        (should (state/game-over? s))))

    (it "returns false when lives remain"
      (let [s (state/make-initial-state {})]
        (should-not (state/game-over? s)))))

  (context "level-complete?"
    (it "returns true when all goals reached"
      (let [s (assoc (state/make-initial-state {})
                     :goals [{:reached? true} {:reached? true}])]
        (should (state/level-complete? s))))

    (it "returns false when some goals not reached"
      (let [s (assoc (state/make-initial-state {})
                     :goals [{:reached? true} {:reached? false}])]
        (should-not (state/level-complete? s)))))

  (context "add-score"
    (it "adds points to score"
      (let [s (state/make-initial-state {})
            s2 (state/add-score s 100)]
        (should= 100 (:score s2))))

    (it "accumulates score"
      (let [s (-> (state/make-initial-state {})
                  (state/add-score 50)
                  (state/add-score 75))]
        (should= 125 (:score s)))))

  (context "lose-life"
    (it "decrements lives by one"
      (let [s (state/make-initial-state {})
            s2 (state/lose-life s)]
        (should= 2 (:lives s2)))))

  (context "set-screen"
    (it "changes screen state"
      (let [s (state/make-initial-state {})
            s2 (state/set-screen s :playing)]
        (should= :playing (:screen s2)))))

  (context "next-level"
    (it "increments level"
      (let [s (state/make-initial-state {})
            s2 (state/next-level s)]
        (should= 2 (:level s2))))

    (it "resets time"
      (let [s (assoc (state/make-initial-state {}) :time-remaining 1000)
            s2 (state/next-level s)]
        (should= 60000 (:time-remaining s2)))))

  (context "set-input and clear-input"
    (it "sets input state"
      (let [s (state/make-initial-state {})
            input {:direction :up :ability-pressed? true}
            s2 (state/set-input s input)]
        (should= :up (get-in s2 [:input :direction]))
        (should (get-in s2 [:input :ability-pressed?]))))

    (it "clears input state"
      (let [s (state/set-input (state/make-initial-state {})
                               {:direction :up :ability-pressed? true})
            s2 (state/clear-input s)]
        (should= nil (get-in s2 [:input :direction]))
        (should-not (get-in s2 [:input :ability-pressed?]))))))

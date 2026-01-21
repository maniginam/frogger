(ns frogger.characters.registry-spec
  (:require [speclj.core #?(:clj :refer :cljs :refer-macros) [describe context it should should= should-not should-not-be-nil should-be-nil]]
            [frogger.characters.registry :as registry]))

(describe "frogger.characters.registry"

  (context "characters map"
    (it "contains 6 characters"
      (should= 6 (count registry/characters)))

    (it "contains forest frog"
      (should-not-be-nil (:forest registry/characters)))

    (it "contains river frog"
      (should-not-be-nil (:river registry/characters)))

    (it "contains desert frog"
      (should-not-be-nil (:desert registry/characters)))

    (it "contains magic frog"
      (should-not-be-nil (:magic registry/characters)))

    (it "contains sun frog"
      (should-not-be-nil (:sun registry/characters)))

    (it "contains shadow frog"
      (should-not-be-nil (:shadow registry/characters))))

  (context "get-character"
    (it "returns character by id"
      (let [forest (registry/get-character :forest)]
        (should= :forest (:id forest))
        (should= "Forest Frog" (:name forest))
        (should= "#228B22" (:color forest))))

    (it "returns nil for unknown character"
      (should-be-nil (registry/get-character :unknown))))

  (context "get-all-characters"
    (it "returns all characters"
      (should= 6 (count (registry/get-all-characters)))))

  (context "get-character-ids"
    (it "returns all character ids"
      (let [ids (set (registry/get-character-ids))]
        (should (contains? ids :forest))
        (should (contains? ids :river))
        (should (contains? ids :desert))
        (should (contains? ids :magic))
        (should (contains? ids :sun))
        (should (contains? ids :shadow)))))

  (context "character-exists?"
    (it "returns true for existing character"
      (should (registry/character-exists? :forest)))

    (it "returns false for non-existing character"
      (should-not (registry/character-exists? :unknown))))

  (context "character attributes"
    (it "forest frog has balanced temperament"
      (let [c (registry/get-character :forest)]
        (should= :balanced (:temperament c))
        (should= 1.0 (:speed-multiplier c))
        (should= 1.0 (:score-multiplier c))))

    (it "river frog has speedy temperament"
      (let [c (registry/get-character :river)]
        (should= :speedy (:temperament c))
        (should= 1.5 (:speed-multiplier c))))

    (it "sun frog has lucky temperament with score bonus"
      (let [c (registry/get-character :sun)]
        (should= :lucky (:temperament c))
        (should= 1.5 (:score-multiplier c))))

    (it "magic frog has double jump ability"
      (let [c (registry/get-character :magic)]
        (should= :mystical (:temperament c))
        (should= :double-jump (:ability-type c))))))

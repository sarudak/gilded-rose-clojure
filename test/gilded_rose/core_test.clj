(ns gilded-rose.core-test
  (:use midje.sweet)
  (:require [gilded-rose.core :refer [item update-quality]]))

(fact "Given an unexpired ordinary item"
      (let [inventory [(item "Normal thing" 10 15)]
            result (update-quality inventory)
            updated-item (first result)]
        (fact "The sell-in date is decremented"
              (:sell-in updated-item) => 9)
        (fact "The quality is decremented by 1"
              (:quality updated-item) => 14)))

(fact "Given an expired ordinary item"
      (let [inventory [(item "Normal thing" -1 15)]
            result (update-quality inventory)
            updated-item (first result)]
        (fact "The sell-in date is decremented"
              (:sell-in updated-item) => -2)
        (fact "The quality is decremented by 2"
              (:quality updated-item) => 13)))

(fact "Given an expired ordinary item of no quality"
      (let [inventory [(item "Normal thing" -1 0)]
            result (update-quality inventory)
            updated-item (first result)]
        (fact "The sell-in date is decremented"
              (:sell-in updated-item) => -2)
        (fact "The quality is still 0"
              (:quality updated-item) => 0)))

(fact "Given an Aged brie item"
        (let [
              normal (item "Aged Brie" 15 10)
              max-quality (item "Aged Brie" 15 50)
              inventory [normal max-quality]
              results (update-quality inventory)]
          (fact "The normal item"
                (let [normal-result (first results)]
                  (fact "The sell-in date is decremented"
                        (:sell-in normal-result) => 14)
                  (fact "The quality increases"
                        (:quality normal-result) => 11)
                  )
                )
          (fact "The item at max quality"
                (let [max-quality-result (nth results 1)]
                  (fact "The sell-in date is decremented"
                        (:sell-in max-quality-result) => 14)
                  (fact "The quality does not increase"
                        (:quality max-quality-result) => 50))
                )))


(fact "Given an Aged brie item at max quality"
      (let [inventory [(item "Aged Brie" 10 50)]
            result (update-quality inventory)
            updated-item (first result)]
        (fact "the quality does not increase"
              (:quality updated-item) => 50)))

(fact "Given Sulfuras"
      (let [inventory [(item "Sulfuras, Hand of Ragnaros" 10 15)]
            result (update-quality inventory)
            updated-item (first result)]
        (fact "nothing changes"
              result => inventory
              (:quality updated-item) => 15
              )))
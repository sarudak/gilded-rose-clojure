(ns gilded-rose.core-test
  (:use midje.sweet)
  (:require [gilded-rose.core :refer [item update-quality]]))

(defn update-item [item]
  (first (update-quality [item])))

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

(def brie (partial item "Aged Brie"))
(def update-brie (comp update-item brie))

(fact "Given aged brie"
      (fact "In the standard case quality increases"
            (update-brie 12 30) => (brie 11 31)
            (update-brie 5 1) => (brie 4 2))
      (fact "When expired quality increases twice as fast"
            (update-brie 0 32) => (brie -1 34)
            (update-brie -3 3) => (brie -4 5))
      (fact "When close to max quality it never goes over max quality"
            (update-brie 14 50) => (brie 13 50)
            (update-brie 14 49) => (brie 13 50)
            (update-brie 0 50) => (brie -1 50)
            (update-brie 0 49) => (brie -1 50)
            (update-brie 0 48) => (brie -1 50)
            (update-brie 0 47) => (brie -1 49)
            ))

(def ticket (partial item "Backstage passes to a TAFKAL80ETC concert"))
(fact "Given backstage passes to a youtube band"
      (fact "After the concert tickets are worthless"
            (update-item (ticket 0 10))=> (ticket -1 0))
      (fact "Prior to 10 days out tickets value increases"
            (update-item (ticket 12 10))=> (ticket 11 11))
      (fact "Prior to 5 days out tickets value increases two-fold"
            (update-item (ticket 7 10))=> (ticket 6 12))
      (fact "Inside five days out tickets value increases three-fold"
            (update-item (ticket 2 10))=> (ticket 1 13)))

(fact "Given Sulfuras"
      (let [inventory [(item "Sulfuras, Hand of Ragnaros" 10 15)]
            result (update-quality inventory)
            updated-item (first result)]
        (fact "nothing changes"
              result => inventory
              (:quality updated-item) => 15
              )))
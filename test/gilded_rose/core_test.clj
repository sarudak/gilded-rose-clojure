(ns gilded-rose.core-test
  (:use midje.sweet)
  (:require [gilded-rose.core :refer [item update-quality]]))

(defn update-item [item]
  (first (update-quality [item])))

(def ordinary-item (partial item "Normal thing"))
(def update-ordinary-item (comp update-item ordinary-item))

(fact "Given an ordinary item"
      (fact "When unexpired, the quality decrements by 1"
            (update-ordinary-item 10 15) => (ordinary-item 9 14)
            (update-ordinary-item 1 15) => (ordinary-item 0 14)
            (fact "but never goes below 0"
                  (update-ordinary-item 3 0) => (ordinary-item 2 0)))
      (fact "When expired, the quality decrements by 2"
            (update-ordinary-item 0 15) => (ordinary-item -1 13)
            (update-ordinary-item -1 15) => (ordinary-item -2 13)
            (fact "but never goes below 0"
                  (update-ordinary-item -1 1) => (ordinary-item -2 0)
                  (update-ordinary-item -1 0) => (ordinary-item -2 0))))


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
(def update-ticket (comp update-item ticket))
(fact "Given backstage passes to a youtube band"
      (fact "After the concert tickets are worthless"
            (update-ticket 0 10)=> (ticket -1 0)
            (update-ticket -1 10)=> (ticket -2 0))
      (fact "Prior to 10 days out tickets value increases"
            (update-ticket 12 10)=> (ticket 11 11)
            (update-ticket 11 10)=> (ticket 10 11)
            (fact "But values cannot increase over 50"
                  (update-ticket 12 50)=> (ticket 11 50)))
      (fact "Prior to 5 days out tickets value increases two-fold"
            (update-ticket 10 10)=> (ticket 9 12)
            (update-ticket 7 10)=> (ticket 6 12)
            (update-ticket 6 10)=> (ticket 5 12)
            (fact "But values cannot increase over 50"
                  (update-ticket 7 50)=> (ticket 6 50)
                  (update-ticket 7 49)=> (ticket 6 50)))
      (fact "Inside five days out tickets value increases three-fold"
            (update-ticket 5 10)=> (ticket 4 13)
            (update-ticket 2 10)=> (ticket 1 13)
            (update-ticket 1 10)=> (ticket 0 13)
            (fact "But values cannot increase over 50"
                  (update-ticket 2 50)=> (ticket 1 50)
                  (update-ticket 2 49)=> (ticket 1 50)
                  (update-ticket 2 48)=> (ticket 1 50))) )

(fact "Given Sulfuras"
      (let [inventory [(item "Sulfuras, Hand of Ragnaros" 10 75)]
            result (update-quality inventory)
            updated-item (first result)]
        (fact "nothing changes"
              result => inventory
              (:quality updated-item) => 75
              )))



(fact "Given a collection of items"
      (let [inventory
            [(item "+5 Dexterity Vest" 10 20)
             (item "Aged Brie" 2 0)
             (item "Elixir of the Mongoose" 5 7)
             (item "Sulfuras, Hand of Ragnaros" 0 80)
             (item "Backstage passes to a TAFKAL80ETC concert" 15 20)]
            result (update-quality inventory)
            get-item (fn [item-name] (first (filter #(= (:name %) item-name) result)))]
            (fact "The number of items is the same"
                  (count result) => (count inventory))
            (fact "Normal items decreased normally"
                  (get-item "+5 Dexterity Vest") => (item "+5 Dexterity Vest" 9 19)
                  (get-item "Elixir of the Mongoose") => (item "Elixir of the Mongoose" 4 6))
            (fact "Sulfuras is not changed"
                  (get-item "Sulfuras, Hand of Ragnaros") => (item "Sulfuras, Hand of Ragnaros" 0 80))
            (fact "Aged Brie increased normally"
                  (get-item "Aged Brie") => (brie 1 1))
            (fact "Tickets increased according to their rules"
                  (get-item "Backstage passes to a TAFKAL80ETC concert") => (ticket 14 21)))
        )
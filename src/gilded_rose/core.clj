(ns gilded-rose.core)

(def sulfuras "Sulfuras, Hand of Ragnaros")
(def backstage-pass "Backstage passes to a TAFKAL80ETC concert")


(defn Sulfuras? [item] (= sulfuras (:name item)))
(defn backstage-pass? [item] (= backstage-pass (:name item)))

(defn update-sellin-date [item]
  (if (Sulfuras? item)
    item
    (update-in item [:sell-in] dec))
  )

(defn expired? [item] (> 0 (:sell-in item)))

(defn inner-update-quality [item]  (cond
            (Sulfuras? item) item
            (backstage-pass? item)
              (if (expired? item)
                (merge item {:quality 0})
                (if (and  (>= (:sell-in item) 5) (< (:sell-in item) 10))
                  (merge item {:quality (inc (inc (:quality item)))})
                  (if (and  (>= (:sell-in item) 0) (< (:sell-in item) 5))
                    (merge item {:quality (inc (inc (inc (:quality item))))})
                    (merge item {:quality (inc (:quality item))})))
                )
            (= (:name item) "Aged Brie")
              (if (expired? item)
                (update-in item [:quality] (comp inc inc))
                (merge item {:quality (inc (:quality item))}))
            (expired? item)
              (merge item {:quality (- (:quality item) 2)})
            :else
              (merge item {:quality (dec (:quality item))})))

(defn adhere-to-quality-bounds [item]
  (if (Sulfuras? item)
    item
    (update-in item [:quality] (fn [x] (min 50 (max 0 x)))))
  )

(defn update-quality [items]
  (map (comp adhere-to-quality-bounds inner-update-quality)
       (map update-sellin-date items)
    ))

(defn item [item-name, sell-in, quality]
  {:name item-name, :sell-in sell-in, :quality quality})

(defn update-current-inventory[]
  (let [inventory 
    [
      (item "+5 Dexterity Vest" 10 20)
      (item "Aged Brie" 2 0)
      (item "Elixir of the Mongoose" 5 7)
      (item "Sulfuras, Hand of Ragnaros" 0 80)
      (item "Backstage passes to a TAFKAL80ETC concert" 15 20)
    ]]
    (update-quality inventory)
    ))

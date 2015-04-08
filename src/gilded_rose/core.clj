(ns gilded-rose.core)

(def sulfuras "Sulfuras, Hand of Ragnaros")
(def backstage-pass "Backstage passes to a TAFKAL80ETC concert")


(defn sulfuras? [item] (= sulfuras (:name item)))
(defn backstage-pass? [item] (= backstage-pass (:name item)))
(defn aged-brie? [item] (= "Aged Brie" (:name item)))

(defn update-sellin-date [item]
  (if (sulfuras? item)
    item
    (update-in item [:sell-in] dec))
  )

(defn expired? [item] (> 0 (:sell-in item)))

(defn sell-in-within-range? [item min max] (and (>= (:sell-in item) min) (< (:sell-in item) max)))

(defn update-item-quality [item]
  (let [quality-increases-by (fn [amount] (update-in item [:quality] (partial + amount)))
        quality-decreases-by (fn [amount] (update-in item [:quality] (fn [x] (- x amount ))))
        quality-becomes-zero (fn [] (assoc item :quality 0))]
    (cond
      (sulfuras? item) item
      (backstage-pass? item)
        (cond
          (expired? item)
            (quality-becomes-zero)
          (sell-in-within-range? item 0 5)
            (quality-increases-by 3)
          (sell-in-within-range? item 5 10)
            (quality-increases-by 2)
          :else
            (quality-increases-by 1))
      (aged-brie? item)
        (if (expired? item)
          (quality-increases-by 2)
          (quality-increases-by 1))
      :else
        (if (expired? item)
          (quality-decreases-by 2)
          (quality-decreases-by 1)))))

(defn adhere-to-quality-bounds [item]
  (if (sulfuras? item)
    item
    (update-in item [:quality] (fn [x] (min 50 (max 0 x)))))
  )

(defn update-quality [items]
  (map (comp adhere-to-quality-bounds
             update-item-quality
             update-sellin-date)
       items))

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

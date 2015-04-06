(ns gilded-rose.core)

(def item-types {"Backstage passes to a TAFKAL80ETC concert" :ticket
                 "Aged Brie" :brie
                 "Sulfuras, Hand of Ragnaros" :sulfuras})

(defn expired? [{sell-in :sell-in}] (< sell-in 0))
(def increases-by-1 inc)
(def increases-by-2 (comp inc inc))
(def increases-by-3 (comp inc inc inc))
(def decreases-by-1 dec)
(def decreases-by-2 (comp dec dec))
(def becomes-0 (fn [_] 0))

(defn quality-of [item modifier] (update-in item [:quality] modifier))

(defn get-item-type [item] (item-types (:name item)))
(defmulti update-item-quality get-item-type)

(defmethod update-item-quality :brie [item]
  (if (expired? item)
    (quality-of item increases-by-2)
    (quality-of item increases-by-1)))

(defmethod update-item-quality :ticket [{sell-in :sell-in :as item}]
  (quality-of item (cond (>= sell-in 10) increases-by-1
                         (>= sell-in 5) increases-by-2
                         (>= sell-in 0) increases-by-3
                         :else becomes-0)))

(defmethod update-item-quality :default [item]
  (if (expired? item)
    (quality-of item decreases-by-2)
    (quality-of item decreases-by-1)))

(defn update-sellin [item]
  (update-in item [:sell-in] decreases-by-1))

(defn ensure-quality-bounds [item]
  (update-in item [:quality] #(max 0 (min % 50))))

(defmulti update-item get-item-type)
(defmethod update-item :sulfuras [item] item)
(defmethod update-item :default [item]
  (-> item
      update-sellin
      update-item-quality
      ensure-quality-bounds))

(defn update-quality [items]
  (map update-item items))

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

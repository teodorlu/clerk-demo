(ns scramble-json-data
  (:require [clojure.walk :refer [prewalk]]
            [cheshire.core :as json]))

;; I'm running this _outside of Clerk_ to get a scrambled JSON blob I can share.
;;
;; You should be able to ignore this namespace when reproducing. I did _not_ use
;; Clerk when running this code.


(def original-path (str "/home/teodorlu/tmp/temp-2022-11-02/cmx" "/" "c16f4f.json"))
(def scrambled-path "datasets/c16f4f.scrambled.json")

(defn load-original []
  (-> original-path
      slurp
      (json/parse-string keyword)))

(def ^:private alphabet "abcdefghijklmnopqrstuvwxyz")

(defn randomize-string [s]
  (apply str (repeatedly (count s)
                         #(rand-nth alphabet))))

(defn randomize-keyword [k]
  (keyword (randomize-string (name k))))

(defn randomize-integer [_n]
  (rand-int 9999))

(defn randomize-bool [_b]
  (< 0.5 (rand)))

(defn randomize-double [_d]
  (rand))

(defn scramble [data]
  (let [scramble-one (fn [one]
                       (cond
                         (map? one) one ;; leave maps be
                         (map-entry? one) one ;; ditto
                         (string? one) (randomize-string one)
                         (vector? one) one ;; leave it
                         (nil? one) one
                         ;; (seq? one)     one                     ;; we don't want to touch collections
                         (keyword? one) (randomize-keyword one) ;; a random keyword of the same length
                         (integer? one) (randomize-integer one) ;; just a random int
                         (boolean? one) (randomize-bool one)    ;; just a random bool
                         (double? one) (randomize-double one)   ;; you get the jist.
                         :else (throw (ex-info "type not supported!" {:type (type one)}))))]
    (prewalk scramble-one data)))

(comment
  (do
    (scramble (load-original))
    nil)

  (spit scrambled-path (json/generate-string (scramble (load-original))))




  )

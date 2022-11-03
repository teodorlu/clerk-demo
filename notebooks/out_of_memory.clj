(ns out-of-memory
  (:require [cheshire.core :as json]
            [nextjournal.clerk :as clerk]))

;; in this notebook, I'll try to reproduce the out of memory error.

(def scrambled-path "datasets/c16f4f.scrambled.json")

^{:nextjournal.clerk/visibility {:result :hide}}
(def scrambled-data
  (-> scrambled-path
      slurp
      (json/parse-string keyword)))

;; now, M-x clerk-show
;;
;; ... waiting for about 10 seconds, browser still showing old page
;;
;; ... still waiting
;;
;; ... out of memory.

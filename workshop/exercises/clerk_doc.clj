;; # Idea: a `clojure.repl/doc` for Clerk

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns exercises.clerk-doc
  (:require [nextjournal.clerk :as clerk]
            [clojure.string :as str]))

;; Yesterday, I asked Martin if there was an idiomatic `clojure.repl/doc` for
;; Clerk. I want to explore using Clerk for teaching Clojure, and I don't want
;; to hop into the REPL. The REPL is great, but teaching the REPL takes time.
;;
;; His answer was somewhere along "this should be possible to solve in
;; userspace".
;; That got me interested.
;;
;; One of the surprising "Clojure truths" that I find myself agreeing more and
;; more with is to provide _less but orthogonal stuff_. Don't try to anticipate
;; everything anyone could want to do. Rather, give them good building
;; materials. Just look at all the stuff that's _not_ in `clojure.core`.

;; ## Answering "how can I use this thing?" with ease
;;
;; ... and not using any other tools than Clerk.
;;
;; We expect "show me the docs for this thing under my cursor" from any decent
;; Clojure editor. In my Doom Emacs config, I press `K` to get that. But that
;; relies on an LSP and/or a REPL. A "Clerk only" solution would be neat.
;;
;; "what can `clojure.core/map` do?"
;;
;; Let's dig in.
;;
;; Clojure vars have metadata.

(keys (meta (var map)))

;; Out of those, I suspect we want `:doc`, and possibly `arglists`.
;; I don't know what those are, though.
;;
;; Good thing we're using an interactive system :)

(:doc (meta (var map)))

(:arglists (meta (var map)))

;; ## Interlude --- some comments on Clerk
;;
;; Thoughts:
;;
;; 1. Not having to call `clojure.pprint/pprint` by hand and get a good default viewer is great.
;; 2. Not having those results disappear into the REPL history is 🤗
;; 3. Being able to collapse / open / expand the viewer and dig into it is 🤗🤗
;; 4. The code / text interplay works great already.
;;    I do miss Org-mode outlining, but I think I'd probably prefer Clerk's strong Clojure support for most "literate Clojure examples".
;;    That remains to be seen!
;;
;; But:
;;
;; 1. As I write this, I can't scroll past the end of the document.
;;    Each time I save and re-evaluate, I need to move my cursor over to the browser window and scrooll
;;    1. But I can just solve this myself with some empty lines!
;;       Scroll down if you're curious.
;; 2. I find the spacing between items in lists to be a bit large for my taste.
;;    I like being able to write compact outlines.
;;    I think Pandoc's default spacing is okay (example: https://play.teod.eu/open-problems/#initial-rambling)
;; 3. When I paste a raw link, like `https://play.teod.eu/open-problems/#initial-rambling` / https://play.teod.eu/open-problems/#initial-rambling,
;;    it seems to strip out the `#` from the HTML.
;;    Is this a bug?
;; 4. I really appreciate Pandoc's `--smart` that translates `---` into —, and "quoted" into “quoted”.
;;
;; Questions:
;;
;; 1. I use lots of `cat stuff | pandoc --from org+smart --to json | ./my-babashka-thing transform-into-what-i-want | pandoc --from json --to html --standalone` for my own work.
;;    I suspect I want some kind of `clerk-cli --to-pandoc-json my-doc.clj | ...`.
;;    1. Is this possible today?
;;    2. Thoughts?
;;    3. Clerk's default HTML view is great, but I'd really like to be able to use Pandoc's reach to enable other things.
;;
;; But 2:
;;
;; 1. Okay, I'm already missing Org-mode's outlining.
;;    But I can live with that.
;;    I could also consider creating multiple smaller document sections with Clerk, and merging them / embedding them together with Pandoc.
;;    1. Note --- this is _not_ a deal breaker for me.
;;
;; Enough commentary for now.

;; ## A shot at `doc`.

^{:nextjournal.clerk/visibility {:result :hide}}
(defn lines [& ls]
  (str/join "\n" ls))

^{:nextjournal.clerk/visibility {:result :hide}}
(defn md-bold [s] (str "**" s "**"))

^{:nextjournal.clerk/visibility {:result :hide}}
(defn doc* [v]
  (clerk/md (lines (md-bold (symbol v))
                   ""
                   (pr-str (:arglists (meta v)))
                   ""
                   (:doc (meta v)))))

;; Let's see it in action.

(doc* #'map)

(doc* #'list)


;; My simple doc:

(defn doc** [v]
  (clerk/html [:div
               [:p [:strong (symbol v)]]
               [:p (pr-str (:arglists (meta v)))]
               [:p (:doc (meta v))]]))

(doc** #'map)


(defn doc***
  "Adapted from https://github.com/nextjournal/clerk/blob/e617f081792c32dccd51d1cebb294d08fefa2132/notebooks/doc.clj#L41-L53"
  [v]
  (let [{:keys [ns doc name arglists]} (meta v)]
    (clerk/html
     [:div
      [:br]
      [:strong (str ns "/" name)]
      (when (seq arglists)
        [:div.pt-4
         (clerk/code (str/join "\n" (map (fn [args] (pr-str (concat [name] args)))
                                         arglists)))])
      (when doc
        [:div.mt-4.viewer-markdown.prose
         (clerk/md doc)])])))

(keys (meta #'map))

(-> #'map
    meta
    :ns
    str)

(:ns (meta #'map))

(doc*** #'map)

(defmacro doc [form]
  `(doc*** (var ~form)))

(doc map)

(doc list)

(clerk/html [:hr])

;; Not the prettiest thing in the world.
;; But enough for me for now!

;; ## Future work
;;
;; Lots of things I want to explore.
;; But enough computer time for now.
;;
;; Ideas:
;;
;; 1. I'd like to take control over spacing.
;;    Write some functions to help me insert horizontal rules and blank lines.
;; 2. Understand better how to show and hide things.
;;    Right now, I'm using lots of metadata.
;;    I think "just data" is possible too.
;;
;; ☀️

;; ## some blank lines please.
;;
;; (accidentally got a code block - I can probably just generate some HTML.
;; but I'm lazy today, that's a problem for future Teodor)

(clerk/md (str/join "\n" ["```"
                          (str/join "\n"
                                    (repeat 2 "\n"))
                          "```"] ))

;; Hmm.
;; How about hiccup?

(clerk/html (into [:div]
                  (repeat 10 [:br])))

;; that's better :)

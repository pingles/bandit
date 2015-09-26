(ns bandit.context
  (:require [clojure.core.matrix :as m]
            [clojure.core.matrix.protocols :as mp]
            [clojure.core.matrix.operators :as mo]
            [bandit.arms :as arms])
  (:import [java.util Collections]))

;; Experimental contextual bandit for search queries
;; Use bag-of-words and create feature vector from a search
(defn tokenise [s]
  (clojure.string/split s #"\s+"))

(defn add-word
  [dictionary word]
  (assoc dictionary word (count dictionary)))

(defn initialise-dictionary
  [words]
  (reduce add-word {} (sort words)))

(def dictionary (initialise-dictionary (tokenise "these are all the words we could search for")))

(defn- dictionary-pos
  "Returns the position of the word in the dictionary"
  [dictionary s]
  (get dictionary s))

(defn feature-vector
  ([dictionary]
     (m/new-vector (count dictionary)))
  ([dictionary wordseq]
     (let [v (m/new-vector (count dictionary))]
       (if-let [indices (filter (comp not nil?)
                                (map (partial dictionary-pos dictionary) wordseq))]
         (reduce (fn [v i] (mp/set-1d v i 1.0)) v indices)
         v))))

(defn arm
  "Creates a contextual arm, uses a feature vector to store pulls and rewards"
  [dictionary name]
  (arms/arm name
            :pulls (feature-vector dictionary)
            :value (feature-vector dictionary)))

(defn- positions
  "v is a vector of words in the dictionary"
  [dictionary v]
  (map (partial dictionary-pos dictionary) v))

(defn pulled
  "contextvec is the context vector. e.g. (feature-vector dictionary (tokenise \"testing this\"))"
  [{:keys [pulls] :as arm} contextvec]
  (assoc arm :pulls (m/add pulls contextvec)))

(defn reward
  "contextvec is the context vector. e.g. (feature-vector dictionary (tokenise \"testing this\"))"
  [{:keys [value] :as arm} contextvec]
  (assoc arm :value (m/add value contextvec)))

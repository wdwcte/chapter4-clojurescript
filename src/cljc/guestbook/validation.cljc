(ns guestbook.validation
  (:require
   [struct.core :as st]))

(def message-schema
  [[:name st/required st/string]
   [:message st/required st/string
    {:message "message must contain at least 10 characters"
     :validate (fn [msg] (>= (count msg) 10))}]])

(defn validate-message [params]
  ;; guestbook.routes.home> (validate-message {:name 1 :message "foo"})
  ;; {:name "must be a string",
  ;; :message "message must contain at least 10 characters"}
  (first (st/validate params message-schema)))

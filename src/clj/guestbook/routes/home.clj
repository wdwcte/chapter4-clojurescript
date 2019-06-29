(ns guestbook.routes.home
  (:require
    [guestbook.layout :as layout]
    [guestbook.db.core :as db]
    [clojure.java.io :as io]
    [guestbook.middleware :as middleware]
    [ring.util.http-response :as response]
    [struct.core :as st]))

(defn home-page [{:keys [flash] :as request}]
  (layout/render
   request
   "home.html"
   (merge
    {:messages (db/get-messages)}
    (select-keys flash [:name :message :errors]))))

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

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (response/bad-request {:errors errors})
    (try
      (db/save-message! params)
      (response/ok {:status :ok})
      (catch Exception e
        (response/internal-server-error
         {:errors {:server-error ["Failed to save message!"]}})))))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/message" {:post save-message!}]
   ["/about" {:get about-page}]])

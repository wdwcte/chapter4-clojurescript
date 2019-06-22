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

(defn save-message! [{:keys [params]}] ;; `params` is {:message "foo" :name 1}
  (if-let [errors (validate-message params)] ;; `errors`: {:name "must be a string",:message "message must contain at least 10 characters"}
    (-> (response/found "/") ;; {:status 302, :headers {"Location" "/"}, :body ""}
        (assoc :flash (assoc params :errors errors))) ;;  {:name 1, :message "foo", :errors {:name "must be a string", :message "message must contain at least 10 characters"}}}

    (do
      (db/save-message! params)
      (response/found "/"))))

(defn about-page [request]
  (layout/render request "about.html"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/message" {:post save-message!}]
   ["/about" {:get about-page}]])

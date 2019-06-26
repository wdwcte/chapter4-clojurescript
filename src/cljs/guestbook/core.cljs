(ns guestbook.core
  (:require [reagent.core :as r]))

(r/render
 [:h1 "Hello, Reagent"]
 (.getElementById js/document "content"))

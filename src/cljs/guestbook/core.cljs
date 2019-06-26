(ns guestbook.core
  (:require [reagent.core :as r]))

(r/render
 ;; equivalent to (> for nesting the <h1> tag)
 ;;     [:div#hello.content [:h1 "Hello, Auto!"]]
 ;;
 ;; which is equivalent to (# for the id, . for the class)
 ;;     [:div {:id "hello", :class "content"} [:h1 "Hello, Auto!"]]
 ;;
 ;; wich is turned into:
 ;;     <div id="hello" class="content"><h1>Hello, Auto!</h1></div>
 [:div#hello.content>h1 "Hello, Auto!"]
 (.getElementById js/document "content"))

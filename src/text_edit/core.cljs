(ns text-edit.core
	(:require [reagent.core :as r]
						[reagent.dom :as rdom]))

(def cursor-on? (r/atom true))
(def lx (r/atom 10))

(defn canvas []
	[:div.canvas-text-editor {:tabIndex 0}
	 [:canvas {:id "main-canvas" :width 1000}]])

(defn textedit []
	[:div
	 [canvas]])

(defn extract-canvas []
	(js/document.getElementById "main-canvas"))

(defn extract-ctxt [canvas] (.getContext canvas "2d"))

(defn draw-cursor! [ctxt] 
	(do 
		(. ctxt beginPath)

		(. ctxt moveTo 0 26)
		(. ctxt lineTo 0 50)
		(. ctxt lineTo 2 50)
		(. ctxt lineTo 2 26)
		(. ctxt lineTo 0 26)

		(. ctxt fill)))

(defn clear-cursor! [ctxt]
	(. ctxt clearRect 0 26 2 24))

(defn- blink! [ctxt] 
	(swap! 
		cursor-on? 
		#(do 
			 (if % (draw-cursor! ctxt) (clear-cursor! ctxt)) 
			 (not %))))

(defn blink-cursor! [ctxt]
	(. js/window setInterval #(blink! ctxt) 500))

(defn keystroke! [e ctxt] 
  (swap! lx 
	#(let [k (. e -key)]
    (. ctxt fillText k % 50)
    (+ % 10))))

(defn keydown! [ctxt]
	(. js/window addEventListener "keydown" #(keystroke! % ctxt)))

(rdom/render [textedit] (js/document.getElementById "app"))
(def ctxt (-> (extract-canvas) extract-ctxt))
(set! (. ctxt -font) "24px serif")

(blink-cursor! ctxt)
(keydown! ctxt)

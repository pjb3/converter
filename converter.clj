;Clojure Implementation of Keith Bennett's Fahrenheit/Celsius Converter Swing App

(ns converter
  (:import (java.awt BorderLayout Dimension Event GridLayout Toolkit)
	   (java.awt.event ActionEvent KeyEvent)
	   (javax.swing AbstractAction Action BorderFactory JButton JFrame JLabel JMenu JMenuBar JPanel JTextField KeyStroke)
	   (javax.swing.event DocumentEvent DocumentListener)))

(defn float-string? 
  "Returns true if the string can be converted to a float"
  [s]
  (try 
   (if (string? s) (do (Float/parseFloat s) true) false) 
   (catch NumberFormatException e false)))

(defn to-f
  "Converts the object to a float"
  [f]
  (if (nil? f) 0.0
      (if (string? f)
	(if (empty? (.trim f))
	  0.0
	  (Float/parseFloat f))
	f)))

(defn blank?
  "True if the string is null or contains only whitespace"
  [s]
  (or (nil? s) (empty? (.trim s))))

(defn f2c 
  "Converts degrees from Fahrenheit to Celsius.  Accepts a string or a number"
  [degrees]
    (/ (* (- (to-f degrees) 32.0) 5.0) 9.0))

(defn c2f
  "Converts degress from Celsius to Fahrenheit.  Accepts a string or a number"
  [degrees]
    (+ (/ (* (to-f degrees) 9.0) 5.0) 32.0))

(defn simple-document-listener
  "Returns an instance of DocumentListener that performs the function f on any change"
  [f]
  (proxy [DocumentListener] []
    (changedUpdate [event] (f event))
    (insertUpdate [event] (f event))
    (removeUpdate [event] (f event))))
   
(defn center-on-screen
  "Moves the given frame to the center of the screen"
  [frame]
  (let [s-size (.getScreenSize (Toolkit/getDefaultToolkit))
	f-size (.getSize frame)
	x (/ (- (.getWidth s-size) (.getWidth f-size)) 2)
	y (/ (- (.getWidth s-size) (.getWidth f-size)) 2)]
    (.setLocation frame (int x) (int y))))

(defn bring-to-front
  "Brings the frame to the front"
  [frame]
  (.setAlwaysOnTop frame true) 
  (.setAlwaysOnTop frame false))

(defn set-border-size
  "Set the border of the given frame to the given width"
  [frame w]
  (.setBorder
   (.getContentPane frame)
   (BorderFactory/createEmptyBorder w w w w))
  (.pack frame))

(defn converter
  "Kicks of a swing app that converters degrees between Farenheit and Celsius"
  []
  (let [frame (JFrame. "Fahrenheit <--> Celsius Converter")
	fahr-text-field (JTextField. 15)
	cels-text-field (JTextField. 15)
	f2c-action (doto (proxy [AbstractAction] ["Fahr -> Cels"]
			 (actionPerformed 
			  [event] 
			  (.setText 
			   cels-text-field 
			   (str (f2c (.getText fahr-text-field))))))
		       (.putValue Action/SHORT_DESCRIPTION "Convert from Fahrenheit to Celsius")
		       (.putValue Action/ACCELERATOR_KEY 
				 (KeyStroke/getKeyStroke KeyEvent/VK_S Event/CTRL_MASK))
		       (.setEnabled false))
	c2f-action (doto (proxy [AbstractAction] ["Cels -> Fahr"]
			 (actionPerformed
			  [event]
			  (.setText
			   fahr-text-field
			   (str (c2f (.getText cels-text-field))))))
		       (.putValue Action/SHORT_DESCRIPTION "Convert from Celsius to Fahrenheit")
		       (.putValue Action/ACCELERATOR_KEY
				 (KeyStroke/getKeyStroke KeyEvent/VK_T Event/CTRL_MASK))
		       (.setEnabled false))
	clear-action (doto (proxy [AbstractAction] ["Clear"]
			   (actionPerformed 
			    [event]
			    (.setText fahr-text-field "")
			    (.setText cels-text-field "")))
			   (.putValue Action/SHORT_DESCRIPTION "Clear the temperature text fields")
			   (.putValue Action/ACCELERATOR_KEY
				     (KeyStroke/getKeyStroke KeyEvent/VK_T Event/CTRL_MASK))
			   (.setEnabled false))
	exit-action (doto (proxy [AbstractAction] ["Exit"]
			  (actionPerformed
			   [event]
			   (.dispose frame)))
			(.putValue Action/SHORT_DESCRIPTION "Exit this program")
			(.putValue Action/ACCELERATOR_KEY
				  (KeyStroke/getKeyStroke KeyEvent/VK_X Event/CTRL_MASK)))
	setup-document-listeners (fn [] 
				   (.addDocumentListener (.getDocument fahr-text-field)
							 (simple-document-listener 
							  (fn [event] 
							    (.setEnabled 
							     f2c-action 
							     (float-string? 
							      (.getText fahr-text-field))))))
				   (.addDocumentListener (.getDocument cels-text-field)
							 (simple-document-listener
							  (fn [event]
							    (.setEnabled
							     c2f-action
							     (float-string?
							      (.getText cels-text-field))))))
				   (let [clear-document-listener
					 (simple-document-listener
					  (fn [event]
					    (.setEnabled
					     clear-action
					     (or (blank? (.getText cels-text-field))
						 (blank? (.getText fahr-text-field))))))]
				     (.addDocumentListener 
				      (.getDocument fahr-text-field)
				      clear-document-listener)
				     (.addDocumentListener
				      (.getDocument cels-text-field)
				      clear-document-listener)))
	create-converters-panel (fn [] 
				  (let [label-panel (JPanel. (GridLayout. 0 1 5 5))
					text-field-panel (JPanel. (GridLayout. 0 1 5 5))
					tooltip "Input a temperature"]
				    (doto label-panel 
					(.add (JLabel. "Fahrenheit:  "))
					(.add (JLabel. "Celsius:  ")))
				    (doto text-field-panel
					(.add fahr-text-field)
					(.add cels-text-field))
				    (.setToolTipText fahr-text-field tooltip)
				    (.setToolTipText cels-text-field tooltip)
				    (setup-document-listeners)
				    (doto (JPanel. (BorderLayout.))
					(.add label-panel BorderLayout/WEST)
					(.add text-field-panel BorderLayout/CENTER))))
	create-buttons-panel (fn [] 
			       (let [inner-panel (JPanel. (GridLayout. 1 0 5 5))]
				 (doto inner-panel
				     (.add (JButton. f2c-action))
				     (.add (JButton. c2f-action))
				     (.add (JButton. clear-action))
				     (.add (JButton. exit-action)))
				 (doto (JPanel. (BorderLayout.))
				     (.add inner-panel BorderLayout/EAST)
				     (.setBorder (BorderFactory/createEmptyBorder 10 0 0 0)))))
	create-menu-bar (fn [] (doto (JMenuBar.)
				 (.add (doto (JMenu. "File")
					 (.add exit-action)))
				 (.add (doto (JMenu. "Edit")
					 (.add clear-action)))
				 (.add (doto (JMenu. "Convert")
					 (.add f2c-action)
					 (.add c2f-action)))))]
    (doto frame
	(.add (create-converters-panel) BorderLayout/CENTER)
	(.add (create-buttons-panel) BorderLayout/SOUTH)
	(.setJMenuBar (create-menu-bar))
	(.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
	(set-border-size 12)
	(.setVisible true)
	(center-on-screen))))


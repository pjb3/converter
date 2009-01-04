package converter;
/**
 * Simplifies the DocumentListener interface by having all three
 * interface methods delegate to a single method.
 *
 * To use this abstract class, subclass it and implement the abstract
 * method handleDocumentEvent().
 *
 * Copyright, Bennett Business Solutions, Inc., 2008
 */
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public abstract class SimpleDocumentListener implements DocumentListener {


    /**
     * Implement this method when subclassing this class.
     * It will be called whenever a DocumentEvent occurs.
     */
    abstract public void handleDocumentEvent(DocumentEvent event);


    public void changedUpdate(DocumentEvent event) {
	handleDocumentEvent(event);
    }


    public void insertUpdate(DocumentEvent event) {
	handleDocumentEvent(event);
    }


    public void removeUpdate(DocumentEvent event) {
	handleDocumentEvent(event);
    }


}




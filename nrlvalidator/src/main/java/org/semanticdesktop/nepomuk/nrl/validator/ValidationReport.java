package org.semanticdesktop.nepomuk.nrl.validator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;
import org.semanticdesktop.nepomuk.nrl.validator.ValidationMessage.MessageType;

/**
 * Encapsulates a report about the validation status.
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class ValidationReport {
    
    private boolean valid = true;
    private List<ValidationMessage> messages;
    
    /**
     * The main constructor;
     */
    public ValidationReport() {
        valid = true;
        messages = new LinkedList<ValidationMessage>();
    }
    
    /**
     * Returns true if the getErrors list contains no errors. It can contain
     * warnings though.
     * @return true if the report indicates that the validation was successful
     *         (the getMessages list contains no errors), false otherwise
     */
    public boolean isValid() {
        return valid;
    }
    
    /**
     * Returns a list of messages from the validator. Note that the returned
     * list is unmodifiable. Use addMessage if you want to add an error message.
     * @return a list of messages from the validator.
     */
    public List<ValidationMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }
    
    /**
     * Adds a validation message to the list.
     * @param message a new validation message.
     */
    public void addMessage(ValidationMessage message) {
        if (message.getMessageType() != null
            && message.getMessageType().equals(
                ValidationMessage.MessageType.ERROR)) {
            valid = false;
        }
        messages.add(message);
    }
    
    /**
     * Adds a validation message to the list. 
     * @param type type of the message
     * @param title title of the message
     * @param msg body of the message
     * @param statements statements connected with the message
     */
    public void addMessage(MessageType type, String title, String msg, Statement... statements) {
        ValidationMessage message = new ValidationMessage(type,title,msg,statements);
        addMessage(message);
    }
}

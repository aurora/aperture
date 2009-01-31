package org.semanticdesktop.nepomuk.nrl.validator;

import java.util.LinkedList;
import java.util.List;

import org.ontoware.rdf2go.model.Statement;


/**
 * Encapsulates a validation message.
 * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
 */
public class ValidationMessage {
    
    private MessageType type;
    private String title;
    private String msg;
    private List<Statement> statements;
    
    /**
     * Type of the message.
     * @author <a href="mailto:antoni.mylka@dfki.de">Antoni Mylka</a>
     */
    public static enum MessageType {
        /** An error message */
        ERROR,
        /** A warning message */
        WARNING,
        /** An information message */
        INFO
    }
    
    /**
     * The main constructor;
     */
    public ValidationMessage() {
        statements = new LinkedList<Statement>();
    }
    
    /**
     * A full-blown constructor.
     * @param type type of the message
     * @param title title of the message
     * @param msg body of the message
     * @param statements RDF statements connected with this message.
     */
    public ValidationMessage(MessageType type, String title, String msg, Statement... statements) {
        this.statements = new LinkedList<Statement>();
        this.type = type;
        this.title = title;
        this.msg = msg;
        if (statements != null) {
            for (Statement statement : statements) {
                if (statement != null) {
                    this.statements.add(statement);
                }
            }
        }
    }
    
    /**
     * Returns the type of the message.
     * @return the type of the message.
     */
    public MessageType getMessageType() {
        return type;
    }
    
    /**
     * Returns the title of the message.
     * @return the name of an error.
     */
    public String getMessageTitle() {
        return title;
    }

    /**
     * Returns the actual message.
     * @return the description of an error.
     */
    public String getMessage() {
        return msg;
    }
    
    /**
     * Returns a list of RDF statements that are connected with this message.
     * @return a list of RDF statements that are connected with this message.
     */
    public List<Statement> getStatements() {
        return statements;
    }

    /**
     * Adds a new statement to the list of RDFStatements connected with this
     * message.
     * 
     * @param statement a new statement
     */
    public void addStatement(Statement statement) {
        statements.add(statement);
    }
    
    /**
     * Sets the message type.
     * @param type the new message type
     */
    public void setMessageType(MessageType type) {
        this.type = type;
    }
    
    /**
     * Sets the title of the message
     * @param title the title of the message
     */
    public void setMessageTitle(String title) {
        this.title = title;
    }
    
    /**
     * Sets the actual message.
     * @param message
     */
    public void setMessage(String message) {
        this.msg = message;
    }
}

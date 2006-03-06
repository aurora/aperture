/*
 * Copyright (c) 2005 -2006 Aduna and Deutsches Forschungszentrum für Künstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.examples.fileinspector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.sesame.repository.RStatement;
import org.openrdf.sesame.repository.Repository;
import org.openrdf.util.iterator.CloseableIterator;
import org.semanticdesktop.aperture.vocabulary.DATA;

public class MetadataModel {

    private String mimeType;

    private Repository repository;

    private String fullText;

    protected EventListenerList listenerList;

    private ChangeEvent changeEvent;

    public MetadataModel() {
        repository = null;
        fullText = null;
        listenerList = new EventListenerList();
        changeEvent = new ChangeEvent(this);
    }

    public void setMetadata(String mimeType, Repository repository) {
        // update the specified properties
        this.mimeType = mimeType;
        this.repository = repository;

        // update the derived full-text property
        if (repository == null) {
            fullText = null;
        }
        else {
            CloseableIterator statements = repository.getStatements(null, DATA.fullText, null);
            try {
                StringBuffer buffer = new StringBuffer(10000);
	            while (statements.hasNext()) {
	                RStatement statement = (RStatement) statements.next();
	                Value value = statement.getObject();
	                if (value instanceof Literal) {
	                    buffer.append(((Literal) value).getLabel());
	                }
	
	                if (statements.hasNext()) {
	                    buffer.append("\n\n=====================================================\n\n");
	                }
	            }

	            fullText = buffer.toString().trim();
            }
            finally {
            	statements.close();
            }
        }

        // notify listeners
        fireStateChanged();
    }

    public String getMimeType() {
        return mimeType;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getFullText() {
        return fullText;
    }

    public void addChangeListener(ChangeListener listener) {
        listenerList.add(ChangeListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listenerList.remove(ChangeListener.class, listener);
    }

    /**
     * Returns an array of all ChangeListeners registered on this MetadataModel.
     * 
     * @return all of this model's ChangeListeners or an empty array if no change listeners are currently
     *         registered.
     */
    public ChangeListener[] getChangeListeners() {
        return (ChangeListener[]) listenerList.getListeners(ChangeListener.class);
    }

    protected void fireStateChanged() {
        // guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();

        // process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
}

/*
 * Copyright (c) 2005 - 2007 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.accessor.base;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.util.ArrayMap;

/**
 * Simple AccessData implementation that holds all information in main memory. No persistency facilities are
 * provided, see FileAccessData for that.
 * 
 * @see FileAccessData
 */
public class AccessDataImpl implements AccessData {

    /**
     * A node in the aggregation relation, encapsulates links to the parent and to the children.
     */
    private static class AggregationNode {
        private String parent;
        private Set<String> children;
        public AggregationNode(String parent) {
            this.parent = parent;
            this.children = new HashSet<String>();
        }
    }
    
    // a key used to store the timestamp
    private static final String TIMESTAMP_KEY = "aperture.timestamp";
    
    /**
     * An identifier for the current crawl
     */
    private String crawlIdentifier;
    
	/**
	 * A Map mapping IDs to another Map that contains the key-value pairs for that ID.
	 */
	protected Map<String,Map<String,String>> idMap;

	/**
	 * A mapping from IDs to Sets of IDs that the former ID refers to. This can be used to model 
	 * links between IDs. The use of this mapping is typically to register IDs that need
	 * special treatment once the referring ID has been changed or removed.
	 */
	protected Map<String,Set<String>> referredIDMap;
	
	/**
	 * A mapping from IDs to Sets of IDs that the former ID aggregates. This can be used to model
	 * aggregation relationships and implement cascade touch and cascade delete functionality.
	 */
	protected Map<String,AggregationNode> aggregatedIDMap;

	/**
	 * @see AccessData#initialize()
	 */
	public void initialize() throws IOException {
		if (idMap == null) {
			idMap = new HashMap<String,Map<String,String>>(1024);
		}
		if (referredIDMap == null) {
			referredIDMap = new HashMap<String,Set<String>>(1024);
		}
		if (aggregatedIDMap == null) {
		    aggregatedIDMap = new HashMap<String,AggregationNode>(1024);
		}
		
		crawlIdentifier = UUID.randomUUID().toString();
	}

	/**
     * @see AccessData#store()
     */
	public void store() throws IOException {
	    // do nothing
	}

	/**
     * @see AccessData#clear()
     */
	public void clear() throws IOException {
		idMap = null;
		referredIDMap = null;
		aggregatedIDMap = null;
	}

	/**
     * @see AccessData#getSize()
     */
	public int getSize() {
		return getStoredIDs().size();
	}

	/**
     * @see AccessData#getStoredIDs()
     */
	public Set getStoredIDs() {
		Set<String> result = new HashSet<String>(idMap.keySet());
		result.addAll(referredIDMap.keySet());
		result.addAll(aggregatedIDMap.keySet());
		return result;
	}

	/**
     * @see AccessData#isKnownId(String)
     */
	public boolean isKnownId(String id) {
		return idMap.containsKey(id) || referredIDMap.containsKey(id) || aggregatedIDMap.containsKey(id);
	}

	/**
	 * @see AccessData#put(String, String, String)
	 */
	public void put(String id, String key, String value) {
	    // assumption: lots of objects with relative few things to store: use an ArrayMap
        ArrayMap infoMap = createInfoMap(id);
        infoMap.put(key, value);
	}

	/**
	 * @see AccessData#putReferredID(String, String)
	 */
	public void putReferredID(String id, String referredID) {
		Set<String> ids = referredIDMap.get(id);

		if (ids == null) {
			ids = new HashSet<String>();
			referredIDMap.put(id, ids);
		}

		ids.add(referredID);
	}

	/**
	 * @see AccessData#get(String, String)
	 */
	public String get(String id, String key) {
		ArrayMap infoMap = (ArrayMap) idMap.get(id);
		if (infoMap == null) {
			return null;
		}
		else {
			return (String) infoMap.get(key);
		}
	}

	/**
	 * @see AccessData#getReferredIDs(String)
	 */
	public Set getReferredIDs(String id) {
		return referredIDMap.get(id);
	}

	/**
	 * @see AccessData#remove(String, String)
	 */
	public void remove(String id, String key) {
		ArrayMap infoMap = (ArrayMap) idMap.get(id);
		if (infoMap != null) {
			infoMap.remove(key);
		}
	}

	/**
	 * @see AccessData#removeReferredID(String, String)
	 */
	public void removeReferredID(String id, String referredID) {
		HashSet ids = (HashSet) referredIDMap.get(id);
		if (ids != null) {
			ids.remove(referredID);

			if (ids.isEmpty()) {
				referredIDMap.remove(id);
			}
		}
	}

	public void removeReferredIDs(String id) {
		referredIDMap.remove(id);
	}
	
	public void remove(String id) {
		idMap.remove(id);
		referredIDMap.remove(id);
		AggregationNode node = aggregatedIDMap.get(id);
		if (node != null) {
		    if (node.parent != null) {
		        aggregatedIDMap.get(node.parent).children.remove(id);
		    }
		    Set set = getAggregatedIDs(id);
		    for (Object obj : set) {
		        remove(obj.toString());
		    }
		    aggregatedIDMap.remove(id);
		}
	}

	private ArrayMap createInfoMap(String id) {
		ArrayMap infoMap = (ArrayMap) idMap.get(id);
		if (infoMap == null) {
			infoMap = new ArrayMap();
			idMap.put(id, infoMap);
		}
		return infoMap;
	}

    public Set getAggregatedIDs(String id) {
        AggregationNode node = aggregatedIDMap.get(id);
        if (node == null) {
            return Collections.EMPTY_SET;
        } else {
            return new HashSet<String>(node.children);
        }
    }

    public void putAggregatedID(String parentId, String childID) {
        AggregationNode parentNode = aggregatedIDMap.get(parentId);
        AggregationNode childNode = aggregatedIDMap.get(childID);
        
        if (parentNode == null) {
            parentNode = new AggregationNode(null);
            aggregatedIDMap.put(parentId, parentNode);
        }
        
        if (childNode == null) {
            // here we register the parent of the aggregated node in 
            childNode = new AggregationNode(parentId);
            aggregatedIDMap.put(childID, childNode);
        } else {
            // this prevents links from appearing in the aggregation hierarchy
            String oldParent = childNode.parent;
            if (oldParent != null && !oldParent.equals(parentId)) {
                // we need to reattach the aggregated node to a new parent
                AggregationNode oldParentNode = aggregatedIDMap.get(oldParent);
                oldParentNode.children.remove(childID);
                childNode.parent = parentId;
            }
        }
        parentNode.children.add(childID);
    }

    public void removeAggregatedID(String id, String aggregatedID) {
        AggregationNode parentNode = aggregatedIDMap.get(id);
        AggregationNode aggregatedIDNode = aggregatedIDMap.get(aggregatedID);
        
        if (parentNode != null) {
            parentNode.children.remove(aggregatedID);
        }
        aggregatedIDMap.remove(aggregatedID);
    }

    private class UntouchedIterator implements ClosableIterator {
        
        private Iterator wrappedIterator;
        private Object nextValue;
        
        public UntouchedIterator(Iterator it) {
            this.wrappedIterator = it;
        }
        
        public boolean hasNext() {
            getNextUntouched();
            return nextValue != null;
        }

        public Object next() {
            getNextUntouched();
            Object result = nextValue;
            nextValue = null;
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }        
        
        public void close() {
            // don't do anything
        }
        
        private void getNextUntouched() {
            if (nextValue == null) {
                while (wrappedIterator.hasNext()) {
                    String id = (String)wrappedIterator.next();
                    if (!isTouched(id)) {
                        nextValue = id;
                        break;
                    }
                }
            }
        }
    }
    
    public ClosableIterator getUntouchedIDsIterator() {
        return new UntouchedIterator(idMap.keySet().iterator());
    }
    
    public void removeUntouchedIDs() {
        Iterator<Map.Entry<String, Map<String,String>>> it = idMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Map<String,String>> entry = it.next();
            String id = entry.getKey();
            if (!isTouched(id)) {
                it.remove();
                referredIDMap.remove(id);
                detachFromParent(id);
                aggregatedIDMap.remove(id);
            }
        }
    }

    public void touchRecursively(String id) {
        touch(id);
        AggregationNode node = aggregatedIDMap.get(id);
        if (node != null) {
            for (String child : node.children) {
                touchRecursively(child);
            }
        }
    }
    
    public void touch(String id) {
        Map infoMap = createInfoMap(id);
        infoMap.put(TIMESTAMP_KEY, crawlIdentifier);
        infoMap = null;
    }
        
    private class AggregatedClosureIterator implements ClosableIterator {

        private List<Iterator<String>> iteratorStack;
        private String nextValue;
        
        public AggregatedClosureIterator(String firstValue) {
            this.nextValue = firstValue;
            this.iteratorStack = new LinkedList<Iterator<String>>();
        }
        
        public boolean hasNext() {
            return nextValue != null;
        }

        public Object next() {
            String result = nextValue;
            nextValue = null;
            
            AggregationNode node = aggregatedIDMap.get(result);
            if (node != null) {
                iteratorStack.add(0,node.children.iterator());
            }
            
            while (iteratorStack.size() > 0) {
                Iterator<String> it = iteratorStack.get(0);
                if (it.hasNext()) {
                    nextValue = it.next();
                    break;
                } else {
                    iteratorStack.remove(0);
                }
            }
            
            return result;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public void close() {
            // don't do anything
        }
    }
    
    public ClosableIterator getAggregatedIDsClosure(String id) {
        return new AggregatedClosureIterator(id);
    }
    
    /**
     * Returns true if the given id has been touched during the current crawl, false otherwise
     * @param id
     * @return
     */
    public boolean isTouched(String id) {
        ArrayMap infoMap = (ArrayMap) idMap.get(id);
        if (infoMap != null && 
            infoMap.get(TIMESTAMP_KEY) != null && 
            infoMap.get(TIMESTAMP_KEY).equals(crawlIdentifier)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Removes the link between the given node and its parent (if it has a parent). Both
     * the upward and the downward links are removed.
     * @param id
     */
    void detachFromParent(String id) {
        AggregationNode node = aggregatedIDMap.get(id);
        if (node != null && node.parent != null) {
            AggregationNode parentNode = aggregatedIDMap.get(node.parent);
            if (parentNode != null) {
                parentNode.children.remove(id);
            }
            node.parent = null;
        }
    }
}

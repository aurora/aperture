/**
 * Copyright (c) Gunnar Aastrand Grimnes, DFKI GmbH, 2008. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of the DFKI GmbH nor the names of its contributors may be used to endorse or promote products derived from 
 *    this software without specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 *    SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 *    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS  
 *    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE  
 *    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.semanticdesktop.nepomuk.openrdf;

import info.aduna.concurrent.locks.Lock;
import info.aduna.iteration.CloseableIteration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.sail.NotifyingSailConnection;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.nativerdf.NativeStore;
import org.openrdf.sail.nativerdf.NativeStoreConnection;

/**
 * @author grimnes
 *
 */
public class UnionNativeStore extends NativeStore implements Sail, UnionSail {

	/**
	 * @author grimnes
	 *
	 */
	public class UnionNativeStoreConnection extends NativeStoreConnection
			implements SailConnection {

		public UnionNativeStoreConnection(UnionNativeStore unionNativeStore) throws IOException {
			super(unionNativeStore);
		}

		@Override
		protected void removeStatementsInternal(Resource subj, URI pred,
				Value obj, Resource... contexts) throws SailException {
			
			for (Resource u: contexts)
				if (isUnion(u)) throw new SailException("Cannot delete from Union Context: "+u);
			
			super.removeStatementsInternal(subj, pred, obj, contexts);
		}

		@Override
		protected long sizeInternal(Resource... contexts) throws SailException {
			contexts=expandContext(contexts);
			return super.sizeInternal(contexts);
		}

	}

	@Override
	protected CloseableIteration<? extends Statement, IOException> createStatementIterator(
			Resource subj, URI pred, Value obj, boolean includeInferred,
			boolean readTransaction, Resource... contexts) throws IOException {
		
		contexts=expandContext(contexts);
		
		return super.createStatementIterator(subj, pred, obj, includeInferred,
				readTransaction, contexts);
	}
	
	private Resource[] expandContext(Resource... contexts) {
		Set<Resource> res=new HashSet<Resource>();
		for (Resource r: contexts) {
			if (unions.containsKey(r)) {
				expandContexts(unions.get(r), res);
			}
			res.add(r);
		}
		return res.toArray(new Resource[res.size()]);
	}


	private void expandContexts(Set<Resource> contexts, Set<Resource> res) {
		for (Resource r: contexts) {
			if (unions.containsKey(r)) {
				expandContexts(unions.get(r), res);
			}
			res.add(r);
		}
	}


	protected HashMap<Resource, Set<Resource>> unions;
	
	public UnionNativeStore() {
		super();
		reset();
	}

	public UnionNativeStore(File dataDir, String tripleIndexes) {
		super(dataDir, tripleIndexes);
		reset();
	}

	public UnionNativeStore(File dataDir) {
		super(dataDir);
		reset();
	}

	public void addUnion(Resource target, Resource... contexts) {
		if (unions.containsKey(target)) {
			// this is worrying, the view-NS already has a view?
			//log.warn(String.format("ViewGraph %s has more than one viewOn, already found: %s now attempting to add: %s ",target, unions.get(target), source));
			for (Resource c: contexts)
				unions.get(target).add(c);
		} else { 
			Set<Resource> set=new HashSet<Resource>();
			for (Resource c: contexts)
				set.add(c);
			unions.put(target,set);
		}
	}

	public SailConnection baseGetConnection() throws SailException {
		return super.getConnection();
	}

	public void createUnion(URI new_context, URI... contexts) throws SailException {
		if (unions.containsKey(new_context)) {
			throw new UnionSailException("New union model context is already a union");
		}
		if (!isEmpty(new_context)) {
			throw new UnionSailException("New union model context is not empty.");
		}
		unions.put(new_context, set(contexts));
	}
	
	private boolean isEmpty(URI new_context) throws SailException {
		SailConnection c = getConnection();
		
		try { 
			CloseableIteration<? extends Statement, SailException> r = c.getStatements(null,null,null, false, new_context);
			return !r.hasNext();
		} finally { 
			c.close();
		}
		
	}
	
	private Set<Resource> set(Resource... contexts) {
		HashSet<Resource> r = new HashSet<Resource>();
		r.addAll(Arrays.asList(contexts));
		return r;
	}

	public void reset() {
		unions=new HashMap<Resource, Set<Resource>>();
	}





	public boolean isUnion(Resource context) {
		return unions.containsKey(context);
	}




	public void debug() {
		System.err.println("Unions:");
		for (Entry<Resource, Set<Resource>> e: unions.entrySet()) { 
			System.err.println(e.getKey()+" => "+e.getValue());
		}
	}

	
	
	@Override
	protected NotifyingSailConnection getConnectionInternal()
		throws SailException
	{
		if (!super.isInitialized()) {
			throw new IllegalStateException("sail not initialized.");
		}

		try {
			return new UnionNativeStoreConnection(this);
		}
		catch (IOException e) {
			throw new SailException(e);
		}
	}

	public boolean isInUnion(Resource context) {
		for (Entry<Resource, Set<Resource>> e : unions.entrySet()) {
			if ( e.getValue().contains(context) )  
				return true;
		}
		return false;
	}

	public void removeUnion(Resource target) throws UnionSailException {
		if (isInUnion(target)) 
			throw new UnionSailException("Can not delete union that is in another union. Untangle from top please.");
		if (!isUnion(target))
			throw new UnionSailException("Context "+target+" is not a union");
		
		unions.remove(target);
	}
	
	public Lock getReadLock() throws SailException {
		return super.getReadLock();
	}

}

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

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

public interface UnionSail extends Sail {

	public abstract void createUnion(URI new_context, URI... contexts)
			throws SailException;

	/** 
	 * return unwrapped base connection
	 * @return
	 * @throws SailException 
	 */
	public abstract SailConnection baseGetConnection() throws SailException;
	
	/** 
	 * Reset the internal Union tables
	 * @throws SailException 
	 */
	public void reset() throws SailException;
	
	/** 
	 * return true if the given context is a union
	 * @param context
	 * @return bool
	 */
	public boolean isUnion(Resource context);
	
	/** 
	 * return true if the given context is included in a union
	 * @param context
	 * @return bool
	 */
	public boolean isInUnion(Resource context);
	
	
	/** 
	 * Make target be a union of contexts
	 * If target is already a union, the new contexts are added
	 * @param target
	 * @param contexts
	 */
	public void addUnion(Resource target, Resource ... contexts);

	/**
	 * Dump debug info to stderr
	 *
	 */
	public abstract void debug();

	/** 
	 * Remove the given union
	 * @param graph
	 * @throws UnionSailException 
	 */
	public abstract void removeUnion(Resource target) throws UnionSailException; 
	
}
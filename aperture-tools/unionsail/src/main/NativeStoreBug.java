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
import java.io.File;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.semanticdesktop.nepomuk.openrdf.UnionNativeStore;

/**
 * 
 */

/**
 * @author grimnes
 *
 */
public class NativeStoreBug {
	
	private static URI A=new URIImpl("urn:a");
	private static URI B=new URIImpl("urn:b");
	private static URI C=new URIImpl("urn:c");
	
	private static URI S=new URIImpl("urn:s");
	private static URI T=new URIImpl("urn:t");
	
	public static void main(String [] arg) throws Exception {
		
		File f = File.createTempFile("test", "rdf");
		f.delete();

		Sail nativeSail=new UnionNativeStore(f);
		SailRepository r=new SailRepository(nativeSail);
		r.initialize();

		RepositoryConnection c = r.getConnection();
		
		c.add(A,B,C,S);
		
		// this should be 1
		System.err.println(c.size(S));
		// this should be 0
		System.err.println(c.size(T));
		
		c.close();
		
		SailConnection sc = r.getSail().getConnection();

		// this should be 1
		System.err.println(sc.size(S));
		// this should be 0
		System.err.println(sc.size(T));
		
		sc.close();
		r.shutDown();
	}
}
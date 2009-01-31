/**
 * 
 */
package org.semanticdesktop.nepomuk.openrdf;

import java.io.File;
import java.io.IOException;

import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;

/**
 * @author grimnes
 *
 */
public class UnionNativeSailTest extends UnionSailTest {

	@Override
	protected UnionSail createUnionSail(MemoryStore base) throws SailException {
		File f;
		try {
			f = File.createTempFile("test", "rdf");
		} catch (IOException e) {
			throw new SailException(e);
		}
		f.delete();
		System.err.println(f);
		return new UnionNativeStore(f);
	}

}

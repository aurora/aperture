/**
 * 
 */
package org.semanticdesktop.nepomuk.openrdf;

import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;

/**
 * @author grimnes
 *
 */
public class UnionMemoryStoreTest extends UnionSailTest {

	@Override
	protected UnionSail createUnionSail(MemoryStore base) throws SailException {
		return new UnionMemoryStore();
	}

}

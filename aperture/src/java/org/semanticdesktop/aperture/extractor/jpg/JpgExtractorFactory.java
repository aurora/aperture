package org.semanticdesktop.aperture.extractor.jpg;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;

/**
 * Minimalistic factory for JpgExtractor. 
 * @author Manuel Moeller
 *
 */
public class JpgExtractorFactory implements ExtractorFactory {

	private static final Set MIME_TYPES;

	static {
		HashSet set = new HashSet();
		set.add("image/jpeg");
		set.add("image/jpg");
		// TODO: are there other mime types that we can interpret?
		MIME_TYPES = Collections.unmodifiableSet(set);
	}

	public Extractor get() {
		return new JpgExtractor();
	}

	public Set getSupportedMimeTypes() {
		return MIME_TYPES;
	}
}

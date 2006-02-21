/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.util;

import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.rdf.RDFContainer;

/**
 * Utility methods for JavaMail.
 */
public class MailUtil {

	/**
	 * Returns the steroetypical date of a Message. This is equal to the sent date or, if not available, the
	 * received date or, if not available, the retrieval date (i.e., "new Date()").
	 */
	public static Date getDate(Message message) throws MessagingException {
		Date result = message.getSentDate();
		if (result == null) {
			result = message.getReceivedDate();
			if (result == null) {
				result = new Date();
			}
		}

		return result;
	}

	/**
	 * Add statements modeling the specified address metadata to the RDFContainer, using the specified
	 * predicate to connect the address resource to the mail resource.
	 * 
	 * @param address The InternetAddress that will be encoded in the RDF model.
	 * @param predicate The property URI that will be used to connect the address metadata to the mail
	 *            resource.
	 * @param metadata The RDFContainer that will receive the RDF statements and whose described URI is
	 *            expected to represent the mail resource.
	 */
	public static void addAddressMetadata(InternetAddress address, URI predicate, RDFContainer metadata) {
		// fetch the name
		String name = address.getPersonal();
		if (name != null) {
			name = name.trim();
		}

		// fetch the email address
		String emailAddress = address.getAddress();
		if (emailAddress != null) {
			emailAddress = emailAddress.trim();
		}

		// proceed when at least one has a reasonable value
		if (hasRealValue(name) || hasRealValue(emailAddress)) {
			// create a URI for this address
			URI person = new URIImpl(getPersonURI(emailAddress, name));

			// connect the person resource to the mail resource
			metadata.put(predicate, person);
			metadata.add(new StatementImpl(person, RDF.TYPE, AccessVocabulary.AGENT));

			// add name and address details
			if (hasRealValue(name)) {
				Literal literal = new LiteralImpl(name);
				metadata.add(new StatementImpl(person, AccessVocabulary.NAME, literal));
			}

			if (hasRealValue(emailAddress)) {
				Literal literal = new LiteralImpl(emailAddress);
				metadata.add(new StatementImpl(person, AccessVocabulary.EMAIL_ADDRESS, literal));
			}
		}
	}

	/**
	 * Derive a URI for a person based on an email address and a name that can be used in an RDF graph. At
	 * least one of these properties has to have a real value.
	 * 
	 * @param email The email address of the person (optional).
	 * @param name The name of the person (optional).
	 * @return A URI String that can be used to model the person.
	 * @throws IllegalArgumentException when both the email address and the name do not have reasonable
	 *             values.
	 */
	public static String getPersonURI(String email, String name) throws IllegalArgumentException {
		if (hasRealValue(email)) {
			return "email:" + email;
		}
		else if (hasRealValue(name)) {
			return "emailperson:" + name;
		}
		else {
			throw new IllegalArgumentException("no valid email or name, email = " + email + ", name = "
					+ name);
		}
	}

	/**
	 * Determines whether the string is non-null and not equal to an empty string.
	 */
	private static boolean hasRealValue(String string) {
		return string != null && string.length() > 0;
	}
}

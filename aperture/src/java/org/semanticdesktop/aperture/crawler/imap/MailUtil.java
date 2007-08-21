/*
 * Copyright (c) 2006 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.imap;

import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.UriUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;

/**
 * Utility methods for JavaMail.
 */
public class MailUtil {

    /**
     * Returns the steroetypical date of a Message. This is equal to the sent date or, if not available, the
     * received date or, if not available, the retrieval date (i.e., "new Date()").
     * @param message the message we want to get the date for
     * @return the stereotypical date of a message
     * @throws MessagingException
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
            URI person = metadata.getModel().createURI(getPersonURI(emailAddress, name));

            // connect the person resource to the mail resource
            metadata.add(predicate, person);
            metadata.getModel().addStatement(person, RDF.type, NCO.Contact);

            // add name and address details
            if (hasRealValue(name)) {
                Literal literal = metadata.getModel().createPlainLiteral(name);
                metadata.getModel().addStatement(person, NCO.fullname, literal);
            }

            if (hasRealValue(emailAddress)) {
                Literal literal = metadata.getModel().createPlainLiteral(emailAddress);
                Resource emailResource = UriUtil.generateRandomResource(metadata.getModel());
                metadata.getModel().addStatement(person, NCO.hasEmailAddress, emailResource);
                metadata.getModel().addStatement(emailResource, RDF.type, NCO.EmailAddress);
                metadata.getModel().addStatement(emailResource, NCO.emailAddress, literal);
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

/*
 * Copyright (c) 2006 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.semanticdesktop.aperture.accessor.AccessData;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NIE;

/**
 * Utility methods for JavaMail.
 */
public class MailUtil {

    
    private static Charset utf7;
    static {
        try {
            utf7 = Charset.forName("X-MODIFIED-UTF-7");
        } catch (Exception x) {
            // backup, use the charset directly. This is needed for OSGi compliance, where classloaders
            // don't get it right with META-INF/services registration of services.
            try {
                utf7 = new com.beetstra.jutf7.CharsetProvider().charsetForName("X-MODIFIED-UTF-7");
            } catch (Exception y)
            {
                throw new RuntimeException("Cannot load X-MODIFIED-UTF-7, com.beetstra.jutf7.CharsetProvider problem: "+y, y);
            }
        }
    }
    private static final Charset normal = Charset.forName("ISO-8859-1");
    
    /**
     * Converts a string (possibly containing non-ascii characters) to it's representation in the
     * UTF7-IMAP encoding. E.g for 'Böser' 'B&APY-ser' is returned.
     * @param input the input string
     * @return a representation of the input string with all non-ascii characters
     *   converted to their UTF7 escape sequences
     */
    public static String utf7Encode(String input) {
        return performConversion(input, utf7, normal);
    }

    /**
     * Converts in the UTF7 encoding to it's "normal" UTF16 representation.
     * @param input the input string in UTF7
     * @return a 'normal' representation of the input string with UTF7 escape sequences
     *     converted to single UTF16 characters
     */
    public static String utf7Decode(String input) {
        return performConversion(input, normal, utf7);
    }
    
    private static String performConversion(String input, Charset inputCharset, Charset outputCharset) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length() * 3);
            OutputStreamWriter osw = new OutputStreamWriter(baos,inputCharset);
            osw.write(input);
            osw.flush();
            byte [] array = baos.toByteArray();
            StringBuilder builder = new StringBuilder(input.length() * 3);
            InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(array),outputCharset);
            int charRead = 0;
            while ((charRead = reader.read()) != -1) {
                builder.append((char)charRead);
            }
            return builder.toString();
        } catch (IOException ioe) {
            // this will not happen
        }
        return null;
    }
    
    /**
     * Returns the stereotypical date of a Message. This is equal to the sent date or, if not available, the
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
                Resource emailResource = metadata.getModel().createURI(getEmailURI("mailto:", emailAddress));
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
        if (hasRealValue(name)) {
            return "emailperson:" + urlifyString(name);
        } else if (hasRealValue(email)) {
            return getEmailURI("emailperson:", email);
        }
        else {
            throw new IllegalArgumentException("no valid email or name, email = " + email + ", name = "
                    + name);
        }
    }    
    
    private static String getEmailURI(String prefix, String email) throws IllegalArgumentException{
        if (hasRealValue(email)) {
         // there are rare cases when the email addresses are completely broken, I (Antoni Mylka)
            // had a problem with crawling mbox folders with emails sent from an e-learning portal
            // of my school, the To: header contained addresses like:
            // "AGH-EAIiE-KI-SR06L": ;
            // note the quotation marks, the colon, the space and the semicolon, it's crappy and
            // certainly is not a "realValue" for an email address, that's why I introduced this
            // check
            if (!isValidEmailAddress(email)) {
                // i'd rather preserve it, it still does contain some information
                return prefix + urlifyString(email);
            } else {
                return prefix + email;
            }
        } else {
            throw new IllegalArgumentException("Email invalid");
        }
        
    }
    
    /**
     * Applies a crappy heuristic that tries to tell if the email looks like an email
     * address or not. It has been copy-pasted from:<br/>
     * http://forum.java.sun.com/thread.jspa?threadID=530845&start=0&tstart=0<br/>
     * 
     * 
     * @param email
     * @return
     */
    private static boolean isValidEmailAddress(String email) {
         // address should must have a length of minimal 3 examp: a@b
         if (email.length()<3) return false;
         if (email.indexOf("@") ==-1) return false;
         if (email.indexOf(" ") != -1) return false;

         try {
             new URIImpl("mailto:" + email, true);
             return true;
         } catch (Exception e) {
             return false;
         }
    }

    private static String urlifyString(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            // this won't happen because UTF-8 is a valid string, woe be to those poor souls
            // who change this....
            throw new RuntimeException("Really weird....",e);
        }
    }

    /**
     * Determines whether the string is non-null and not equal to an empty string.
     */
    private static boolean hasRealValue(String string) {
        return string != null && string.length() > 0;
    }
    
    /**
     * <p>
     * Stores in the AccessData the fact that the given object has a parent.
     * </p>
     * 
     * <p>
     * This method first searches for the parent for the given data object (see {@link #getParent(DataObject)}
     * If it finds one, it puts a reference between the two. (see
     * {@link AccessData#putReferredID(String, String)}).
     * </p>
     * 
     * <p>
     * The parent must already exist within the access data. (see {@link AccessData#isKnownId(String)}).
     * Otherwise this method won't do anything. This enforces that parents must be crawled and stored in the
     * access data before their children.
     * </p>
     * 
     * @param object the object whose parent is to be recorded in the access data
     * @param accessData the access data where the parent-child relationship is to be recorded, it must
     *            already know the parent ({@link AccessData#isKnownId(String)})
     */
    public static void registerParentRelationshipInAccessData(DataObject object, AccessData accessData) {
        if (accessData == null) {
            return;
        }

        URI parent = getParent(object);
        if (parent != null) {
            String parentID = parent.toString();
            String childID = object.getID().toString();

            if (accessData.isKnownId(parentID)) {
                if (parentID.equals(childID)) {
                    //logger.error("cyclical " + NIE.isPartOf + " property for " + parentID + ", ignoring");
                }
                else {
                    accessData.putReferredID(parentID, childID);
                }
            }
            else {
                //logger.error("Internal error: encountered unknown parent: " + parentID + ", child = "
                //        + childID);
            }
        }
    }
    
    /**
     * <p>
     * Returns the URI of the object's parent in the containment hierachy.
     * </p>
     * 
     * <p>
     * B is a parent of A is there exists a triple A nie:isPartOf B. In case there is more than one such
     * resource, or the resource is not an URI, this method returns null.
     * </p>
     * 
     * @param object the object whose parent we're looking
     * @return the parent of the given object or null if none exists
     */
    @SuppressWarnings("unchecked")
    public static URI getParent(DataObject object) {
        // query for all parents
        Collection parentIDs = object.getMetadata().getAll(NIE.isPartOf);

        // determine all unique parent URIs (the same partOf statement may be returned more than once due
        // to the use of context in the underlying model)
        if (!(parentIDs instanceof Set)) {
            parentIDs = new HashSet(parentIDs);
        }

        // return the parent if there is only one
        if (parentIDs.isEmpty()) {
            return null;
        }
        else if (parentIDs.size() > 1) {
            return null;
        }
        else {
            Node parent = (Node) parentIDs.iterator().next();
            if (parent instanceof URI) {
                return (URI) parent;
            }
            else {
                return null;
            }
        }
    }
}

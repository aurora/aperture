/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.RDFContainerFactory;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.pdf.PdfExtractorFactory;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.util.ResourceUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.semanticdesktop.aperture.vocabulary.NMO;

/**
 * A test case for the data object factory. It checks if the MimeMessage -> RDF mapping actually is 
 * correct (i.e. the same as we would imagine it :)
 */
public class DataObjectFactoryTest extends ApertureTestBase {
    
    /**
     * This method runs the data object factory over a simple email, with plain-text content written in
     * "normal" US-ASCII encoding without any non-ASCII characters. The factory should return exactly one data
     * object with the full-text correctly extracted.
     * @throws Exception
     */
    public void testOrdinarySinglePartPlainTextEmail() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-thunderbird-1.5.eml");
        DataObject obj = fac.getObject();
        // there should only be one data object
        assertNull(fac.getObject()); 
        URI emailUri = obj.getID();
        assertEquals(emailUri.toString(), "uri:dummymailuri:mail-thunderbird-1.5.eml");
        
        RDFContainer container = obj.getMetadata();
        Model model = container.getModel();
        
        /*
         * First we test the names of the sender and receiver
         */
        Resource sender = findSingleObjectResource(model, emailUri, NMO.from);
        assertSingleValueProperty(model, sender, NCO.fullname, "Christiaan Fluit");
        List<Resource> senderEmailAddresses = findObjectResourceList(model, sender, NCO.hasEmailAddress);
        Resource receiver = findSingleObjectResource(model, emailUri, NMO.to);
        assertSingleValueProperty(model, receiver, NCO.fullname, "Christiaan Fluit");
        List<Resource> receiverEmailAddresses = findObjectResourceList(model, receiver, NCO.hasEmailAddress);
        
        /*
         * This is a test that confirms the problem with loosing information which address was the sender
         * and which was the receiver if both have the same name.
         */
        assertEquals(2,senderEmailAddresses.size()); 
        assertTrue(RDFTool.getSingleValueString(model, senderEmailAddresses.get(0), NCO.emailAddress).equalsIgnoreCase("christiaan.fluit@aduna.biz"));
        assertTrue(RDFTool.getSingleValueString(model, senderEmailAddresses.get(1), NCO.emailAddress).equalsIgnoreCase("christiaan.fluit@aduna.biz"));
        assertEquals(2,receiverEmailAddresses.size()); 
        assertTrue(senderEmailAddresses.contains(receiverEmailAddresses.get(0)));
        assertTrue(senderEmailAddresses.contains(receiverEmailAddresses.get(1)));
        assertEquals(sender,receiver); // the sender and receiver are the same resource
        
        testStandardMessageMetadata(model, emailUri, "iso-8859-1", "message/rfc822", 
            "text/plain", "test subject", "15", "2006-02-20T14:47:14", "<43F9C862.9040605@aduna.biz>");
        
        // test the plain text content extraction
        String content = container.getString(NMO.plainTextMessageContent);
        assertEquals("test body\r\n--\r\n",content);
        
        
        validate(model);
        obj.dispose();
    }
    
    /**
     * This method tests the behavior of the data object factory when it is confronted with a
     * multipart/alternative email message. Such a message contains the same content in both the html and the
     * plain text version. The desired behavior is to ignore the html part altogether and put in only the
     * plain text part. Thus only one data object should be returned, even though the message is composed of
     * three mime parts.
     * @throws Exception
     */
    public void testMultipartAlternative() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-multipart-plain-html.eml");
        DataObject obj = fac.getObject();
        // there should only be one data object
        assertNull(fac.getObject()); 
        URI emailUri = obj.getID();
        assertEquals(emailUri.toString(), "uri:dummymailuri:mail-multipart-plain-html.eml");
        RDFContainer container = obj.getMetadata();
        Model model = container.getModel();
        
        testSenderAndReceiver(model, emailUri, "SourceForge.net","surveys@ostg.com",null,"mylka@users.sourceforge.net");

        /*
         * the charset and the content mime type are defined only in the plaintext part, the factory should
         * merge them with the overall message metadata
         */
        testStandardMessageMetadata(model, emailUri, "iso-8859-1", "message/rfc822", 
            "text/plain", "SourceForge.net needs your input on OSS development and support issues", 
            "10251", "2006-10-25T14:50:02", "<CONFIRMITblZz02H67y00000e88@smtp.buzzsponge.com>");
        
        // test the plain text content extraction
        String content = container.getString(NMO.plainTextMessageContent);
        // this is a sentence from the plaintext part 
        assertTrue(content.contains("SourceForge.net is looking for open-source \"experts and opinion-leaders\""));
        // the original message contains weird equality signs at the end of each plaintext line, they should
        // be filtered out
        assertFalse(content.contains("please help us ensure that they ="));
        // the content should not contain any HTML markup
        assertFalse(content.contains("<b>SourceForge.net</b> is looking for open-source \"experts and"));
        
        validate(model);
        obj.dispose();
    }
    
    /**
     * This method tests how does the DataObjectFactory copes with a fairly complicated multipart/mixed message.
     * It is a message with two attachments - the first attachment is a PDF, the second attachment is another
     * message (forwarded). We should get three data objects.
     * @throws Exception
     */
    public void testMultipartMixed() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-multipart-test.eml");
        DataObject obj1 = fac.getObject();
        assertNotNull(obj1);
        DataObject obj2 = fac.getObject();
        assertNotNull(obj2);
        DataObject obj3 = fac.getObject();
        assertNotNull(obj3);
        assertNull(fac.getObject());
        
        // first some test of the first message
        URI emailUri = obj1.getID();
        assertEquals(emailUri.toString(), "uri:dummymailuri:mail-multipart-test.eml");
        RDFContainer container1 = obj1.getMetadata();
        Model model1 = container1.getModel();
        testSenderAndReceiver(model1, emailUri, "Antoni My\u0142ka","antoni.mylka@gmail.com","aperture-devel","aperture-devel@lists.sourceforge.net");
        testStandardMessageMetadata(model1, emailUri, "iso-8859-2", "message/rfc822", "text/plain",
            "[Fwd: Re: [Aperture-devel] Developer's Checklists]", "36872", "2008-07-28T00:10:47",
            "<488CF267.3030008@gmail.com>");
        String content = container1.getString(NMO.plainTextMessageContent);
        assertEquals("This is a test of a multipart message, that has some content and an \r\n" + 
                     "attached message, and a PDF attachment. Let's see how the MimeSubCrawler \r\n" + 
                     "will handler this.\r\n" +
                     "\r\n" +
                     "Antoni Mylka\r\n",
                     content); 
        validate(container1);
        
        // then some tests of the attached message (which should reside in the third data object);
        URI emailUri3 = obj3.getID();
        assertEquals(emailUri3.toString(), "uri:dummymailuri:mail-multipart-test.eml#2");
        RDFContainer container3 = obj3.getMetadata();
        Model model3 = container3.getModel();
        testSenderAndReceiver(model3, emailUri3, "Leo Sauermann","leo.sauermann@dfki.de","Antoni Mylka","antoni.mylka@gmail.com");
        testStandardMessageMetadata(model3, emailUri3, "iso-8859-2", "message/rfc822", "text/plain",
            "Re: [Aperture-devel] Developer's Checklists", "1341", "2008-07-25T10:50:18",
            "<488993CA.8070205@dfki.de>");
        String content3 = container3.getString(NMO.plainTextMessageContent);
        assertTrue(content3.contains("> http://aperture.wiki.sourceforge.net/DevelopersChecklists"));
        assertTrue(content3.contains("> about all this, but it may nevertheless be interesting."));
        validate(container3);
        obj3.dispose();
        
        // the second data object should be a FileDataObject containing a PDF
        assertTrue(obj2 instanceof FileDataObject);
        FileDataObject fobj2 = (FileDataObject)obj2;
        URI pdfUri = fobj2.getID();
        RDFContainer container2 = fobj2.getMetadata();
        Model model2 = container2.getModel();
        InputStream contentStream = fobj2.getContent();
        assertNotNull(contentStream);
        assertEquals(pdfUri.toString(), "uri:dummymailuri:mail-multipart-test.eml#1");
        assertSingleValueProperty(model2, pdfUri, NIE.mimeType, "application/pdf");
        assertSingleValueProperty(model2, pdfUri, NFO.fileName, "pdf-openoffice-2.0-writer.pdf");
        
        // now apply the mime type identifier
        MimeTypeIdentifier mimeTypeIdentifier = new MagicMimeTypeIdentifier();
        int minimumArrayLength = mimeTypeIdentifier.getMinArrayLength();
        contentStream.mark(minimumArrayLength + 10); // add some for safety
        byte[] bytes = IOUtil.readBytes(contentStream, minimumArrayLength);
        String mimeType = mimeTypeIdentifier.identify(bytes, null, pdfUri);
        assertEquals(mimeType, "application/pdf");
        contentStream.reset();

        // and the extractor
        Extractor extractor = new PdfExtractorFactory().get();
        extractor.extract(pdfUri, contentStream, null, mimeType, container2);
        String contentString = container2.getString(NIE.plainTextContent);
        assertEquals("This is an example document created with OpenOffice 2.0\r\n",contentString);
        
        validate(container2);
        obj2.dispose();
        
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////// BASIC PLUMBING /////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private void testSenderAndReceiver(Model model, URI emailUri, String senderName, String senderEmail,
            String receiverName, String receiverEmail) {
        Resource sender = findSingleObjectResource(model, emailUri, NMO.from);
        if (senderName != null) {
            assertSingleValueProperty(model, sender, NCO.fullname, senderName);
        }
        Resource senderAddress = findSingleObjectResource(model, sender, NCO.hasEmailAddress);
        assertSingleValueProperty(model, senderAddress, NCO.emailAddress, senderEmail);
        
        Resource receiver = findSingleObjectResource(model, emailUri, NMO.to);
        if (receiverName != null) {
            assertSingleValueProperty(model, receiver, NCO.fullname, receiverName);
        }
        Resource receiverAddress = findSingleObjectResource(model, receiver, NCO.hasEmailAddress);
        assertSingleValueProperty(model, receiverAddress, NCO.emailAddress, receiverEmail);
    }
    
    private void testStandardMessageMetadata(Model model, URI emailUri, String charset, String mimeType, 
            String contentMimeType, String subject, String byteSize, String contentCreated, String messageId) {
        assertSingleValueProperty(model, emailUri, NIE.characterSet, charset);
        assertSingleValueProperty(model, emailUri, NIE.mimeType, mimeType);
        assertSingleValueProperty(model, emailUri, NMO.contentMimeType, contentMimeType);
        assertSingleValueProperty(model, emailUri, NMO.messageSubject, subject);
        assertSingleValueProperty(model, emailUri, NIE.byteSize, model.createDatatypeLiteral(byteSize, XSD._integer));
        // this exhibits the problem with ambiguous dates
        assertSingleValueProperty(model, emailUri, NIE.contentCreated, model.createDatatypeLiteral(contentCreated, XSD._dateTime));
        // this exhibits the problem with brackets
        assertSingleValueProperty(model, emailUri, NMO.messageId, messageId);
        
        List<Resource> emailTypes = findObjectResourceList(model, emailUri, RDF.type);
        assertEquals(4,emailTypes.size());
        assertTrue(emailTypes.contains(NMO.Email));
        assertTrue(emailTypes.contains(NMO.MimeEntity));
        assertTrue(emailTypes.contains(NMO.MailboxDataObject));
        assertTrue(emailTypes.contains(NIE.DataObject));
    }

    private RDFContainerFactory containerFactory;
    
    @Override public void setUp() {
        this.containerFactory = new DataObjectFactoryTestRDFContainerFactory();
    }
    
    @Override public void tearDown() {
        containerFactory = null;
    }
    
    class DataObjectFactoryTestRDFContainerFactory implements RDFContainerFactory {
        public RDFContainer getRDFContainer(URI uri) {
            Model model = RDF2Go.getModelFactory().createModel();
            model.open();
            return new RDFContainerImpl(model,uri);
        }
    }
    
    private DataObjectFactory wrapEmail(String resourceName) throws MessagingException, IOException {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + resourceName, this.getClass());
        MimeMessage msg =  new MimeMessage(null, stream);
        DataObjectFactory fac = new DataObjectFactory(msg,containerFactory,null,null,
            new URIImpl("uri:dummymailuri:" + resourceName), null);
        return fac;
    }
}


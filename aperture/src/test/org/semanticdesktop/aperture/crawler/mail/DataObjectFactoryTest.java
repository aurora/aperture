/*
 * Copyright (c) 2008 Aduna and Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Academic Free License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.Resource;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.util.RDFTool;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.XSD;
import org.semanticdesktop.aperture.ApertureTestBase;
import org.semanticdesktop.aperture.accessor.DataObject;
import org.semanticdesktop.aperture.accessor.FileDataObject;
import org.semanticdesktop.aperture.accessor.MessageDataObject;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.pdf.PdfExtractorFactory;
import org.semanticdesktop.aperture.rdf.RDFContainer;
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
        assertTrue(obj instanceof MessageDataObject);
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
        Set<Resource> senderEmailAddresses = findObjectResourceSet(model, sender, NCO.hasEmailAddress);
        Resource receiver = findSingleObjectResource(model, emailUri, NMO.to);
        assertSingleValueProperty(model, receiver, NCO.fullname, "Christiaan Fluit");
        Set<Resource> receiverEmailAddresses = findObjectResourceSet(model, receiver, NCO.hasEmailAddress);
        
        /*
         * This is a test that confirms the problem with loosing information which address was the sender
         * and which was the receiver if both have the same name.
         */
        assertEquals(2,senderEmailAddresses.size());
        Iterator<Resource> it = senderEmailAddresses.iterator();
        assertTrue(RDFTool.getSingleValueString(model, it.next(), NCO.emailAddress).equalsIgnoreCase("christiaan.fluit@aduna.biz"));
        assertTrue(RDFTool.getSingleValueString(model, it.next(), NCO.emailAddress).equalsIgnoreCase("christiaan.fluit@aduna.biz"));
        
        assertEquals(2,receiverEmailAddresses.size()); 
        it = receiverEmailAddresses.iterator();
        assertTrue(senderEmailAddresses.contains(it.next()));
        assertTrue(senderEmailAddresses.contains(it.next()));
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
        assertTrue(obj instanceof MessageDataObject);
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
        assertTrue(obj1 instanceof MessageDataObject);
        assertNotNull(obj1);
        DataObject obj2 = fac.getObject();
        assertTrue(obj2 instanceof FileDataObject);
        assertNotNull(obj2);
        DataObject obj3 = fac.getObject();
        assertTrue(obj3 instanceof MessageDataObject);
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
        obj1.dispose();
        
        // then some tests of the attached message (which should reside in the third data object);
        assertEquals(obj3.getID().toString(), "uri:dummymailuri:mail-multipart-test.eml#2");
        Model model3 = obj3.getMetadata().getModel();
        testSenderAndReceiver(model3, obj3.getID(), "Leo Sauermann","leo.sauermann@dfki.de","Antoni Mylka","antoni.mylka@gmail.com");
        testStandardMessageMetadata(model3, obj3.getID(), "iso-8859-2", "message/rfc822", "text/plain",
            "Re: [Aperture-devel] Developer's Checklists", "1341", "2008-07-25T10:50:18",
            "<488993CA.8070205@dfki.de>");
        assertMessageContentContains("> http://aperture.wiki.sourceforge.net/DevelopersChecklists", obj3);
        assertMessageContentContains("> about all this, but it may nevertheless be interesting.", obj3);
        validate(model3);
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
        
        assertMimeType("application/pdf", pdfUri, contentStream);

        // and the extractor
        Extractor extractor = new PdfExtractorFactory().get();
        extractor.extract(pdfUri, contentStream, null, "application/pdf", container2);
        String contentString = container2.getString(NIE.plainTextContent);
        assertTrue(contentString.contains("This is an example document created with OpenOffice 2.0"));
        
        validate(container2);
        obj2.dispose();
    }

    /**
     * This method tests the partUriDelimiter feature. It allows the user to customize the delimiter between the
     * uri of the message and the part identifiers
     * @throws Exception
     */
    public void testPartUriDelimiter() throws Exception {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + "mail-multipart-test.eml", this.getClass());
        MimeMessage msg =  new MimeMessage(null, stream);
        DataObjectFactory fac = new DataObjectFactory(msg,containerFactory,service,null,null,
            new URIImpl("mime:zip:uri:dummymailuri:somefile.zip!/mail-multipart-test.eml!/"), null,"");
        DataObject obj1 = fac.getObject();
        DataObject obj2 = fac.getObject();
        DataObject obj3 = fac.getObject();
        
        URI emailUri = obj1.getID();
        assertEquals(emailUri.toString(), "mime:zip:uri:dummymailuri:somefile.zip!/mail-multipart-test.eml!/");
        obj1.dispose();
        
        
        assertEquals(obj3.getID().toString(), "mime:zip:uri:dummymailuri:somefile.zip!/mail-multipart-test.eml!/2");
        obj3.dispose();

        URI pdfUri = obj2.getID();
        assertEquals(pdfUri.toString(), "mime:zip:uri:dummymailuri:somefile.zip!/mail-multipart-test.eml!/1");
        obj2.dispose();
        service.shutdown();
    }
    
    /**
     * A test for a message that has been taken from within a thread. It contains References: and In-Reply-To:
     * headers. It is a multipart/mixed message, whose first part is a multipart/alternative. An example of
     * the ingenuity of the SF mailing list software, they add the ads and the mailing list signature as
     * attachments. All the 'References:' and 'In-Reply-To:' headers should appear in the extracted RDF.
     * 
     * @throws Exception
     */
    public void testMessageInAThread() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-threaded.eml");
        DataObject obj = fac.getObject();
        assertTrue(obj instanceof MessageDataObject);
        RDFContainer metadata = obj.getMetadata();
        Model model = metadata.getModel();
        fac.getObject().dispose();
        fac.getObject().dispose();
        assertNull(fac.getObject());

        assertEquals("<452A6646.10207@dfki.de>", findSingleObjectNode(model, metadata.getDescribedUri(),
            NMO.messageId).asLiteral().getValue());
        assertReferencedEmails(obj, NMO.references, "<452A03DB.3020705@dfki.de>",
            "<452A168F.7010108@aduna-software.com>", "<452A5832.2080801@dfki.de>",
            "<452A6537.1030309@aduna-software.com>");
        assertReferencedEmails(obj, NMO.inReplyTo, "<452A6537.1030309@aduna-software.com>");
        validate(obj);
        obj.dispose();
    }

    /**
     * Tests whether the 'References:' and 'In-Reply-To:' are extracted correctly from a forwarded message.
     * The .eml file that is tested has a following structure
     * 
     * <pre>
     * multipart/mixed
     * - plain text (my greeting)
     * - multipart/mixed (the forwarded message)
     *   - multipart/alternative (the leo's reply)
     *     - plain text - plain text content
     *     - html text - the html text contet
     *   - plain text - the sourceforge ad
     *   - plain text - the sourceforge list signature
     * </pre>
     * 
     * This structure should yield four data objects. (my greeting, leo's reply in plain text, the ad and the
     * signature). The second data object should have correct References: and In-Reply-To links.
     * @throws Exception 
     */
    public void testForwardedMessageWithReferecesAndInReplyToHeaders() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-forwarded-references.eml");
        DataObject myGreeting = fac.getObject(); 
        assertTrue(myGreeting instanceof MessageDataObject);
        DataObject forwardedMsg = fac.getObject();
        assertTrue(forwardedMsg instanceof MessageDataObject);
        DataObject sfAd = fac.getObject();
        assertTrue(sfAd instanceof FileDataObject);
        DataObject sfSig = fac.getObject();
        assertTrue(sfSig instanceof FileDataObject);
        assertNull(fac.getObject()); 

        assertMessageId("<48DFA882.5010502@poczta.onet.pl>", myGreeting);
        assertMessageId("<46ADDEF5.6080504@dfki.de>", forwardedMsg);
        assertMessageContentContains("A test message that contains a forwarded message",myGreeting);
        assertMessageContentContains("There are two concrete benefits to using XRIs identified",forwardedMsg);
        
        /*
         * The two last data objects are unnamed plain text message parts - attachments, they should be
         * interpreted as such i.e. no content in the metadata, everything is a plain text FileDataObject
         */
        assertNull(sfAd.getMetadata().getString(NMO.plainTextMessageContent));
        assertAsciiFileContentContains(sfAd, "This SF.net email is sponsored by: Splunk Inc.");
        
        assertNull(sfSig.getMetadata().getString(NMO.plainTextMessageContent));
        assertAsciiFileContentContains(sfSig, "Aperture-devel mailing list");
        
        assertReferencedEmails(forwardedMsg, NMO.references, "<46AA316B.40604@dfki.de>",
            "<21635b740707300447w71553199j7e14e44f47727f37@mail.gmail.com>");
        assertReferencedEmails(forwardedMsg, NMO.inReplyTo,
            "<21635b740707300447w71553199j7e14e44f47727f37@mail.gmail.com>");
        validate(myGreeting);
        myGreeting.dispose();
        forwardedMsg.dispose();
        sfAd.dispose();
        sfSig.dispose();
    }
    
    /**
     * Tests whether .xml files attached to the email are returned as separate FileDataObjects. Problems have
     * been reported with XML attachments being mistakenly returned as DataObjects with their entire content
     * being returned as NMO.messagePlainTextContent (with all the tags).
     * 
     * @throws Exception
     */
    public void testXmlAttachment() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-xml-attachment.eml");
        DataObject mailContent = fac.getObject();
        assertTrue(mailContent instanceof MessageDataObject);
        DataObject xmlAttachment = fac.getObject();
        assertTrue(xmlAttachment instanceof FileDataObject);
        assertNull(fac.getObject());
        assertMessageContentContains("test mail.", mailContent);
        testSenderAndReceiver(mailContent.getMetadata().getModel(), mailContent.getID(), "Christiaan Fluit", 
            "christiaan.fluit@aduna-software.com", null, "chris@aduna-software.com");
        assertEquals("line.xml",xmlAttachment.getMetadata().getString(NFO.fileName));
        assertMimeType("text/xml", new URIImpl("uri:line.xml"), ((FileDataObject)xmlAttachment).getContent());
        validate(mailContent);
        mailContent.dispose();
        xmlAttachment.dispose();
    }
    
    /**
     * <p>
     * Tests whether .txt files attached to the email are returned as separate FileDataObjects instead of as
     * DataObjects with their content already extracted.
     * </p>
     * 
     * The MIME structure of this .eml file is:
     * 
     * <pre>
     * multipart/mixed
     *   text/plain - body text
     *   text/plain, filename=&quot;attachment.txt&quot; - attachment text
     * </pre>
     * 
     * <p>
     * Obviously, it should yield two DataObjects. The second one should be a FileDataObject.
     * </p>
     * 
     * @throws Exception
     */
    public void testPlainTextAttachment() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-plaintext-attachment.eml");
        DataObject mail = fac.getObject();
        assertTrue(mail.getMetadata().getString(NMO.plainTextMessageContent).contains("Example body text."));
        DataObject attachment = fac.getObject();
        assertTrue(attachment instanceof FileDataObject);
        String content = IOUtil.readString(((FileDataObject)attachment).getContent());
        assertTrue(content.contains("test attachment"));
        assertEquals("attachment.txt",attachment.getMetadata().getString(NFO.fileName));
        assertTrue(attachment.getMetadata().getAll(RDF.type).contains(NFO.Attachment));
        assertNull(fac.getObject());
        validate(attachment);
        mail.dispose();
        attachment.dispose();
    }
    
    /**
     * <p>
     * The .eml file tested in this test has been submitted with a problem.
     * </p>
     * 
     * <p>
     * It has a following mime structure:
     * 
     * <pre>
     * multipart/mixed
     * - plain text (the text of the message)
     * - plain text (a text/plain attachment with a name: ConfigFilePanel.java)
     * - plain text (a text/plain attachment without a name)
     * - plain text (a text/plain attachment without a name)
     * </pre>
     * 
     * </p>
     * 
     * <p>
     * <ol>
     * <li>Four data objects should be returned, a MessageDataObject for the first part, and three FileDataObjects
     * for three subsequent parts.</li>
     * <li>The second dataobject should contain a filename.</li>
     * <li>The plaintext attachment should NOT be reported as messages.</li>
     * <li>The unnamed file attachments should NOT get a contentCreated
     * property.</li>
     * </ol>
     *    
     * </p>
     * 
     * @throws Exception
     */
    public void testUnsupportedOperationException() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-UnsupportedOperationException.eml");
        DataObject mail = fac.getObject();
        assertTrue(mail.getMetadata().getString(NMO.plainTextMessageContent).contains(
            "I've attached my .java file"));
        assertTrue(mail instanceof MessageDataObject);
        
        DataObject javaAttachment = fac.getObject();
        assertTrue(javaAttachment instanceof FileDataObject);
        assertEquals("ConfigFilePanel.java",javaAttachment.getMetadata().getString(NFO.fileName));
        assertFalse(javaAttachment.getMetadata().getAll(RDF.type).contains(NMO.Message));
        
        DataObject firstUnnamedAttachment = fac.getObject();
        assertTrue(firstUnnamedAttachment instanceof FileDataObject);
        assertNull(firstUnnamedAttachment.getMetadata().getString(NFO.fileName));
        assertNull(firstUnnamedAttachment.getMetadata().getDate(NIE.contentCreated));
        assertFalse(firstUnnamedAttachment.getMetadata().getAll(RDF.type).contains(NMO.Message));
        
        DataObject secondUnnamedAttachment = fac.getObject();
        assertTrue(secondUnnamedAttachment instanceof FileDataObject);
        assertNull(secondUnnamedAttachment.getMetadata().getString(NFO.fileName));
        assertNull(secondUnnamedAttachment.getMetadata().getDate(NIE.contentCreated));
        assertFalse(secondUnnamedAttachment.getMetadata().getAll(RDF.type).contains(NMO.Message));
        
        for (RDFContainer cont : containerFactory.returnedContainers.values()) {
            validate(cont);
        }
        mail.dispose();
        javaAttachment.dispose();
        firstUnnamedAttachment.dispose();
        secondUnnamedAttachment.dispose();
    }
    
    /**
     * Tests whether the @link {@link DataObjectFactory#getObjectAndDisposeAllOtherObjects(String)} works
     * correctly. The method will process an email that yields four data objects, will try to obtain the third
     * one, and then will check if all others have been disposed already.
     * 
     * @throws Exception
     */
    public void testGetObjectAndDisposeAllOther() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-forwarded-references.eml");
        DataObject object = fac.getObjectAndDisposeAllOtherObjects("uri:dummymailuri:mail-forwarded-references.eml#1-1");
        assertFalse(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml").getModel().isOpen());
        assertFalse(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1").getModel().isOpen());
        assertTrue(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1-1").getModel().isOpen());
        assertFalse(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1-2").getModel().isOpen());
        assertAsciiFileContentContains(object, "This SF.net email is sponsored by: Splunk Inc.");
        object.dispose();
    }

    /**
     * <p>
     * Tests whether the @link {@link DataObjectFactory#getObject(String)} works correctly. The method will
     * process an email that originally yields four data objects, will try to obtain the third one, and then
     * will check that no other object has been disposed in the process.
     * </p>
     * 
     * <p>
     * The second part of the text will call the {@link DataObjectFactory#disposeRemainingObjects()} method
     * and will check if all models have been properly disposed.
     * </p>
     * 
     * @throws Exception
     */
    public void testGetObjectString() throws Exception {
        DataObjectFactory fac = wrapEmail("mail-forwarded-references.eml");
        DataObject object = fac.getObject("uri:dummymailuri:mail-forwarded-references.eml#1-1");
        assertTrue(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml").getModel().isOpen());
        assertTrue(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1").getModel().isOpen());
        assertTrue(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1-1").getModel().isOpen());
        assertTrue(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1-2").getModel().isOpen());
        assertAsciiFileContentContains(object, "This SF.net email is sponsored by: Splunk Inc.");
        fac.disposeRemainingObjects();
        assertFalse(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml").getModel().isOpen());
        assertFalse(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1").getModel().isOpen());
        assertFalse(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1-1").getModel().isOpen());
        assertFalse(containerFactory.returnedContainers.get("uri:dummymailuri:mail-forwarded-references.eml#1-2").getModel().isOpen());
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
        
        Set<Resource> emailTypes = findObjectResourceSet(model, emailUri, RDF.type);
        assertEquals(4,emailTypes.size());
        assertTrue(emailTypes.contains(NMO.Email));
        assertTrue(emailTypes.contains(NMO.MimeEntity));
        assertTrue(emailTypes.contains(NMO.MailboxDataObject));
        assertTrue(emailTypes.contains(NIE.DataObject));
    }

    private TestRDFContainerFactory containerFactory;
    private ExecutorService service;
    
    @Override public void setUp() {
        this.containerFactory = new TestRDFContainerFactory();
        this.service = Executors.newSingleThreadExecutor();
    }
    
    @Override public void tearDown() {
        containerFactory = null;
        service.shutdown();
        service = null;
    }
    
    private DataObjectFactory wrapEmail(String resourceName) throws MessagingException, IOException {
        InputStream stream = ResourceUtil.getInputStream(DOCS_PATH + resourceName, this.getClass());
        MimeMessage msg =  new MimeMessage(null, stream);
        DataObjectFactory fac = new DataObjectFactory(msg,containerFactory,service, null,null,
            new URIImpl("uri:dummymailuri:" + resourceName), null);
        return fac;
    }
    
    private void assertMessageId(String id, DataObject obj) {
        assertEquals(id, findSingleObjectNode(obj.getMetadata().getModel(), obj.getID(), NMO.messageId)
                .asLiteral().getValue());
    }
    
    private void assertMessageContentContains(String string, DataObject obj) {
        assertTrue(obj.getMetadata().getString(NMO.plainTextMessageContent).contains(string));
    }
    
    private void assertAsciiFileContentContains(DataObject obj, String string) throws IOException {
        assertTrue(IOUtil.readString(
            new InputStreamReader(((FileDataObject) obj).getContent(), Charset.forName("US-ASCII")))
                .contains(string));
    }
    
    @SuppressWarnings("unchecked")
    private void assertReferencedEmails(DataObject object, URI prop, String ... ids) {
        Set<String> referencedIdsSet = new TreeSet<String>();
        referencedIdsSet.addAll(Arrays.asList(ids));
        Collection<Node> nodes = object.getMetadata().getAll(prop);
        assertEquals(referencedIdsSet.size(), nodes.size());
        Model model = object.getMetadata().getModel();
        for (Node node : nodes) {
            Resource res = node.asResource();
            assertSingleValueProperty(model, res, RDF.type, NMO.Email);
            String value = findSingleObjectNode(model, res, NMO.messageId).asLiteral().getValue();
            assertTrue(referencedIdsSet.remove(value));
        }
        assertTrue(referencedIdsSet.isEmpty());
    }
}


/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mime;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.util.HtmlParserUtil;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.MailUtil;
import org.semanticdesktop.aperture.vocabulary.DATA;

/**
 * An Extractor implementation for message/rfc822-style messages.
 * 
 * <p>
 * The main purpose of this class is to process mails that are stored as .eml-files, as mails originating from
 * a mail server are typically already completely processed (i.e., a structured DataObject is returned rather
 * than a FileDataObject whose stream has to be processed).
 * 
 * <p>
 * Furthermore, this class can also handle web archives in MHTML style (.mht files), as created by Internet
 * Explorer and Mozilla/Firefox (using the Mozilla Archive Format plugin), which have a similar MIME
 * structure.
 * 
 * <p>
 * Only typical body parts are processed during full-text extraction, (binary) attachments are not handled.
 */
public class MimeExtractor implements Extractor {

	public void extract(URI id, InputStream stream, Charset charset, String mimeType, RDFContainer result)
			throws ExtractorException {
		try {
			// parse the stream
			MimeMessage message = new MimeMessage(null, stream);

			// extract the full-text
			StringBuffer buffer = new StringBuffer(10000);
			processContent(message.getContent(), buffer);
			String text = buffer.toString().trim();
			if (text.length() > 0) {
				result.add(DATA.fullText, text);
			}

			// extract other metadata
			String title = message.getSubject();
			if (title != null) {
				title = title.trim();
				if (title.length() > 0) {
					result.add(DATA.title, title);
				}
			}

			try {
				copyAddress(message.getFrom(), DATA.from, result);
			}
			catch (AddressException e) {
				// ignore
			}

			copyAddress(getRecipients(message, RecipientType.TO), DATA.to, result);
			copyAddress(getRecipients(message, RecipientType.CC), DATA.cc, result);
			copyAddress(getRecipients(message, RecipientType.BCC), DATA.bcc, result);

			Date date = MailUtil.getDate(message);
			if (date != null) {
				result.add(DATA.date, date);
			}
		}
		catch (MessagingException e) {
			throw new ExtractorException(e);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}

	private void processContent(Object content, StringBuffer buffer) throws MessagingException, IOException {
		if (content instanceof String) {
			buffer.append(content);
			buffer.append(' ');
		}
		else if (content instanceof BodyPart) {
			BodyPart bodyPart = (BodyPart) content;

			// append the file name, if any
			String fileName = bodyPart.getFileName();
			if (fileName != null) {
				try {
					fileName = MimeUtility.decodeWord(fileName);
				}
				catch (MessagingException e) {
					// happens on unencoded file names! so just ignore it and leave the file name as it is
				}
				buffer.append(fileName);
				buffer.append(' ');
			}
			
			// append the content, if any
			content = bodyPart.getContent();

			// remove any html markup if necessary
			String contentType = bodyPart.getContentType();
			if (contentType != null && content instanceof String) {
				contentType = contentType.toLowerCase();
				if (contentType.indexOf("text/html") >= 0) {
					content = extractTextFromHtml((String) content);
				}
			}

			processContent(content, buffer);
		}
		else if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			String subType = null;

			String contentType = multipart.getContentType();
			if (contentType != null) {
				ContentType ct = new ContentType(contentType);
				subType = ct.getSubType();
				if (subType != null) {
					subType = subType.trim().toLowerCase();
				}
			}

			if ("alternative".equals(subType)) {
				handleAlternativePart(multipart, buffer);
			}
			else if ("signed".equals(subType)) {
				handleProtectedPart(multipart, 0, buffer);
			}
			else if ("encrypted".equals(subType)) {
				handleProtectedPart(multipart, 1, buffer);
			}
			else {
				// handles multipart/mixed, /digest, /related, /parallel, /report and unknown subtypes
				handleMixedPart(multipart, buffer);
			}
		}
	}

	private void handleAlternativePart(Multipart multipart, StringBuffer buffer) throws MessagingException,
			IOException {
		// find the first text/plain part or else the first text/html part
		boolean isHtml = false;

		int idx = getPartWithMimeType(multipart, "text/plain");
		if (idx < 0) {
			idx = getPartWithMimeType(multipart, "text/html");
			isHtml = true;
		}

		if (idx >= 0) {
			Object content = multipart.getBodyPart(idx).getContent();
			if (content != null) {
				if (content instanceof String && isHtml) {
					content = extractTextFromHtml((String) content);
				}

				processContent(content, buffer);
			}
		}
	}
	
	private void handleMixedPart(Multipart multipart, StringBuffer buffer) throws MessagingException,
			IOException {
		int count = multipart.getCount();
		for (int i = 0; i < count; i++) {
			processContent(multipart.getBodyPart(i), buffer);
		}
	}
	
	private void handleProtectedPart(Multipart multipart, int index, StringBuffer buffer)
			throws MessagingException, IOException {
		if (index < multipart.getCount()) {
			processContent(multipart.getBodyPart(index), buffer);
		}
	}

	private int getPartWithMimeType(Multipart multipart, String mimeType) throws MessagingException {
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (mimeType.equalsIgnoreCase(getMimeType(bodyPart))) {
				return i;
			}
		}

		return -1;
	}

	private String getMimeType(Part mailPart) throws MessagingException {
		String contentType = mailPart.getContentType();
		if (contentType != null) {
			ContentType ct = new ContentType(contentType);
			return ct.getBaseType();
		}

		return null;
	}

	private String extractTextFromHtml(String string) {
		// parse the HTML and extract full-text and metadata
		HtmlParserUtil.ContentExtractor extractor = new HtmlParserUtil.ContentExtractor();
		InputStream stream = new ByteArrayInputStream(string.getBytes()); // default encoding, problematic?
		try {
			HtmlParserUtil.parse(stream, null, extractor);
		}
		catch (ExtractorException e) {
			return "";
		}

		// append metadata and full-text to a string buffer
		StringBuffer buffer = new StringBuffer(32 * 1024);
		append(buffer, extractor.getTitle());
		append(buffer, extractor.getAuthor());
		append(buffer, extractor.getDescription());
		Iterator keywords = extractor.getKeywords();
		while (keywords.hasNext()) {
			append(buffer, (String) keywords.next());
		}
		append(buffer, extractor.getText());

		// return the buffer's content
		return buffer.toString();
	}

	private void append(StringBuffer buffer, String text) {
		if (text != null) {
			buffer.append(text);
			buffer.append(' ');
		}
	}

	private Address[] getRecipients(MimeMessage message, RecipientType type) throws MessagingException {
		Address[] result = null;

		try {
			result = message.getRecipients(type);
		}
		catch (AddressException e) {
			// ignore
		}

		return result;
	}

	private void copyAddress(Object address, URI predicate, RDFContainer result) {
		if (address instanceof InternetAddress) {
			MailUtil.addAddressMetadata((InternetAddress) address, predicate, result);
		}
		else if (address instanceof InternetAddress[]) {
			InternetAddress[] array = (InternetAddress[]) address;
			for (int i = 0; i < array.length; i++) {
				MailUtil.addAddressMetadata(array[i], predicate, result);
			}
		}
	}
}

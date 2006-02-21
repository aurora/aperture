/*
 * Copyright (c) 2006 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.extractor.mime;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Message.RecipientType;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openrdf.model.URI;
import org.semanticdesktop.aperture.accessor.AccessVocabulary;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.util.MailUtil;

/**
 * An Extractor implementation for message/rfc822-style messages.
 * 
 * <p>
 * The main purpose of this class is to process mails that are stored as .eml-files, as mails originating from
 * a mail server are typically already completely processed (i.e., a structured DataObject is returned rather
 * than a FileDataObject whose stream has to be processed).
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
			extractText(message.getContent(), buffer);
			String text = buffer.toString().trim();
			if (text.length() > 0) {
				result.put(AccessVocabulary.FULL_TEXT, text);
			}

			// extract other metadata
			String title = message.getSubject();
			if (title != null) {
				title = title.trim();
				if (title.length() > 0) {
					result.put(AccessVocabulary.TITLE, title);
				}
			}

			copyAddress(message.getFrom(), AccessVocabulary.FROM, result);
			copyAddress(message.getRecipients(RecipientType.TO), AccessVocabulary.TO, result);
			copyAddress(message.getRecipients(RecipientType.CC), AccessVocabulary.CC, result);
			copyAddress(message.getRecipients(RecipientType.BCC), AccessVocabulary.BCC, result);
			
			Date date = MailUtil.getDate(message);
			if (date != null) {
				result.put(AccessVocabulary.DATE, date);
			}
		}
		catch (MessagingException e) {
			throw new ExtractorException(e);
		}
		catch (IOException e) {
			throw new ExtractorException(e);
		}
	}

	private void extractText(Object content, StringBuffer buffer) throws MessagingException, IOException {
		if (content instanceof Multipart) {
			Multipart multiContent = (Multipart) content;
			if (isMultipartAlternative(multiContent)) {
				int idx = getPartWithMimeType(multiContent, "text/plain");

				if (idx < 0) {
					// FIXME: we should parse this html, perhaps move HtmlExtractor's code to a utility class?
					idx = getPartWithMimeType(multiContent, "text/html");
				}

				if (idx >= 0) {
					content = multiContent.getBodyPart(idx).getContent();
				}
			}
		}

		if (content instanceof String) {
			buffer.append((String) content);
		}
		else if (content instanceof InputStream) {
			// ignore
		}
		else if (content instanceof Multipart) {
			Multipart multipartContent = (Multipart) content;
			for (int i = 0; i < multipartContent.getCount(); i++) {
				BodyPart part = multipartContent.getBodyPart(i);
				Object partContent = part.getContent();
				if (partContent instanceof String) {
					buffer.append((String) partContent);
				}
			}
		}
	}

	private boolean isMultipartAlternative(Multipart multipart) throws MessagingException {
		String contentType = multipart.getContentType();
		if (contentType != null) {
			ContentType ct = new ContentType(contentType);
			return "multipart".equalsIgnoreCase(ct.getPrimaryType())
					&& "alternative".equalsIgnoreCase(ct.getSubType());
		}

		return false;
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

/*
 * Copyright (c) 2005 - 2008 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.crawler.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.semanticdesktop.aperture.crawler.CrawlReport;
import org.semanticdesktop.aperture.crawler.ExitCode;
import org.semanticdesktop.aperture.util.DateUtil;
import org.semanticdesktop.aperture.util.SimpleSAXAdapter;
import org.semanticdesktop.aperture.util.SimpleSAXParser;
import org.semanticdesktop.aperture.util.XmlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * A trivial implementation of the CrawlReport interface. This class provides additional support for
 * writing the contents of the CrawlReport to a XML file.
 */
public class CrawlReportBase implements CrawlReport {

    /** main CrawlReport XML tag */
    public static final String CRAWL_REPORT_TAG = "crawlReport";

    /** crawl report version XML attribute */
    public static final String VERSION_ATTR = "version";

    /** Crawl started tag - contains the date and time when the crawl has been started */
    public static final String CRAWL_STARTED_TAG = "crawlStarted";

    /** Crawl stopped tag - contains the date and time when the crawl has been stopped */
    public static final String CRAWL_STOPPED_TAG = "crawlStopped";

    /** Exit code tag - contains the exit code */
    public static final String EXIT_CODE_TAG = "exitCode";

    /** constant used to signify a successfully completed crawl */
    public static final String COMPLETED_VALUE = "completed";

    /** constant used to signify a crawl that has been interrupted by the user */
    public static final String STOP_REQUESTED_VALUE = "stopRequested";

    /** constant used to signify a crawl interrupted due to a fatal error */
    public static final String FATAL_ERROR_VALUE = "fatalError";

    /** tag containing the number of new objects */
    public static final String NEW_COUNT_TAG = "newCount";

    /** tag containing the number of changed objects */
    public static final String CHANGED_COUNT_TAG = "changedCount";

    /** tag containing the number of removed objects */
    public static final String REMOVED_COUNT_TAG = "removedCount";

    /** tag containing the number of unchanged objects */
    public static final String UNCHANGED_COUNT_TAG = "unchangedCount";

    private long crawlStarted;

    private long crawlStopped;

    private ExitCode exitCode;

    private int newCount;

    private int changedCount;

    private int removedCount;

    private int unchangedCount;

    /** Default constructor */
    public CrawlReportBase() {
        crawlStarted = -1l;
        crawlStopped = -1l;
        exitCode = null;
        newCount = 0;
        changedCount = 0;
        removedCount = 0;
        unchangedCount = 0;
    }

    public void setCrawlStarted(long crawlStarted) {
        this.crawlStarted = crawlStarted;
    }

    public long getCrawlStarted() {
        return crawlStarted;
    }

    public void setCrawlStopped(long crawlStopped) {
        this.crawlStopped = crawlStopped;
    }

    public long getCrawlStopped() {
        return crawlStopped;
    }

    public void setExitCode(ExitCode exitCode) {
        this.exitCode = exitCode;
    }

    public ExitCode getExitCode() {
        return exitCode;
    }

    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }

    public void increaseNewCount() {
        newCount++;
    }

    public int getNewCount() {
        return newCount;
    }

    public void setChangedCount(int changedCount) {
        this.changedCount = changedCount;
    }

    public void increaseChangedCount() {
        changedCount++;
    }

    public int getChangedCount() {
        return changedCount;
    }

    public void setRemovedCount(int removedCount) {
        this.removedCount = removedCount;
    }

    public void increaseRemovedCount() {
        removedCount++;
    }

    public int getRemovedCount() {
        return removedCount;
    }

    public void setUnchangedCount(int unchangedCount) {
        this.unchangedCount = unchangedCount;
    }

    public void increaseUnchangedCount() {
        unchangedCount++;
    }

    public int getUnchangedCount() {
        return unchangedCount;
    }

    /**
     * Gets the total amount of items processed in the last crawl, i.e. the sum of the number of new,
     * changed and unchanged items.
     */
    public int getTotalCount() {
        return newCount + changedCount + unchangedCount;
    }

    public String toString() {
        return "CrawlReport[crawlStarted=" + crawlStarted + ", crawlStopped=" + crawlStopped + ", exitCode="
                + exitCode + ", newCount=" + newCount + ", changedCount=" + changedCount + ", removedCount="
                + removedCount + ", unchangedCount=" + unchangedCount + "]";
    }

    public void write(OutputStream stream) throws IOException {
        XmlWriter xmlWriter = new XmlWriter(stream);
        xmlWriter.setPrettyPrint(true);

        xmlWriter.startDocument();
        xmlWriter.setAttribute("version", "1.0");
        xmlWriter.startTag(CRAWL_REPORT_TAG);

        if (crawlStarted >= 0l) {
            Date date = new Date(crawlStarted);
            xmlWriter.textElement(CRAWL_STARTED_TAG, DateUtil.dateTime2String(date));
        }
        if (crawlStopped >= 0l) {
            Date date = new Date(crawlStopped);
            xmlWriter.textElement(CRAWL_STOPPED_TAG, DateUtil.dateTime2String(date));
        }

        if (exitCode != null) {
            String value = null;
            if (exitCode.equals(ExitCode.COMPLETED)) {
                value = COMPLETED_VALUE;
            }
            else if (exitCode.equals(ExitCode.STOP_REQUESTED)) {
                value = STOP_REQUESTED_VALUE;
            }
            else if (exitCode.equals(ExitCode.FATAL_ERROR)) {
                value = FATAL_ERROR_VALUE;
            }

            if (value != null) {
                xmlWriter.textElement(EXIT_CODE_TAG, value);
            }
        }

        if (newCount >= 0) {
            xmlWriter.textElement(NEW_COUNT_TAG, String.valueOf(newCount));
        }
        if (changedCount >= 0) {
            xmlWriter.textElement(CHANGED_COUNT_TAG, String.valueOf(changedCount));
        }
        if (removedCount >= 0) {
            xmlWriter.textElement(REMOVED_COUNT_TAG, String.valueOf(removedCount));
        }
        if (unchangedCount >= 0) {
            xmlWriter.textElement(UNCHANGED_COUNT_TAG, String.valueOf(unchangedCount));
        }

        xmlWriter.endTag(CRAWL_REPORT_TAG);
        xmlWriter.endDocument();
    }

    public void read(InputStream stream) throws IOException {
        try {
            // Parse the document
            SimpleSAXParser parser = new SimpleSAXParser();
            parser.setListener(new CrawlReportParser());
            parser.parse(stream);
        }
        catch (ParserConfigurationException e) {
            IOException ie = new IOException(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
        catch (SAXException e) {
            IOException ie = new IOException(e.getMessage());
            ie.initCause(e);
            throw ie;
        }
    }

    private class CrawlReportParser extends SimpleSAXAdapter {

        private Logger logger = LoggerFactory.getLogger(getClass());
        
        public void startTag(String tagName, Map atts, String text) throws SAXException {
            if (text != null && !text.equals("")) {
                try {
                    handleTag(tagName, atts, text);
                }
                catch (Exception e) {
                    throw new SAXException(e);
                }
            }
        }

        private void handleTag(String tagName, Map atts, String text) {
            if (CRAWL_STARTED_TAG.equals(tagName)) {
                try {
                    Date date = DateUtil.string2DateTime(text);
                    crawlStarted = date.getTime();
                }
                catch (ParseException e) {
                    // log and ignore
                    logger.warn("invalid date: " + text, e);
                }
            }
            else if (CRAWL_STOPPED_TAG.equals(tagName)) {
                try {
                    Date date = DateUtil.string2DateTime(text);
                    crawlStopped = date.getTime();
                }
                catch (ParseException e) {
                    // log and ignore
                    logger.warn("invalid date: " + text, e);
                }
            }
            else if (EXIT_CODE_TAG.equals(tagName)) {
                if (COMPLETED_VALUE.equals(text)) {
                    exitCode = ExitCode.COMPLETED;
                }
                else if (STOP_REQUESTED_VALUE.equals(text)) {
                    exitCode = ExitCode.STOP_REQUESTED;
                }
                else if (FATAL_ERROR_VALUE.equals(text)) {
                    exitCode = ExitCode.FATAL_ERROR;
                }
                else {
                    logger.warn("unknown exit code: " + text);
                }
            }
            else if (NEW_COUNT_TAG.equals(tagName)) {
                newCount = parseInt(text, newCount);
            }
            else if (CHANGED_COUNT_TAG.equals(tagName)) {
                changedCount = parseInt(text, changedCount);
            }
            else if (REMOVED_COUNT_TAG.equals(tagName)) {
                removedCount = parseInt(text, removedCount);
            }
            else if (UNCHANGED_COUNT_TAG.equals(tagName)) {
                unchangedCount = parseInt(text, unchangedCount);
            }
        }
        
        private int parseInt(String text, int oldValue) {
            // the old value is passed so that parameters are not altered in case of a NFE
            try {
                return Integer.parseInt(text);
            }
            catch (NumberFormatException e) {
                logger.warn("invalid int: " + text, e);
                return oldValue;
            }
        }
    }
}

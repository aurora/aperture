/*
 * Copyright (c) 2005 - 2008 Deutsches Forschungszentrum fuer Kuenstliche Intelligenz DFKI GmbH.
 * All rights reserved.
 * 
 * Licensed under the Aperture BSD-style license.
 */
package org.semanticdesktop.aperture.outlook;


//
// Auto-Erstellung verwendet JActiveX.EXE 5.00.3601
//   (jactivex /d d:\1\test "C:\Programme\Microsoft Office\Office10\MSOUTL.OLB")
//
// WARNUNG: Entfernen Sie keine Kommentare, die "@com"-Anweisungen enthalten.
// Diese Quelldatei muss kompiliert werden von einem Compiler, der @com verarbeiten kann.
// Falls Sie den Microsoft Visual J++-Compiler verwenden, m\u00fcssen Sie
// Version 1.02.3920 oder h\u00f6her verwenden. \u00c4ltere Versionen werden zwar keinen Fehler melden,
// aber Sie werden keine COM-aktivierten Klassendateien erzeugen.
//

/**
 * Enum: OlObjectClass
 * <p> </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organisation: www.gnowsis.com</p>
 * @author Leo Sauermann leo@gnowsis.com
 * @version $Id$
 */
public interface OlObjectClass
{
    
    /**
     * Recipient type
     */
  public static final int olCC = 2;
  public static final int olTo = 1;  
  public static final int olBCC= 3;
  /**
   * OlMeetingRecipientType
   * recipient type - appointment
   */
  public static final int olOptional = 2;
  public static final int olOrganizer = 0;
  public static final int olRequired = 1;
  public static final int olResource = 3;

  // the rest
  public static final int olApplication = 0;
  public static final int olNamespace = 1;
  public static final int olFolder = 2;
  public static final int olRecipient = 4;
  public static final int olAttachment = 5;
  public static final int olAddressList = 7;
  public static final int olAddressEntry = 8;
  public static final int olFolders = 15;
  public static final int olItems = 16;
  public static final int olRecipients = 17;
  public static final int olAttachments = 18;
  public static final int olAddressLists = 20;
  public static final int olAddressEntries = 21;
  public static final int olAppointment = 26;
  public static final int olMeetingRequest = 53;
  public static final int olMeetingCancellation = 54;
  public static final int olMeetingResponseNegative = 55;
  public static final int olMeetingResponsePositive = 56;
  public static final int olMeetingResponseTentative = 57;
  public static final int olRecurrencePattern = 28;
  public static final int olExceptions = 29;
  public static final int olException = 30;
  public static final int olAction = 32;
  public static final int olActions = 33;
  public static final int olExplorer = 34;
  public static final int olInspector = 35;
  public static final int olPages = 36;
  public static final int olFormDescription = 37;
  public static final int olUserProperties = 38;
  public static final int olUserProperty = 39;
  public static final int olContact = 40;
  public static final int olDocument = 41;
  public static final int olJournal = 42;
  public static final int olMail = 43;
  public static final int olNote = 44;
  public static final int olPost = 45;
  public static final int olReport = 46;
  public static final int olRemote = 47;
  public static final int olTask = 48;
  public static final int olTaskRequest = 49;
  public static final int olTaskRequestUpdate = 50;
  public static final int olTaskRequestAccept = 51;
  public static final int olTaskRequestDecline = 52;
  public static final int olExplorers = 60;
  public static final int olInspectors = 61;
  public static final int olPanes = 62;
  public static final int olOutlookBarPane = 63;
  public static final int olOutlookBarStorage = 64;
  public static final int olOutlookBarGroups = 65;
  public static final int olOutlookBarGroup = 66;
  public static final int olOutlookBarShortcuts = 67;
  public static final int olOutlookBarShortcut = 68;
  public static final int olDistributionList = 69;
  public static final int olPropertyPageSite = 70;
  public static final int olPropertyPages = 71;
  public static final int olSyncObject = 72;
  public static final int olSyncObjects = 73;
  public static final int olSelection = 74;
  public static final int olLink = 75;
  public static final int olLinks = 76;
  public static final int olSearch = 77;
  public static final int olResults = 78;
  public static final int olViews = 79;
  public static final int olView = 80;
  public static final int olItemProperties = 98;
  public static final int olItemProperty = 99;
  public static final int olReminders = 100;
  public static final int olReminder = 101;
}




/*
 * $Log$
 * Revision 1.3  2006/10/20 17:43:00  mylka
 * removed the umlauts from the copyright message.
 *
 * Revision 1.2  2006/02/27 14:36:30  leo_sauermann
 * corrected license: (C) DFKI, OSL 3.0
 *
 * Revision 1.1  2006/02/27 14:05:48  leo_sauermann
 * Implemented First version of Outlook. Added the vocabularyWriter for ease of vocabulary and some launch configs to run it. Added new dependencies (jacob)
 *
 * Revision 1.2  2005/02/02 17:41:26  jshen
 * new outlook adapter with GNOMAIL vocabulary
 *
 * Revision 1.1  2005/01/13 13:16:04  sauermann
 * project restructuring
 *
 * Revision 1.1  2004/11/22 14:43:47  sauermann
 * init
 *
 * Revision 1.4  2004/09/09 15:38:32  kiesel
 * - added CVS tags
 *
 */

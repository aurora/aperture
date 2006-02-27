Microsoft Outlook Test Data
===========================

This is an example file of outlook to code the Outlook crawler.
The crawler code itself is in src/java/org/semanticdesktop/aperture/outlook

Note that you have to include this file with your local outlook installation.


TO RUN THE TESTS
==================

Open Ms-Outlook and add the file src/testdocuments/msoutlook/TestData.pst
to the outlook files.

The test classes will then use the data from this file to query outlook.

You will get a "someone is accessing email addresses" message,
this will hopefully be removed by some fearless hacker someday.
We actually use outlook-redemption to make the signs go away, but
that doesn't work for some parts.

package org.semanticdesktop.aperture.util;

import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Random;

import junit.framework.TestCase;

public class WriterOutputStreamTest extends TestCase {
    
    public void testThreeUtf32CharsAtOnce() throws Exception {
        // this array contains three copies of the 
        // LATIN SMALL LETTER S WITH CARON' (U+0161) unicode character
        // encoded in utf32
        byte [] array = new byte [] {0x00, 0x00, 0x01, 0x61, 0x00, 0x00, 0x01, 0x61, 0x00, 0x00, 0x01, 0x61};
        Writer sw = new StringWriter();
        WriterOutputStream wos = new WriterOutputStream(sw, Charset.forName("UTF-32"));
        wos.write(array);
        assertEquals(sw.toString(), "\u0161\u0161\u0161");
    }
    
    public void testThreeUtf32CharsIrregular() throws Exception {
        Writer sw = new StringWriter();
        WriterOutputStream wos = new WriterOutputStream(sw, Charset.forName("UTF-32"));
        // we should get the same string, regardless of how many calls to write() there were
        // and how do they overlap with the character boundaries
        wos.write(new byte [] {0x00, 0x00, 0x01});
        wos.write(new byte [] {0x61, 0x00, 0x00, 0x01, 0x61, 0x00});
        wos.write(new byte [] {0x00, 0x01, 0x61});
        assertEquals(sw.toString(), "\u0161\u0161\u0161");
    }
    
    public void testUtf32LongRandomString() throws Exception {
        testRandomText(null, 100, 3, Charset.forName("UTF-32"));
    }
    
    public void testUtf16LongRandomString() throws Exception {
        testRandomText(null, 10, 3, Charset.forName("UTF-16"));
    }
    
    public void testUtf32LongRandomStringLongChunks() throws Exception {
        testRandomText(null, 1000, 200, Charset.forName("UTF-32"));
    }
    
    public void testUtf16LongRandomStringLongChunks() throws Exception {
        testRandomText(null, 1000, 200, Charset.forName("UTF-16"));
    }
    
    public void testRandomText(String string, int stringLength, int maxChunkLength, Charset charset) throws Exception {
        // first we generate a string of random utf32 characters
        Random rand = new Random();
        if (string == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < stringLength; i++) {
                int s = 0;
                do {
                    s = rand.nextInt(20000);
                    
                } while (!Character.isDefined(s) || !Character.isValidCodePoint(s));
                buffer.append(Character.toChars(s));
            }
            string = buffer.toString();
        }
        byte [] oldByteArray = string.getBytes(charset);
//        for (int i = 0; i < oldByteArray.length; i++) {
//            System.out.print(Integer.toHexString(oldByteArray[i] >=  0 ? oldByteArray[i] : oldByteArray[i] + 256) + ", ");
//        }
        System.out.println();
        char [] oldCharArray = new char[string.length()];
        string.getChars(0, string.length(), oldCharArray, 0);
//        System.out.println(oldByteArray.length);
        // now we have a byte array containing the random string encoded in the given encoding
        
        // we'll try to reconstruct the string
        StringWriter swr = new StringWriter();
        WriterOutputStream wos = new WriterOutputStream(swr, charset);
        int i = 0;
        while (i < oldByteArray.length) {
            int len = rand.nextInt(maxChunkLength) + 1; 
            if (i + len > oldByteArray.length) {
                len = oldByteArray.length - i;
            }
            wos.write(oldByteArray, i, len);
            i += len;
        }
        char [] newArray = new char[string.length()];
        swr.toString().getChars(0, string.length(), newArray, 0);
        for (i = 0 ; i < oldCharArray.length; i++) {
            if (oldCharArray[i] != newArray[i]) {
//                if (i > 0) {
//                    System.out.println(i-1);
//                    System.out.println(Integer.toHexString(oldCharArray[i-1]));
//                    System.out.println(Integer.toHexString(newArray[i-1]));
//                }
//                System.out.println(i);
//                System.out.println(Integer.toHexString(oldCharArray[i]));
//                System.out.println(Integer.toHexString(newArray[i]));
//                if (i < oldCharArray.length - 1) {
//                    System.out.println(i+1);
//                    System.out.println(Integer.toHexString(oldCharArray[i+1]));
//                    System.out.println(Integer.toHexString(newArray[i+1]));
//                }
            }
        }
        assertEquals(swr.toString(),string);
    }
}

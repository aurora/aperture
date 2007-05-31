/*
 * Copyright (c) 2005 - 2007 Aduna.
 * All rights reserved.
 * 
 * Licensed under the Open Software License version 3.0.
 */
package org.semanticdesktop.aperture.hypertext.linkextractor.html;

import java.util.HashMap;

/**
 * Utility methods for resolving HTML entities.
 */
public class EntityResolver {

    /**
     * A mapping from entity names (Strings) to their decimal code (Strings).
     */
    private static final HashMap ENTITIES;

    /**
     * Replaces all HTML character entities with the characters they represent.
     */
    public static String resolveEntities(String text) {
        StringBuilder buffer = new StringBuilder(text.length() + 20);

        int ampIndex = text.indexOf("&");
        int prevIndex = 0;

        // loop until there are no more entities to escape
        while (ampIndex >= 0) {
            // copy the text from the point where we are now until the next entity
            buffer.append(text.substring(prevIndex, ampIndex));

            // determine the end position of the entity
            int semiColonIndex = text.indexOf(";", ampIndex);
            if (semiColonIndex >= 0) {
                // resolve the entity
                String entityName = text.substring(ampIndex + 1, semiColonIndex);
                String entityValue = resolveEntity(entityName);

                // append the entity value when it has been successfully resolved
                if (entityValue != null) {
                    buffer.append(entityValue);

                    // continue searching for other entities after this entity's semi-colon
                    prevIndex = semiColonIndex + 1;
                }
                else {
                    // no entity has been resolved, continue after the ampersand
                    buffer.append("&");
                    prevIndex = ampIndex + 1;
                }
            }
            else {
                // no entity has been resolved, copy the ampersand and continue right after it
                buffer.append("&");
                prevIndex = ampIndex + 1;
            }

            // search for the next occurrence of an ampersand
            ampIndex = text.indexOf("&", prevIndex);
        }

        // copy the remaining text to the buffer
        buffer.append(text.substring(prevIndex));

        return buffer.toString();
    }

    /**
     * Resolves an entity reference or character reference to its value.
     * 
     * @param entName The 'name' of the reference. This is the string between &amp; and ;, e.g. amp,
     *            quot, #65 or #x41.
     * @return The value of the supplied reference, or null if it could not be resolved.
     */
    public static String resolveEntity(String entName) {
        String result = null;

        // translate named entities to their decimal value
        if (!entName.startsWith("#")) {
            // Named entity? Get its decimal code
            entName = (String) ENTITIES.get(entName);
        }

        // transform decimal or hexadecimal entities to their String representation
        if (entName != null && entName.startsWith("#") && entName.length() > 1) {
            char c;
            
            if (entName.charAt(1) == 'x') {
                // hexadecimal notation
                c = (char) Integer.parseInt(entName.substring(2), 16);
            }
            else {
                // decimal notation
                c = (char) Integer.parseInt(entName.substring(1));
            }

            result = Character.toString(c);
        }

        return result;
    }

    static {
        ENTITIES = new HashMap();

        // Latin1 entities
        ENTITIES.put("nbsp", "#160");
        ENTITIES.put("iexcl", "#161");
        ENTITIES.put("cent", "#162");
        ENTITIES.put("pound", "#163");
        ENTITIES.put("curren", "#164");
        ENTITIES.put("yen", "#165");
        ENTITIES.put("brvbar", "#166");
        ENTITIES.put("sect", "#167");
        ENTITIES.put("uml", "#168");
        ENTITIES.put("copy", "#169");
        ENTITIES.put("ordf", "#170");
        ENTITIES.put("laquo", "#171");
        ENTITIES.put("not", "#172");
        ENTITIES.put("shy", "#173");
        ENTITIES.put("reg", "#174");
        ENTITIES.put("macr", "#175");
        ENTITIES.put("deg", "#176");
        ENTITIES.put("plusmn", "#177");
        ENTITIES.put("sup2", "#178");
        ENTITIES.put("sup3", "#179");
        ENTITIES.put("acute", "#180");
        ENTITIES.put("micro", "#181");
        ENTITIES.put("para", "#182");
        ENTITIES.put("middot", "#183");
        ENTITIES.put("cedil", "#184");
        ENTITIES.put("sup1", "#185");
        ENTITIES.put("ordm", "#186");
        ENTITIES.put("raquo", "#187");
        ENTITIES.put("frac14", "#188");
        ENTITIES.put("frac12", "#189");
        ENTITIES.put("frac34", "#190");
        ENTITIES.put("iquest", "#191");
        ENTITIES.put("Agrave", "#192");
        ENTITIES.put("Aacute", "#193");
        ENTITIES.put("Acirc", "#194");
        ENTITIES.put("Atilde", "#195");
        ENTITIES.put("Auml", "#196");
        ENTITIES.put("Aring", "#197");
        ENTITIES.put("AElig", "#198");
        ENTITIES.put("Ccedil", "#199");
        ENTITIES.put("Egrave", "#200");
        ENTITIES.put("Eacute", "#201");
        ENTITIES.put("Ecirc", "#202");
        ENTITIES.put("Euml", "#203");
        ENTITIES.put("Igrave", "#204");
        ENTITIES.put("Iacute", "#205");
        ENTITIES.put("Icirc", "#206");
        ENTITIES.put("Iuml", "#207");
        ENTITIES.put("ETH", "#208");
        ENTITIES.put("Ntilde", "#209");
        ENTITIES.put("Ograve", "#210");
        ENTITIES.put("Oacute", "#211");
        ENTITIES.put("Ocirc", "#212");
        ENTITIES.put("Otilde", "#213");
        ENTITIES.put("Ouml", "#214");
        ENTITIES.put("times", "#215");
        ENTITIES.put("Oslash", "#216");
        ENTITIES.put("Ugrave", "#217");
        ENTITIES.put("Uacute", "#218");
        ENTITIES.put("Ucirc", "#219");
        ENTITIES.put("Uuml", "#220");
        ENTITIES.put("Yacute", "#221");
        ENTITIES.put("THORN", "#222");
        ENTITIES.put("szlig", "#223");
        ENTITIES.put("agrave", "#224");
        ENTITIES.put("aacute", "#225");
        ENTITIES.put("acirc", "#226");
        ENTITIES.put("atilde", "#227");
        ENTITIES.put("auml", "#228");
        ENTITIES.put("aring", "#229");
        ENTITIES.put("aelig", "#230");
        ENTITIES.put("ccedil", "#231");
        ENTITIES.put("egrave", "#232");
        ENTITIES.put("eacute", "#233");
        ENTITIES.put("ecirc", "#234");
        ENTITIES.put("euml", "#235");
        ENTITIES.put("igrave", "#236");
        ENTITIES.put("iacute", "#237");
        ENTITIES.put("icirc", "#238");
        ENTITIES.put("iuml", "#239");
        ENTITIES.put("eth", "#240");
        ENTITIES.put("ntilde", "#241");
        ENTITIES.put("ograve", "#242");
        ENTITIES.put("oacute", "#243");
        ENTITIES.put("ocirc", "#244");
        ENTITIES.put("otilde", "#245");
        ENTITIES.put("ouml", "#246");
        ENTITIES.put("divide", "#247");
        ENTITIES.put("oslash", "#248");
        ENTITIES.put("ugrave", "#249");
        ENTITIES.put("uacute", "#250");
        ENTITIES.put("ucirc", "#251");
        ENTITIES.put("uuml", "#252");
        ENTITIES.put("yacute", "#253");
        ENTITIES.put("thorn", "#254");
        ENTITIES.put("yuml", "#255");

        // Entities for special characters);
        ENTITIES.put("quot", "#34");
        ENTITIES.put("amp", "#38");
        ENTITIES.put("lt", "#60");
        ENTITIES.put("gt", "#62");
        ENTITIES.put("OElig", "#338");
        ENTITIES.put("oelig", "#339");
        ENTITIES.put("Scaron", "#352");
        ENTITIES.put("scaron", "#353");
        ENTITIES.put("Yuml", "#376");
        ENTITIES.put("circ", "#710");
        ENTITIES.put("tilde", "#732");
        ENTITIES.put("ensp", "#8194");
        ENTITIES.put("emsp", "#8195");
        ENTITIES.put("thinsp", "#8201");
        ENTITIES.put("zwnj", "#8204");
        ENTITIES.put("zwj", "#8205");
        ENTITIES.put("lrm", "#8206");
        ENTITIES.put("rlm", "#8207");
        ENTITIES.put("ndash", "#8211");
        ENTITIES.put("mdash", "#8212");
        ENTITIES.put("lsquo", "#8216");
        ENTITIES.put("rsquo", "#8217");
        ENTITIES.put("sbquo", "#8218");
        ENTITIES.put("ldquo", "#8220");
        ENTITIES.put("rdquo", "#8221");
        ENTITIES.put("bdquo", "#8222");
        ENTITIES.put("dagger", "#8224");
        ENTITIES.put("Dagger", "#8225");
        ENTITIES.put("permil", "#8240");
        ENTITIES.put("lsaquo", "#8249");
        ENTITIES.put("rsaquo", "#8250");
        ENTITIES.put("euro", "#8364");

        // Mathematical, Greek and Symbolic characters for HTML
        ENTITIES.put("fnof", "#402");
        ENTITIES.put("Alpha", "#913");
        ENTITIES.put("Beta", "#914");
        ENTITIES.put("Gamma", "#915");
        ENTITIES.put("Delta", "#916");
        ENTITIES.put("Epsilon", "#917");
        ENTITIES.put("Zeta", "#918");
        ENTITIES.put("Eta", "#919");
        ENTITIES.put("Theta", "#920");
        ENTITIES.put("Iota", "#921");
        ENTITIES.put("Kappa", "#922");
        ENTITIES.put("Lambda", "#923");
        ENTITIES.put("Mu", "#924");
        ENTITIES.put("Nu", "#925");
        ENTITIES.put("Xi", "#926");
        ENTITIES.put("Omicron", "#927");
        ENTITIES.put("Pi", "#928");
        ENTITIES.put("Rho", "#929");
        ENTITIES.put("Sigma", "#931");
        ENTITIES.put("Tau", "#932");
        ENTITIES.put("Upsilon", "#933");
        ENTITIES.put("Phi", "#934");
        ENTITIES.put("Chi", "#935");
        ENTITIES.put("Psi", "#936");
        ENTITIES.put("Omega", "#937");
        ENTITIES.put("alpha", "#945");
        ENTITIES.put("beta", "#946");
        ENTITIES.put("gamma", "#947");
        ENTITIES.put("delta", "#948");
        ENTITIES.put("epsilon", "#949");
        ENTITIES.put("zeta", "#950");
        ENTITIES.put("eta", "#951");
        ENTITIES.put("theta", "#952");
        ENTITIES.put("iota", "#953");
        ENTITIES.put("kappa", "#954");
        ENTITIES.put("lambda", "#955");
        ENTITIES.put("mu", "#956");
        ENTITIES.put("nu", "#957");
        ENTITIES.put("xi", "#958");
        ENTITIES.put("omicron", "#959");
        ENTITIES.put("pi", "#960");
        ENTITIES.put("rho", "#961");
        ENTITIES.put("sigmaf", "#962");
        ENTITIES.put("sigma", "#963");
        ENTITIES.put("tau", "#964");
        ENTITIES.put("upsilon", "#965");
        ENTITIES.put("phi", "#966");
        ENTITIES.put("chi", "#967");
        ENTITIES.put("psi", "#968");
        ENTITIES.put("omega", "#969");
        ENTITIES.put("thetasym", "#977");
        ENTITIES.put("upsih", "#978");
        ENTITIES.put("piv", "#982");
        ENTITIES.put("bull", "#8226");
        ENTITIES.put("hellip", "#8230");
        ENTITIES.put("prime", "#8242");
        ENTITIES.put("Prime", "#8243");
        ENTITIES.put("oline", "#8254");
        ENTITIES.put("frasl", "#8260");
        ENTITIES.put("weierp", "#8472");
        ENTITIES.put("image", "#8465");
        ENTITIES.put("real", "#8476");
        ENTITIES.put("trade", "#8482");
        ENTITIES.put("alefsym", "#8501");
        ENTITIES.put("larr", "#8592");
        ENTITIES.put("uarr", "#8593");
        ENTITIES.put("rarr", "#8594");
        ENTITIES.put("darr", "#8595");
        ENTITIES.put("harr", "#8596");
        ENTITIES.put("crarr", "#8629");
        ENTITIES.put("lArr", "#8656");
        ENTITIES.put("uArr", "#8657");
        ENTITIES.put("rArr", "#8658");
        ENTITIES.put("dArr", "#8659");
        ENTITIES.put("hArr", "#8660");
        ENTITIES.put("forall", "#8704");
        ENTITIES.put("part", "#8706");
        ENTITIES.put("exist", "#8707");
        ENTITIES.put("empty", "#8709");
        ENTITIES.put("nabla", "#8711");
        ENTITIES.put("isin", "#8712");
        ENTITIES.put("notin", "#8713");
        ENTITIES.put("ni", "#8715");
        ENTITIES.put("prod", "#8719");
        ENTITIES.put("sum", "#8721");
        ENTITIES.put("minus", "#8722");
        ENTITIES.put("lowast", "#8727");
        ENTITIES.put("radic", "#8730");
        ENTITIES.put("prop", "#8733");
        ENTITIES.put("infin", "#8734");
        ENTITIES.put("ang", "#8736");
        ENTITIES.put("and", "#8743");
        ENTITIES.put("or", "#8744");
        ENTITIES.put("cap", "#8745");
        ENTITIES.put("cup", "#8746");
        ENTITIES.put("int", "#8747");
        ENTITIES.put("there4", "#8756");
        ENTITIES.put("sim", "#8764");
        ENTITIES.put("cong", "#8773");
        ENTITIES.put("asymp", "#8776");
        ENTITIES.put("ne", "#8800");
        ENTITIES.put("equiv", "#8801");
        ENTITIES.put("le", "#8804");
        ENTITIES.put("ge", "#8805");
        ENTITIES.put("sub", "#8834");
        ENTITIES.put("sup", "#8835");
        ENTITIES.put("nsub", "#8836");
        ENTITIES.put("sube", "#8838");
        ENTITIES.put("supe", "#8839");
        ENTITIES.put("oplus", "#8853");
        ENTITIES.put("otimes", "#8855");
        ENTITIES.put("perp", "#8869");
        ENTITIES.put("sdot", "#8901");
        ENTITIES.put("lceil", "#8968");
        ENTITIES.put("rceil", "#8969");
        ENTITIES.put("lfloor", "#8970");
        ENTITIES.put("rfloor", "#8971");
        ENTITIES.put("lang", "#9001");
        ENTITIES.put("rang", "#9002");
        ENTITIES.put("loz", "#9674");
        ENTITIES.put("spades", "#9824");
        ENTITIES.put("clubs", "#9827");
        ENTITIES.put("hearts", "#9829");
        ENTITIES.put("diams", "#9830");
    }
}

/**
 * Copyright (c) Gunnar Aastrand Grimnes, DFKI GmbH, 2008. 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the distribution.
 *  * Neither the name of the DFKI GmbH nor the names of its contributors may be used to endorse or promote products derived from 
 *    this software without specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 *    BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 *    SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 *    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS  
 *    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE  
 *    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.semanticdesktop.demork;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticdesktop.demork.database.Cell;
import org.semanticdesktop.demork.database.Database;
import org.semanticdesktop.demork.database.Row;
import org.semanticdesktop.demork.database.Table;

/**
 * @author ggrimnes
 *
 */
public class Demork {
	
	Logger log=Logger.getLogger(Demork.class.getName());

	
	private Charset charset;
	
	static private Hashtable<String, String> backslash = new Hashtable<String, String>();
	static {
		backslash.put("\\\\", "\\");
		backslash.put("\\$", "$");
		backslash.put("\\0", chr(0));
		backslash.put("\\a", chr(7));
		backslash.put("\\b", chr(8));
		backslash.put("\\t", chr(9));
		backslash.put("\\n", chr(10));
		backslash.put("\\v", chr(11));
		backslash.put("\\f", chr(12));
		backslash.put("\\r", chr(13));
	}
	
	/**
	 * Return the encoding of the file, as a string, f.x. iso-8859-1
	 * @param infile - a mork file
	 * @return return - the encoding of the given mork file
	 */
	public String getEncoding(String infile) throws Exception {
		FileInputStream fis=new FileInputStream(new File(infile));
		
		byte[] bytes=new byte[150];
		
		fis.read(bytes);
		
		fis.close();
		
		String header=new String(bytes);
		return getEncodingFromHeader(header);
	}
	
	private String getEncodingFromHeader(String header) throws Exception { 
//		System.err.println(header);
		Matcher m = Pattern.compile("\\(f=(.*)\\)").matcher(header);
		//Matcher m = Pattern.compile("\\(f=([\\d\\w-]+)\\)").matcher(header);
		if (m==null || !m.find()) throw new Exception("Could not find encoding info in mork header.");
		return m.group(1);
	}
	

	public Database inputMork(String data) {
		// Remove beginning comment
		data=data.replaceFirst("//.*","");
		
		try {
			charset=Charset.forName(getEncodingFromHeader(data));
		} catch (Exception e) {
			//no much we can do
			charset=Charset.defaultCharset();
		}
		
		// Remove line continuation backslashes
		data=data.replaceAll("(\\\\(?:\\r|\\n))","");

		// Remove line termination
		data=data.replaceAll("(\\n\\s*)|(\\r\\s*)|(\\r\\n\\s*)","");

		Database db = new Database();

		// Compile the appropriate regular expressions
		Pattern pSpace      = Pattern.compile("^\\s+");
		Pattern pColumnDict = Pattern.compile("^<\\s*<\\(a=c\\)>\\s*(?:\\/\\/)?\\s*(\\(.+?\\))\\s*>");
		Pattern pCell       = Pattern.compile("(\\(.+?\\))");
		Pattern pAtomDict   = Pattern.compile("^<\\s*(\\(.+?\\))\\s*>");
		Pattern pTranBegin  = Pattern.compile("^@\\$\\$\\{.+?\\{\\@");
	    Pattern pTranEnd    = Pattern.compile("^@\\$\\$\\}.+?\\}\\@");
	    Pattern pTable      = Pattern.compile("^\\{-?(\\d+):\\^(..)\\s*\\{\\(k\\^(..):c\\)\\(s=9u?\\)\\s*(.*?)\\}\\s*(.+?)\\}");
	    Pattern pRow        = Pattern.compile("(-?)\\s*\\[(.+?)((\\(.+?\\)\\s*)*)\\]");
		/*
		Pattern pCell       = re.compile(r'(\(.+?\))')
		Pattern pSpace      = re.compile(r'\s+')
		    pColumnDict = re.compile(r'<\s*<\(a=c\)>\s*(?:\/\/)?\s*(\(.+?\))\s*>')
		    pAtomDict   = re.compile(r'<\s*(\(.+?\))\s*>')
		    pTable      = re.compile(r'\{-?(\d+):\^(..)\s*\{\(k\^(..):c\)\(s=9u?\)\s*(.*?)\}\s*(.+?)\}')
		    pRow        = re.compile(r'(-?)\s*\[(.+?)((\(.+?\)\s*)*)\]')

		    pTranBegin  = re.compile(r'@\$\$\{.+?\{\@')
		    pTranEnd    = re.compile(r'@\$\$\}.+?\}\@')

		    # Escape all '%)>}]' characters within () cells
		 */
	    data=myEscapedata(data);
		//data=data.replaceAll("(\\(.+?\\))",data);

		// Iterate through the data
		int index  = 0;
		int length = data.length();
		Matcher match  = null;
		boolean tran   = false;
		while (true) {
			if (match!=null) index += match.group().length();
		    
			if (index >= length) break;
			
			String sub = data.substring(index);

		    // Skip whitespace
		    match = pSpace.matcher(sub);
		    if (match.find()) {
		    		index += match.group().length();
		        continue;
		    } else {
				match = null;
			}

			// Parse a column dictionary
			match = pColumnDict.matcher(sub);
			if (match.find()) {
				List<String> m = findall(pCell, match.group());
				// Remove extraneous '(f=iso-8859-1)'
				if (m.size() >= 2 && m.get(1).indexOf("(f=") == 0)
					m = m.subList(1, m.size());
				if (m.size() > 1)
					addToDict(db.cdict, m.subList(1, m.size()));
				continue;
			} else {
				match = null;
			} 
	            
	            
	        // Parse an atom dictionary
	        match = pAtomDict.matcher(sub);
	        if (match.find()) {
	        		addToDict(db.adict, findall(pCell,match.group()));
	            continue;
	        } else { match=null; } 

	        // Parse a table
	        match = pTable.matcher(sub);
	        if (match.find()) { 
	            String id = match.group(1) + ':' + match.group(2);

	            Table table;
	            if (db.tables.containsKey(id)) {
	                table = db.tables.get(id);
	            } else {
	                table = new Table();
	                table.id    = match.group(1);
	                table.scope = db.cdict.get(match.group(2));
	                table.kind  = db.cdict.get(match.group(3));
	                db.tables.put(id,table);
	            }

	            Vector<Vector<String>> rows = findallM(pRow,match.group());
	            for (Vector<String> row: rows) {
	                Vector<String> cells = findall(pCell,row.get(2));
	                String rowid = row.get(1);
	                if (tran && rowid.equals("-")) {
	                		rowid = rowid.substring(1,rowid.length());
	                    delRow(db, db.tables.get(id), rowid);
	                }
	                //TODO: hmm - charAt vs. get
	                if (tran && !rowid.equals("-")) {
	                } else {
	                    addRow(db, db.tables.get(id), rowid, cells);
	                }
	            }
	            continue;
	        } else { match=null; } 
	            
	        // Transaction support
	        match = pTranBegin.matcher(sub);
	        if (match.find()) {
	            tran = true;
	            continue;
	        } else { match=null; } 

	        match = pTranEnd.matcher(sub);
	        if (match.find()) {
	            tran = false;
	            continue;
	        } else { match=null; } 

	            
	        match = pRow.matcher(sub);
	        if (match.find() && tran) {
	            //System.err.println("WARNING: using table '1:^80' for dangling row: "+match.group());
	            String rowid = match.group(2);
	            if (rowid.charAt(0) == '-')
	                rowid = rowid.substring(1,rowid.length());

	            Vector<String> cells = findall(pCell,match.group(3));
	            delRow(db, db.tables.get("1:80"), rowid);
	            //TOOD: This was
	            //if (row.charAt(0) != '-')
	            // in the python code, i'll guess they meant rowid
	            if (rowid.charAt(0) != '-')
	                addRow(db, db.tables.get("1:80"), rowid, cells);
	            continue;
	        } else { match=null; } 	

	        //# Syntax error
	        log.warning("ERROR: syntax error while parsing MORK file: context["+index+"]: "+sub.substring(0,Math.min(sub.length(),40)));
	        index += 1;	   
		}
		    // Return the database
		    return db;

	}

	/**
	 * @param db
	 * @param table
	 * @param rowid
	 * @param cells
	 */
	private void addRow(Database db, Table table, String rowid, Vector<String> cells) {
		//global pCellText
	    //global pCellOid

		Pattern pCellText   = Pattern.compile("\\^(.+?)=(.*)");
		Pattern pCellOid    = Pattern.compile("\\^(.+?)\\^(.+)");
		/*
		pCellText   = re.compile(r'\^(.+?)=(.*)')
		pCellOid    = re.compile(r'\^(.+?)\^(.+)')
		pCellEscape = re.compile(r'((?:\\[\$\0abtnvfr])|(?:\$..))')
		*/
	    Row row = new Row();
	    String[] hack = getRowIdScope(rowid, db.cdict);
	    row.id=hack[0];
	    row.scope=hack[1];

	    for (String cell: cells) {
	        Cell obj = new Cell();
	        cell = cell.substring(1,cell.length()-1);

	        Matcher match = pCellText.matcher(cell);
	        if (match.find()) {
	            obj.column = db.cdict.get(match.group(1));
	            obj.atom   = decodeMorkValue(match.group(2));
	        } else {
	            match = pCellOid.matcher(cell);
	            if (match.find()) {
	                obj.column = db.cdict.get(match.group(1));
	                obj.atom   = db.adict.get(match.group(2));
	            }
	                                     
	        }

	        if (obj.column!=null && obj.atom!=null)
	            row.cells.add(obj);
	    }
	    
	    String rowkey;
		if (row.scope!=null) 
	        rowkey = row.id + "/" + row.scope;
	    else 
	        rowkey = row.id + "/" + table.scope;

	    if (table.rows.containsKey(rowkey)) {
	    	log.warning("ERROR: duplicate rowid/scope "+rowkey+": "+cells);
	    }
	    table.rows.put(rowkey,row);
	}

	/**
	 * @param db
	 * @param table
	 * @param rowid
	 */
	private void delRow(Database db, Table table, String rowid) {
		String[] hack = getRowIdScope(rowid, db.cdict);
	    rowid=hack[0];
		String scope=hack[1];
		String rowkey;
	    if (scope!=null)
	        rowkey = rowid + "/" + scope;
	    else 
	        rowkey = rowid + "/" + table.scope;

	    if (table.rows.containsKey(rowkey))
	        table.rows.remove(rowkey);
	}

	/**
	 * @param rowid
	 * @param cdict
	 * @return
	 */
	private String[] getRowIdScope(String rowid, Hashtable<String, String> cdict) {
		int idx = rowid.indexOf(':');
	    if (idx > 0)
	        return new String[] {rowid.substring(0,idx), cdict.get(rowid.substring(idx+2,rowid.length()))};
	    else
	        return new String[] {rowid, null};
	}

	/**
	 * @param row
	 * @param string
	 * @return
	 */
	private Vector<Vector<String>> findallM(Pattern cell, String string) {
		Vector<Vector<String>> res=new Vector<Vector<String>>();
		Matcher m=cell.matcher(string);
		while (m.find()) {
			Vector<String> row=new Vector<String>();
			//skip first group to be consistent with python
			for (int i=1;i<m.groupCount();i++) {
				row.add(m.group(i));	
			}
			res.add(row);
		}
		return res;
	}

	private  void addToDict(Hashtable cdict, List<String> name) {
		for (String cell : name) {
	        int eq  = cell.indexOf('=');
	        String key = cell.substring(1,eq);
	        String val = cell.substring(eq+1,cell.length()-1);
	        cdict.put(key,decodeMorkValue(val));
		}
	}

	
	private String myEscapedata(String value) {
		Pattern p=Pattern.compile("(\\(.+?\\))");
		Hashtable<String,String> replace=new Hashtable<String,String>();
		
		Matcher m=p.matcher(value);
		while (m.find()) {
			String t=m.group();
			String e=escapeData(t);
			if (!t.equals(e))
				replace.put(t,e);
		}
		//This is meant to be:  return pCellEscape.sub(unescapeMork, value) where unesacpe isa function.
		for (String k: replace.keySet()) {
			//System.err.println("Replacing "+k+" with "+replace.get(k));
			value=value.replace(k,replace.get(k));
		}
		return value;
	}
	
	/**
	 * @param m
	 * @return
	 */
	private String escapeData(String m) {
		return m.replace("\\\\n", "$0A") 
            .replace("\\)", "$29") 
            .replace(">", "$3E") 
            .replace("}", "$7D") 
            .replace("]", "$5D");
	}

	private  String decodeMorkValue(String value) {
		Pattern pCellEscape = Pattern.compile("((?:\\\\[\\$\0abtnvfr])|(?:\\$..\\$..))");
		Hashtable<String,String> replace=new Hashtable<String,String>();
		
		Matcher m=pCellEscape.matcher(value);
		while (m.find()) {
			String s=m.group();
			String t=unescapeMork(s);
			if (!s.equals(t))
				replace.put(s,t);
		}
		//This is meant to be:  return pCellEscape.sub(unescapeMork, value) where unesacpe isa function.
		for (String k: replace.keySet()) {
			value=value.replace(k,replace.get(k));
		}
		return value;
	}

	
	private  String unescapeMork(String s) {
	    if (s.charAt(0) == '\\')
	        return backslash.get(s);
	    else {
	    		//System.err.println("Unescape: "+s.substring(1,Math.min(s.length(),10)));
	    		//System.err.println(s.substring(1,3)+" - "+s.substring(4,6));
	    		int i=Integer.parseInt(s.substring(1,3), 16);
	    		int i2=Integer.parseInt(s.substring(4,6),16);
	        return chr(i,i2,charset);
	    }
	}

	/**
	 * @param i
	 * @param i2
	 * @param charset2
	 * @return
	 */
	private String chr(int i, int i2, Charset charset2) {
		ByteBuffer bytes=ByteBuffer.allocate(2);
		bytes.put((byte)i);
		bytes.put((byte)i2);
		charset2=Charset.forName("utf-8");
		String res=new String(charset2.decode(bytes).array());
		return res;
	}

	private static String chr(int i){
		return Character.toString(Character.forDigit(i,10));
	}

	private Vector<String> findall(Pattern cell, String string) {
		Vector<String> res=new Vector<String>();
		Matcher m=cell.matcher(string);
		while (m.find()) {
			res.add(m.group());
		}
		return res;
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: java Demork <morkfile>");
			System.exit(-1);
		}
		
		Demork demork = new Demork();

		String filename = args[0];

		String encoding=demork.getEncoding(filename);
		
		String data = Utils.readWholeFileAsEncoding(filename,encoding);

		// Determine the file type and process accordingly
		if (data.indexOf("<mdb:mork") != -1) {
			
			System.err.println("parsing...");
			Database db = demork.inputMork(data);
			System.err.println("Result:");
			System.err.println(XMLOut.outPut(db));
		} else {
			System.err.println("unknown file format: " + filename
					+ " (I only deal with Mork, sorry)");
			System.exit(-1);
		}
	}

}

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
 *    
 *    Adapted from the Jena, as detailed below.
 */
/******************************************************************
 * File:        Rule.java
 * Created by:  Dave Reynolds
 * Created on:  29-Mar-03
 * 
 * (c) Copyright 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
 * [See end of file]
 * $Id: Rule.java,v 1.41 2006/03/22 13:52:20 andy_seaborne Exp $
 *****************************************************************/
package org.semanticdesktop.nepomuk.nrl.inference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.XMLSchema;
import org.semanticdesktop.nepomuk.nrl.inference.exceptions.ReasonerException;
import org.semanticdesktop.nepomuk.nrl.inference.exceptions.RulesetNotFoundException;
import org.semanticdesktop.nepomuk.nrl.inference.exceptions.WrappedIOException;
import org.semanticdesktop.nepomuk.nrl.inference.model.Builtin;
import org.semanticdesktop.nepomuk.nrl.inference.model.ClauseEntry;
import org.semanticdesktop.nepomuk.nrl.inference.model.Functor;
import org.semanticdesktop.nepomuk.nrl.inference.model.TriplePattern;
import org.semanticdesktop.nepomuk.nrl.inference.model.Variable;
import org.semanticdesktop.nepomuk.nrl.inference.utils.PrefixMapping;
import org.semanticdesktop.nepomuk.nrl.inference.utils.Tokenizer;
import org.semanticdesktop.nepomuk.nrl.inference.utils.Util;


/**Representation of a generic inference rule. 
 * <p>
 * This represents the rule specification but most engines will 
 * compile this specification into an abstract machine or processing
 * graph. </p>
 * <p>
 * The rule specification comprises a list of antecendents (body) and a list
 * of consequents (head). If there is more than one consequent then a backchainer
 * should regard this as a shorthand for several rules, all with the
 * same body but with a singleton head. </p>
 * <p>
 * Each element in the head or body can be a TriplePattern, a Functor or a Rule.
 * A TriplePattern is just a triple of Nodes but the Nodes can represent
 * variables, wildcards and embedded functors - as well as constant uri or
 * literal graph nodes. A functor comprises a functor name and a list of 
 * arguments. The arguments are Nodes of any type except functor nodes
 * (there is no functor nesting). The functor name can be mapped into a registered
 * java class that implements its semantics. Functors play three roles -
 * in heads they represent actions (procedural attachement); in bodies they
 * represent builtin predicates; in TriplePatterns they represent embedded
 * structured literals that are used to cache matched subgraphs such as
 * restriction specifications. </p>
 *<p>
 * We include a trivial, recursive descent parser but this is just there
 * to allow rules to be embedded in code. External rule syntax based on N3
 * and RDF could be developed. The embedded syntax supports rules such as:
 * <blockindent>    
 * <code>[ (?C rdf:type *), guard(?C, ?P)  -> (?c rb:restriction some(?P, ?D)) ].</code><br />
 * <code>[ (?s owl:foo ?p) -> [ (?s owl:bar ?a) -> (?s ?p ?a) ] ].</code><br />
 * <code>[name: (?s owl:foo ?p) -> (?s ?p ?a)].</code><br />
 * </blockindent>
 * only built in namespaces are recognized as such, * is a wildcard node, ?c is a variable, 
 * name(node ... node) is a functor, (node node node) is a triple pattern, [..] is an
 * embedded rule, commas are ignore and can be freely used as separators. Functor names
 * may not end in ':'.
 * </p>
 * @author <a href="mailto:der@hplb.hpl.hp.com">Dave Reynolds</a> * @version $Revision: 1.41 $ on $Date: 2006/03/22 13:52:20 $ 
 */
public class Rule implements ClauseEntry {
    
//=======================================================================
// variables

    private static final String RDFS_RULES = null;

	/** Rule body */
    protected ClauseEntry[] body;
    
    /** Rule head or set of heads */
    protected ClauseEntry[] head;
    
    /** Optional name for the rule */
    protected String name;
    
    /** The number of distinct variables used in the rule */
    protected int numVars = -1;
    
    /** Flags whether the rule was written as a forward or backward rule */
    protected boolean isBackward = false;
    
    /** Flags whether the rule is monotonic */
    protected boolean isMonotonic = true;
    
    static Logger logger = Logger.getLogger(Rule.class.getName());

	private static List<Rule> rdfsrules;

    
    /**
     * Constructor
     * @param body a list of TriplePatterns or Functors.
     * @param head a list of TriplePatterns, Functors or rules
     */
    public Rule(List<ClauseEntry> head, List<ClauseEntry> body) {
        this(null, head, body);
    }
    
    /**
     * Constructor
     * @param name a label for rule
     * @param body a list of TriplePatterns or Functors.
     * @param head a list of TriplePatterns, Functors or rules
     */
    public Rule(String name, List<ClauseEntry> head, List<ClauseEntry> body) {
        this(name, 
                head.toArray(new ClauseEntry[head.size()]),
                body.toArray(new ClauseEntry[body.size()]) );
    }
    
    /**
     * Constructor
     * @param name a label for rule
     * @param body an array of TriplePatterns or Functors.
     * @param head an array of TriplePatterns, Functors or rules
     */
    public Rule(String name, ClauseEntry[] head, ClauseEntry[] body) {
        this.name = name;
        this.head = head;
        this.body = body;
        this.isMonotonic = allMonotonic(head);
    }
    
    // Compute the monotonicity flag
    // Future support for negation would affect this
    private boolean allMonotonic(ClauseEntry[] elts) {
        for (int i = 0; i < elts.length; i++) {
            ClauseEntry elt = elts[i];
            if (elt instanceof Functor) {
                Builtin b = ((Functor)elt).getImplementor();
                if (b != null) {
                    if (! b.isMonotonic() ) return false;
                } else {
                    throw new ReasonerException("Undefined Functor " + ((Functor)elt).getName() +" in " + toShortString());
                }
            }
        }
        return true;
    }
    
//=======================================================================
// accessors

    /**
     * Return the number of body elements
     */
    public int bodyLength() {
        return body.length;
    }
    
    /**
     * Return the n'th body element
     */
    public ClauseEntry getBodyElement(int n) {
        return body[n];
    }
    
    /**
     * return the entire rule body as an array of objects
     */
    public ClauseEntry[] getBody() {
        return body;
    }
        
    
    /**
     * Return the number of head elements
     */
    public int headLength() {
        return head.length;
    }
    
    /**
     * Return the n'th head element
     */
    public ClauseEntry getHeadElement(int n) {
        return head[n];
    }
    
    /**
     * return the entire rule head as an array of objects
     */
    public ClauseEntry[] getHead() {
        return head;
    }
    
    /**
     * Return true if the rule was written as a backward (as opposed to forward) rule.
     */
    public boolean isBackward() {
        return isBackward;
    }
    
    /**
     * Set the rule to be run backwards.
     * @param flag if true the rule should run backwards.
     */
    public void setBackward(boolean flag) {
        isBackward = flag;
    }
    
    /**
     * Get the name for the rule - can be null.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the number of distinct variables for this rule.
     * Used internally when cloing rules, not normally required.
     */
    public void setNumVars(int n) {
        numVars = n;
    }
    
    /**
     * Return the number of distinct variables in the rule. Or more precisely, the
     * size of a binding environment needed to represent the rule.
     */
    public int getNumVars() {
        if (numVars == -1) {
            // only have to do this if the rule was generated programatically
            // the parser will have prefilled this in for normal rules
            int max = findVars(body, -1);
            max = findVars(head, max);
            numVars = max + 1;
        }
        return numVars;
    }
    
    /**
     * Find all the variables in a clause array.
     */
    private int findVars(Object[] nodes, int maxIn) {
        int max = maxIn;
        for (int i = 0; i < nodes.length; i++) {
            Object node = nodes[i];
            if (node instanceof TriplePattern) {
                max = findVars((TriplePattern)node, max);
            } else {
                max = findVars((Functor)node, max); 
            }
        }
        return max;
    }
    
    /**
     * Find all the variables in a TriplePattern.
     */
    private int findVars(TriplePattern t, int maxIn) {
        int max = maxIn;
        max = maxVarIndex(t.getSubject(), max);
        max = maxVarIndex(t.getPredicate(), max);
        Value obj = t.getObject();
        if (obj instanceof Variable) {
            max = maxVarIndex(obj, max);
        } else if (Functor.isFunctor(obj)) {
            max = findVars(((Functor)obj), max);
        }
        return max;
    }
        
    /**
     * Find all the variables in a Functor.
     */
    private int findVars(Functor f, int maxIn) {
        int max = maxIn;
        Value[] args = f.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Variable) max = maxVarIndex(args[i], max);
        }
        return max;
    }
    
    /**
     * Return the maximum Value index of the variable and the max so far. 
     */
    private int maxVarIndex(Value var, int max) {
        if (var instanceof Variable) {
            int index = ((Variable)var).index;
            if (index > max) return index;            
        }
        return max;
    }
    
    /**
     * Instantiate a rule given a variable binding environment.
     * This will clone any non-bound variables though that is only needed
     * for trail implementations.
     */
    public Rule instantiate(BindingEnvironment env) {
        HashMap<Variable, Value> vmap = new HashMap<Variable, Value>();
        return new Rule(name, cloneClauseArray(head, vmap, env), cloneClauseArray(body, vmap, env));
    }
    
    /**
     * Clone a rule, cloning any embedded variables.
     */
    public Rule cloneRule() {
        if (getNumVars() > 0) {
            HashMap<Variable, Value> vmap = new HashMap<Variable, Value>();
            return new Rule(name, cloneClauseArray(head, vmap, null), cloneClauseArray(body, vmap, null));
        } else {
            return this;
        }
    }
    
    /**
     * Clone a clause array.
     */
    private ClauseEntry[] cloneClauseArray(ClauseEntry[] clauses, Map<Variable, Value> vmap, BindingEnvironment env) {
        ClauseEntry[] cClauses = new ClauseEntry[clauses.length];
        for (int i = 0; i < clauses.length; i++ ) {
            cClauses[i] = cloneClause(clauses[i], vmap, env);
        }
        return cClauses;
    }
    
    /**
     * Clone a clause, cloning any embedded variables.
     */
    private ClauseEntry cloneClause(ClauseEntry clause, Map<Variable, Value> vmap, BindingEnvironment env) {
        if (clause instanceof TriplePattern) {
            TriplePattern tp = (TriplePattern)clause;
            return new TriplePattern (
                            cloneValue(tp.getSubject(), vmap, env),
                            cloneValue(tp.getPredicate(), vmap, env),
                            cloneValue(tp.getObject(), vmap, env)
                        );
        } else {
            return cloneFunctor((Functor)clause, vmap, env);
        }
    }
    
    /**
     * Clone a functor, cloning any embedded variables.
     */
    private Functor cloneFunctor(Functor f, Map<Variable, Value> vmap, BindingEnvironment env) {
        Value[] args = f.getArgs();
        Value[] cargs = new Value[args.length];
        for (int i = 0; i < args.length; i++) {
            cargs[i] = cloneValue(args[i], vmap, env);
        }
        Functor fn = new Functor(f.getName(), cargs);
        fn.setImplementor(f.getImplementor());
        return fn;
    }
    
    /**
     * Close a single Value.
     */
    private Value cloneValue(Value nIn, Map<Variable, Value> vmap, BindingEnvironment env) {
        Value n = (env == null) ? nIn : env.getBinding(nIn);
        if (n instanceof Variable) {
        	Variable nv = (Variable)n;
            Value c = vmap.get(nv);
            if (c == null) {
                c = ((Variable)n).cloneValue();
                vmap.put(nv, c);
            }
            return c;
        } else if (Functor.isFunctor(n)) {
            Functor f = ((Functor)n);
            return Functor.makeFunctorValue(cloneFunctor(f, vmap, env));
        } else {
            return n;
        }
    }
    
    /**
     * Returns false for rules which can affect other rules non-monotonically (remove builtin
     * or similar) or are affected non-monotonically (involve negation-as-failure).
     */
    public boolean isMonotonic() {
        return isMonotonic;
    }
    
    /**
     * Returns true if the rule does not depend on any data, and so should 
     * be treated as an axiom.
     */
    public boolean isAxiom() {
    	// While we only have triplePattern this is ok: 
    	return body.length==0; 
        /*for (int i = 0; i < body.length; i++) {
            if (body[i] instanceof TriplePattern) {
                return false;
            }
    	return true;
        }*/
    	
    }
    
    /**
     * Printable string describing the rule
     */
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("[ ");
        if (name != null) {
            buff.append(name);
            buff.append(": ");
        }
        if (isBackward) { 
            for (int i = 0; i < head.length; i++) {
                buff.append(head[i]);
                buff.append(" ");
            }
            buff.append("<- ");
            for (int i = 0; i < body.length; i++) {
                buff.append(body[i]);
                buff.append(" ");
            }
        } else {
            for (int i = 0; i < body.length; i++) {
                buff.append(body[i]);
                buff.append(" ");
            }
            buff.append("-> ");
            for (int i = 0; i < head.length; i++) {
                buff.append(head[i]);
                buff.append(" ");
            }
        }
        buff.append("]");
        return buff.toString();
    }
    
    /**
     * Print a short description of the rule, just its name if it
     * has one, otherwise the whole rule description.
     */
    public String toShortString() {
        if (name != null) {
            return name;
        } else {
            return toString();
        }
    }
    
//=======================================================================
// parser access

    /**
     * Parse a string as a rule.
     * @throws ParserException if there is a problem
     */
    public static Rule parseRule(String source) throws ParserException {
        Parser parser = new Parser(source);
        return parser.parseRule();
    }
    
    /**
     * Answer the list of rules parsed from the given URL.
     * @throws RulesetNotFoundException
     */
    public static List<Rule> rulesFromURL( String uri ) {
        try {
            BufferedReader br = Util.readerFromURL( uri );
            return parseRules( Rule.rulesParserFromReader( br ) );
        }
        catch (WrappedIOException e)
            { throw new RulesetNotFoundException( uri ); }
    }
    
    /**
    Answer a String which is the concatenation (with newline glue) of all the
    non-comment lines readable from <code>src</code>. A comment line is
    one starting "#" or "//".
    @deprecated Use rulesParserFromReader
    */
    public static String rulesStringFromReader( BufferedReader src ) {
       try {
           StringBuffer result = new StringBuffer();
           String line;
           while ((line = src.readLine()) != null) {
               if (line.startsWith( "#" ) || line.startsWith( "//" )) continue;     // Skip comment lines
               result.append( line );
               result.append( "\n" );
           }
           return result.toString();
       }
       catch (IOException e) 
           { throw new WrappedIOException( e ); }
   }
    
    /**
     * Processes the source reader stripping off comment lines and noting prefix
     * definitions (@prefix) and rule inclusion commands (@include).
     * Returns a parser which is bound to the stripped source text with 
     * associated prefix and rule inclusion definitions.
    */
    public static Parser rulesParserFromReader( BufferedReader src ) {
       try {
           StringBuffer result = new StringBuffer();
           String line;
           Map<String,String> prefixes = new HashMap<String,String>();
           List<Rule> preloadedRules = new ArrayList<Rule>();
           while ((line = src.readLine()) != null) {
               if (line.startsWith("#")) continue;     // Skip comment lines
               line = line.trim();
               if (line.startsWith("//")) continue;    // Skip comment lines
               if (line.startsWith("@prefix")) {
                   line = line.substring("@prefix".length());
                   String prefix = nextArg(line);
                   String rest = nextAfterArg(line);
                   if (prefix.endsWith(":")) prefix = prefix.substring(0, prefix.length() - 1);
                   String url = extractURI(rest);
                   prefixes.put(prefix, url);

               } else if (line.startsWith("@include")) {
                   // Include referenced rule file, either URL or local special case
                   line = line.substring("@include".length());
                   String url = extractURI(line);
                   // Check for predefined cases
                   if (url.equalsIgnoreCase("rdfs")) {
                       preloadedRules.addAll( loadRDFSRules() );
                   } else {
                       // Just try loading as a URL
                       preloadedRules.addAll( rulesFromURL(url) );
                   }

               } else {
                   result.append(line);
                   result.append("\n");
               }
           }
           Parser parser = new Parser(result.toString());
           parser.registerPrefixMap(prefixes);
           parser.addRulesPreload(preloadedRules);
           return parser;
       }
       catch (IOException e) 
           { throw new WrappedIOException( e ); }
   }

    private static Collection<Rule> loadRDFSRules() {
    	if (rdfsrules==null) {
    		InputStream is = Rule.class.getResourceAsStream( RDFS_RULES );
            BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				throw new WrappedIOException(e);
			}
    		rdfsrules=parseRules(rulesParserFromReader(br));
    	}
    	return rdfsrules;
	}

	/** 
     * Helper function find a URI argument in the current string,
     * optionally surrounded by matching <>.
     */
    private static String extractURI(String lineSoFar) {
        String token = lineSoFar.trim();
        if (token.startsWith("<")) {
            int split = token.indexOf('>');
            token = token.substring(1, split);
        }
        return token;
    }

    /** 
     * Helper function to return the next whitespace delimited argument
     * from the string
     */
    private static String nextArg(String token) {
        int start = nextSplit(0, false, token);
        int stop = nextSplit(start, true, token);
        return token.substring(start, stop);
    }
    
    /** 
     * Helper function to return the remainder of the line after
     * stripping off the next whitespace delimited argument
     * from the string
     */
    private static String nextAfterArg(String token) {
        int start = nextSplit(0, false, token);
        int stop = nextSplit(start, true, token);
        int rest = nextSplit(stop, false, token);
        return token.substring(rest);
    }
    
    /**
     * Helper function - find index of next whitespace or non white
     * after the start index. 
     */
    private static int nextSplit(int start, boolean white, String line) {
        int i = start;
        while (i < line.length()) {
            boolean isWhite = Character.isWhitespace(line.charAt(i));
            if ((white & isWhite) || (!white & !isWhite)) {
                return i;
            }
            i++;
        }
        return i;
    }

    public static void main(String[] args) {
        String test = " <http://myuri/fool>.";
        String arg = nextArg(test);
        String rest = nextAfterArg(test);
        String uri = extractURI(rest);
        System.out.println("ARG = [" + arg + "], URI = [" + uri + "]");
    }
    /**
     * Run a pre-bound rule parser to extract it's rules
     * @return a list of rules
     * @throws ParserException if there is a problem
     */
    public static List<Rule> parseRules(Parser parser) throws ParserException {
        boolean finished = false;
        List<Rule> ruleset = new ArrayList<Rule>();
        ruleset.addAll(parser.getRulesPreload());
        while (!finished) {
            try {
                parser.peekToken();
            } catch (NoSuchElementException e) {
                finished = true;
                break;
            }
            Rule rule = parser.parseRule();
            ruleset.add(rule);
        }
        return ruleset;
    }

    /**
     * Parse a string as a list a rules.
     * @return a list of rules
     * @throws ParserException if there is a problem
     */
    public static List<Rule> parseRules(String source) throws ParserException {
        return parseRules(new Parser(source));
    }
    

//=======================================================================
// parser support

    /**
     * Inner class which provides minimalist parsing support based on
     * tokenisation with depth 1 lookahead. No sensible error reporting on offer.
     * No embedded spaces supported.
     */
    public static class Parser {
        
        /** Tokenizer */
        private Tokenizer stream;
        
        /** Look ahead, null if none */
        private String lookahead;
        
        // Literal parse state flags
        private static final int NORMAL = 0;
        private static final int STARTED_LITERAL = 1;
        
        /** Literal parse state */
        private int literalState = NORMAL;;
        
        /** Trace back of recent tokens for error reporting */
        protected List<String> priorTokens = new ArrayList<String>();
        
        /** Maximum number of recent tokens to remember */
        private static final int maxPriors = 20;
        
        /** Variable table */
        private Map<String, Variable> varMap;
        
        /** Local prefix map */
        private PrefixMapping prefixMapping = PrefixMapping.Factory.create();
        
        /** Pre-included rules */
        private List<Rule> preloadedRules = new ArrayList<Rule>();
        
        /**
         * Constructor
         * @param source the string to be parsed
         */
        Parser(String source) {
            stream = new Tokenizer(source, "()[], \t\n\r", "'\"", true);
            lookahead = null;
        }
        
        /**
         * Register a new namespace prefix with the parser
         */
        public void registerPrefix(String prefix, String namespace ) {
            prefixMapping.setNsPrefix(prefix, namespace);
        }
        
        /**
         * Register a set of prefix to namespace mappings with the parser
         */
        public void registerPrefixMap(Map<String, String> map) {
            prefixMapping.setNsPrefixes(map);
        }
        
        /**
         * Return a map of all the discovered prefixes
         */
        public Map getPrefixMap() {
            return prefixMapping.getNsPrefixMap();
        }
        
        /**
         * Add a new set of preloaded rules.
         */
        void addRulesPreload(List<Rule> rules) {
            preloadedRules.addAll(rules);
        }
        
        /**
         * Return the complete set of preloaded rules;
         */
        public List<Rule> getRulesPreload() {
            return preloadedRules;
        }
        
        /**
         * Return the next token
         */
        String nextToken() {
            if (lookahead != null) {
                String temp = lookahead;
                lookahead = null;
                return temp;
            } else {
                String token = stream.nextToken();
                if (literalState == NORMAL) {
                    // Skip separators unless within a literal
                    while (isSeparator(token)) {
                        token = stream.nextToken();
                    }
                }
                if (token.equals("'")) {
                    if (literalState == NORMAL) {
                        literalState = STARTED_LITERAL;
                    } else {
                        literalState = NORMAL;
                    }
                }
                priorTokens.add(0, token);
                if (priorTokens.size() > maxPriors) {
                    priorTokens.remove(priorTokens.size()-1);
                }
                return token;
            }
        }
                
        /**
         * Return a trace of the recently seen tokens, for use
         * in error reporting
         */
        public String recentTokens() {
            StringBuffer trace = new StringBuffer();
            for (int i = priorTokens.size()-1; i >= 0; i--) {
                trace.append(priorTokens.get(i));
                trace.append(" ");
            }
            return trace.toString();
        }
        
        /**
         * Peek ahead one token.
         */
        String peekToken() {
            if (lookahead == null) {
                lookahead = nextToken();
            }
            return lookahead;
        }
        
        /**
         * Push back a previously fetched token. Only depth 1 supported.
         */
        void pushback(String token) {
            lookahead = token;
        }
        
        /**
         * Returns true if token is an skippable separator
         */
        boolean isSeparator(String token) {
            if (token.length() == 1) {
                char c = token.charAt(0);
                return (c == ',' || Character.isWhitespace(c));
            }
            return false;
        }
        
        /**
         * Returns true if token is a syntax element ()[]
         */
        boolean isSyntax(String token) {
            if (token.length() == 1) {
                char c = token.charAt(0);
                return (c == '(' || c == ')' || c == '[' || c == ']');
            }
            return false;
        }
        
        /**
         * Find the variable index for the given variable name
         * and return a Value_RuleVariable with that index.
         */
        Variable getValueVar(String name) {
        	Variable Value = varMap.get(name);
            if (Value == null) {
                Value = new Variable(name, varMap.size());
                varMap.put(name, Value);
            }
            return Value;
        }
        
        /**
         * Translate a token to a Value.
         */
        Value parseValue(String token) {
            if (token.startsWith("?")) {
                return getValueVar(token);
                // Dropped support for anon wildcards until the implementation is better resolved
            } else if (token.equals("*") || token.equals("_")) {
                throw new ParserException("Wildcard variables no longer supported", this);
////                return Value_RuleVariable.ANY;
//                return Value_RuleVariable.WILD;
            } else if (token.indexOf(':') != -1) {
                String exp = prefixMapping.expandPrefix(token); // Local map first
                if (exp == token) {
                    // No expansion was possible
                    String prefix = token.substring(0, token.indexOf(':'));
                    if (prefix.equals("http") || prefix.equals("urn") 
                     || prefix.equals("ftp") || prefix.equals("mailto")) {
                        // assume it is all OK and fall through
                    } else {
                        // Likely to be a typo in a qname or failure to register
                        throw new ParserException("Unrecognized qname prefix (" + prefix + ") in rule", this);
                    }
                }
                return new URIImpl(exp);
            } else if (peekToken().equals("(")) {
                Functor f = new Functor(token, parseValueList(), BuiltinRegistry.theRegistry);
                return Functor.makeFunctorValue( f );
            } else if (token.equals("'") || token.equals("\"")) {
                // A plain literal
                String lit = nextToken();
                // Skip the trailing quote
                nextToken();
                // Check for an explicit datatype
                if (peekToken().startsWith("^^")) {
                    String dtURI = nextToken().substring(2);
                    if (dtURI.startsWith("XMLSchema:")) {
                        dtURI = XMLSchema.NAMESPACE + "#" + dtURI.substring(4);
                    }
                    //URI dt = getXMLSchemaTypeByURI(dtURI);
                    URI dt = new URIImpl(dtURI);
                    return new LiteralImpl(lit, dt);
                } else {
                    return new LiteralImpl(lit);
                }
            } else  if ( Character.isDigit(token.charAt(0)) || 
                         (token.charAt(0) == '-' && token.length() > 1 && Character.isDigit(token.charAt(1))) ) {
                // A number literal
               return parseNumber(token);
            } else {
                // A  uri
                return new URIImpl(token);
            }
        }
        
        /**
         * Turn a possible numeric token into typed literal else a plain literal
         * @return the constructed literal Value
         */
        Value parseNumber(String lit) {
            if ( Character.isDigit(lit.charAt(0)) || 
                (lit.charAt(0) == '-' && lit.length() > 1 && Character.isDigit(lit.charAt(1))) ) {
                if (lit.indexOf(".") != -1) {
                    // Float?

                	try { 
                		Float.parseFloat(lit);
                		return new LiteralImpl(lit,XMLSchema.FLOAT);
                	} catch(NumberFormatException e) {
                		//ignore
                	}
                } else {
                    // Int?
                	try { 
                		Integer.parseInt(lit);
                		return new LiteralImpl(lit,XMLSchema.INT);
                	} catch(NumberFormatException e) {
                		//ignore
                	}
                    //if (XMLSchema.XMLSchemaint.isValid(lit)) {
                        //return Value.createLiteral(lit, "", XMLSchemaDatatype.XMLSchemaint);
                    //}
                }
            }
            // Default is a plain literal
            return new LiteralImpl(lit);
        }
        
        /**
         * Parse a list of Values delimited by parentheses
         */
        List<Value> parseValueList() {
            String token = nextToken();
            if (!token.equals("(")) {
                throw new ParserException("Expected '(' at start of clause, found " + token, this);
            }
            token = nextToken();
            List<Value> ValueList = new ArrayList<Value>();
            while (!isSyntax(token)) {
                ValueList.add(parseValue(token));
                token = nextToken();
            }
            if (!token.equals(")")) {
                throw new ParserException("Expected ')' at end of clause, found " + token, this);
            }
            return ValueList;
        }
        
        /**
         * Parse a clause, could be a triple pattern, a rule or a functor
         */
        ClauseEntry parseClause() {
            String token = peekToken();
            if (token.equals("(")) {
                List<Value> Values = parseValueList();
                if (Values.size() != 3) {
                    throw new ParserException("Triple with " + Values.size() + " Values!", this);
                }
                if (Functor.isFunctor(Values.get(0))) {
                    throw new ParserException("Functors not allowed in subject position of pattern", this);
                }
                if (Functor.isFunctor(Values.get(1))) {
                    throw new ParserException("Functors not allowed in predicate position of pattern", this);
                }
                return new TriplePattern(Values.get(0), Values.get(1), Values.get(2));
            } else if (token.equals("[")) {
                nextToken();
                return doParseRule(true);
            } else {
                String name = nextToken();
                List<Value> args = parseValueList();
                Functor clause = new Functor(name, args, BuiltinRegistry.theRegistry);
                if (clause.getImplementor() == null) {
                    // Not a fatal error becase later processing can add this
                    // implementation to the registry
                    logger.warning("Rule references unimplemented functor: " + name);
                }
                return clause;
            }
        }
        
        
        /**
         * Parse a rule, terminated by a "]" or "." character.
         */
        public Rule parseRule() {
            return doParseRule(false);
        }
        
        /**
         * Parse a rule, terminated by a "]" or "." character.
         * @param retainVarMap set to true to ccause the existing varMap to be left in place, which
         * is required for nested rules.
         */
        private Rule doParseRule(boolean retainVarMap) {
            try {
                // Skip initial '[' if present
                if (peekToken().equals("[")) {
                    nextToken();
                }
                // Check for optional name
                String name = null;
                String token = peekToken();
                if (token.endsWith(":")) {
                    name = token.substring(0, token.length()-1);
                    nextToken();
                }
                // Start rule parsing with empty variable table
                if (!retainVarMap) varMap = new HashMap<String, Variable>();
                // Body
                List<ClauseEntry> body = new ArrayList<ClauseEntry>();
                token = peekToken();
                while ( !(token.equals("->") || token.equals("<-")) ) {
                    body.add(parseClause());
                    token = peekToken();
                }
                boolean backwardRule = token.equals("<-");
                List<ClauseEntry> head = new ArrayList<ClauseEntry>();
                token = nextToken();   // skip -> token
                token = peekToken();
                while ( !(token.equals(".") || token.equals("]")) ) {
                    head.add(parseClause());
                    token = peekToken();
                } 
                nextToken();        // consume the terminating token
                Rule r = null;
                if (backwardRule) {
                    r =  new Rule(name, body, head);
                } else {
                    r = new Rule(name, head, body);
                }
                r.numVars = varMap.keySet().size();
                r.isBackward = backwardRule;
                return r;
            } catch (NoSuchElementException e) {
                throw new ParserException("Malformed rule", this);
            }
        }

    }
   
    /** Equality override */
    public boolean equals(Object o) {
        // Pass 1 - just check basic shape
        if (! (o instanceof Rule) ) return false;
        Rule other = (Rule) o;
        if (other.head.length != head.length) return false;
        if (other.body.length != body.length) return false;
        // Pass 2 - check clause by clause matching
        for (int i = 0; i < body.length; i++) {
            if (! ((ClauseEntry)body[i]).sameAs((ClauseEntry)other.body[i]) ) return false;
        }
        for (int i = 0; i < head.length; i++) {
            if (! ((ClauseEntry)head[i]).sameAs((ClauseEntry)other.head[i]) ) return false;
        }
        return true;
    }
        
    /** hash function override */
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < body.length; i++) {
            hash = (hash << 1) ^ body[i].hashCode();
        }
        for (int i = 0; i < head.length; i++) {
            hash = (hash << 1) ^ head[i].hashCode();
        }
        return hash;
    }
    
    /**
     * Compare clause entries, taking into account variable indices.
     * The equality function ignores differences between variables.
     */
    public boolean sameAs(Object o) {
        return equals(o);
    }
    
//=======================================================================
// Other supporting inner classes

    /**
     * Inner class. Exception raised if there is a problem
     * during rule parsing.
     */
    public static class ParserException extends RuntimeException {
        
        /** constructor */
        public ParserException(String message, Parser parser) {
            super(constructMessage(message, parser));
        }
        
        /**
         * Extract context trace from prior tokens stack
         */
        private static String constructMessage(String baseMessage, Parser parser) {
            StringBuffer message = new StringBuffer();
            message.append(baseMessage);
            message.append("\nAt '");
            message.append(parser.recentTokens());
            message.append("'");
            return message.toString();
        }
        
    }
    
}

/*
    (c) Copyright 2003, 2004, 2005, 2006 Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

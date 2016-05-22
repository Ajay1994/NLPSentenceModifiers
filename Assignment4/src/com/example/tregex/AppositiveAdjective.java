package com.example.tregex;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public class AppositiveAdjective {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String parserModel = "taggers/englishPCFG.ser.gz";
	    if (args.length > 0) {
	      parserModel = args[0];
	    }
	    LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	    String sentence = "Arthur was a big boy, tall, strong, and broad-shouldered.";
	    if (args.length == 0) {
	    	String t = demoAPI(lp, sentence);
	    	System.out.println(t);
	    	Tree tree = Tree.valueOf(t);
	    	
	    	TregexPattern patternMW = TregexPattern.compile("__ <: JJ|NN"); 
	    	// Run the pattern on one particular tree 
	    	TregexMatcher matcher = patternMW.matcher(tree); 
	    	// Iterate over all of the subtrees that matched 
	    	Tree postmod = null;
	    	String dependent = "";
	    	System.out.println("--------------------------------------------------");
	    	List<String> appositive = new ArrayList<String>();
	    	String sente = null;
	    	while (matcher.findNextMatchingNode()) { 
	    	  postmod = matcher.getMatch();
	    	  postmod.nodeString();
	    	  postmod.pennPrint();
	    	  dependent = postmod.toString();
	    	  
	    	  
	    	  Tree dependentTree = Tree.valueOf(dependent);
		      //System.out.println(dependentTree);
		      
		      
		      TregexPattern pattern = TregexPattern.compile("__ !<< __");
		      TregexMatcher match = pattern.matcher(dependentTree);
		      while(match.findNextMatchingNode()){
		    		dependent = match.getMatch().nodeString();
		    		appositive.add(dependent);
		      }
	    	}
	    	
	    	String sent[] = sentence.split(appositive.get(0));
	    	System.out.println(sent[0]);
	    	System.out.println(sent[1]);
	    	int index = sent[0].trim().lastIndexOf(" ");
	    	StringBuffer buf = new StringBuffer(sent[0].trim());
	    	buf.insert(index + 1, "," + appositive.get(0)+" " + sent[1].trim().replace(".", "")+" ");
	    	sente = buf.toString();
	    	System.out.println(sente);
	    }
	    
	}
	
	public static void demoDP(LexicalizedParser lp, String filename) {
	    // This option shows loading, sentence-segmenting and tokenizing
	    // a file using DocumentPreprocessor.
	    TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = null;
	    if (tlp.supportsGrammaticalStructures()) {
	      gsf = tlp.grammaticalStructureFactory();
	    }
	    // You could also create a tokenizer here (as below) and pass it
	    // to DocumentPreprocessor
	    for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
	      Tree parse = lp.apply(sentence);
	      parse.pennPrint();
	      System.out.println();

	      if (gsf != null) {
	        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	        Collection tdl = gs.typedDependenciesCCprocessed();
	        System.out.println(tdl);
	        System.out.println();
	      }
	    }
	  }
	  public static String demoAPI(LexicalizedParser lp, String sentence) {
		  
	    // This option shows loading and using an explicit tokenizer
	    TokenizerFactory<CoreLabel> tokenizerFactory =
	        PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	    Tokenizer<CoreLabel> tok =
	        tokenizerFactory.getTokenizer(new StringReader(sentence));
	    List<CoreLabel> rawWords2 = tok.tokenize();
	    Tree parse = lp.apply(rawWords2);
	    parse.pennPrint();
	    String tree = parse.toString();
	    
	    TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
	    //tp.printTree(parse);
	    return tree;
	  }

	  private AppositiveAdjective() {} // static methods only

}

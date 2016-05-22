package com.example.tregex;

import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Collection;
import java.util.List;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.parser.lexparser.TreebankLangParserParams;
import edu.stanford.nlp.parser.lexparser.EnglishTreebankParserParams;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphFactory;
import edu.stanford.nlp.semgraph.semgrex.SemgrexMatcher;
import edu.stanford.nlp.semgraph.semgrex.SemgrexPattern;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
public class ParticiplePhrase {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String parserModel = "taggers/englishPCFG.ser.gz";
	    if (args.length > 0) {
	      parserModel = args[0];
	    }
	    LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	    String sentence = "The basketball team, encouraged by its performance in the semifinals, went on to the finals.";
	    if (args.length == 0) {
	    	String t = demoAPI(lp, sentence);
	    	
	    	Tree tree = Tree.valueOf(t);
	    	
	    	TregexPattern patternMW = TregexPattern.compile("VP << /VB.?/ <2 PP"); 
	    	// Run the pattern on one particular tree 
	    	TregexMatcher matcher = patternMW.matcher(tree); 
	    	// Iterate over all of the subtrees that matched 
	    	Tree postmod = null;
	    	String dependent = "";
	    	System.out.println("--------------------------------------------------");
	    	while (matcher.findNextMatchingNode()) { 
	    	  postmod = matcher.getMatch();
	    	  postmod.nodeString();
	    	  postmod.pennPrint();
	    	  dependent = postmod.toString();
	    	}
	    	System.out.println("------------------------------------");
	    	Tree dependentTree = Tree.valueOf(dependent);
	    	System.out.println(dependentTree);
	    	patternMW = TregexPattern.compile("__ !<< __");
	    	matcher = patternMW.matcher(dependentTree);
	    	dependent = "";
	    	while(matcher.findNextMatchingNode()){
	    		dependent = dependent + matcher.getMatch().nodeString() + " ";
	    	}
	    	
	    	boolean isPostModifier = true;
	    	
	    	System.out.println(dependent);
	    	int index = sentence.indexOf(dependent.trim());
	    	if(index == 0) isPostModifier = false;
	    	if(isPostModifier){
	    		String nondependent = sentence.replace(dependent.trim(), "");
		    	nondependent =  nondependent.replace(",", "");
		    	dependent = dependent.trim() + ", "+ nondependent.trim();
		    	System.out.println(dependent);
	    	}else{
	    		String nondependent = sentence.replace(dependent.trim(), "");
		    	nondependent =  nondependent.replace(".", "");
		    	nondependent =  nondependent.replace(",", "");
		    	dependent = nondependent.trim() + ", "+ dependent.trim()+ ".";
		    	System.out.println(dependent);
	    	}
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

	  private ParticiplePhrase() {} // static methods only

}

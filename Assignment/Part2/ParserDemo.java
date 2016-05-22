package com.assignment.part1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.io.StringReader;

import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

public class ParserDemo {
	public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }
	
	public static void main(String[] args) {
	    String parserModel = "taggers/englishPCFG.ser.gz";
	    if (args.length > 0) {
	      parserModel = args[0];
	    }
	    LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	    String sentence1 = "I have been hit by a car.";
	    String sentence2 = "A pointer contains the address of the function in memory.";
	    if (args.length == 0) {
	      String tree1 = demoAPI(lp, sentence1);
	      String tree2 = demoAPI(lp, sentence2);
	      String sent1token[] = tree1.split("\\),");
		    List<String> sent1dependencies = new ArrayList<String>();
		    for(String s:sent1token){
		    	s = s.replace("[", "");
		    	s = s.replaceAll("\\d+", "");
		    	s = s.replaceAll("-", "");
		    	s = s.replace(")]", "");
		    	s = s.trim();
		    	s = s+")";
		    	System.out.println(s);
		    	sent1dependencies.add(s);
		    }
		    
		    System.out.println("++++++++++++++++++++++++++++++++++");
		    
		    String sent2token[] = tree2.split("\\),");
		    List<String> sent2dependencies = new ArrayList<String>();
		    for(String s:sent2token){
		    	s = s.replace("[", "");
		    	s = s.replaceAll("\\d+", "");
		    	s = s.replaceAll("-", "");
		    	s = s.replace(")]", "");
		    	s = s.trim();
		    	s = s+")";
		    	System.out.println(s);
		    	sent2dependencies.add(s);
		    }
		    
		    System.out.println();
		    
		    List<String> list = ParserDemo.intersection(sent1dependencies, sent2dependencies);
		    System.out.println(list);
		    System.out.println("\nTotal No of common edges : "+ list.size());
		    
		    
	    } else {
	      String textFile = (args.length > 1) ? args[1] : args[0];
	      demoDP(lp, textFile);
	    }
	  }
		
	
	  /**
	   * demoDP demonstrates turning a file into tokens and then parse
	   * trees.  Note that the trees are printed by calling pennPrint on
	   * the Tree object.  It is also possible to pass a PrintWriter to
	   * pennPrint if you want to capture the output.
	   * This code will work with any supported language.
	   */
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

	  /**
	   * demoAPI demonstrates other ways of calling the parser with
	   * already tokenized text, or in some cases, raw text that needs to
	   * be tokenized as a single sentence.  Output is handled with a
	   * TreePrint object.  Note that the options used when creating the
	   * TreePrint can determine what results to print out.  Once again,
	   * one can capture the output by passing a PrintWriter to
	   * TreePrint.printTree. This code is for English.
	   */
	  public static String demoAPI(LexicalizedParser lp, String sentence) {
	    // This option shows parsing a list of correctly tokenized words
//	    String[] sent = { "This", "is", "an", "easy", "sentence", "." };
//	    List<CoreLabel> rawWords = Sentence.toCoreLabelList(sent);
//	    Tree parse = lp.apply(rawWords);
//	    parse.pennPrint();
//	    System.out.println();

	    // This option shows loading and using an explicit tokenizer
	    String sent2 = "I hit the ball with my bat and it flew away..";
	    TokenizerFactory<CoreLabel> tokenizerFactory =
	        PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	    Tokenizer<CoreLabel> tok =
	        tokenizerFactory.getTokenizer(new StringReader(sentence));
	    List<CoreLabel> rawWords2 = tok.tokenize();
	    Tree parse = lp.apply(rawWords2);

	    TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
	    String sent1 = tdl.toString();
	    
	    return sent1;
	    
	    

	    // You can also use a TreePrint object to print trees and dependencies
	    //TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
	    //tp.printTree(parse);
	    //tp.p
	  }

	  private ParserDemo() {} // static methods only

}

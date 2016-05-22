package com.assignment.part1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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

public class PassiveActive {
	
	public static String demoAPI(LexicalizedParser lp, String sentence) {
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
	  }

	public static void printActiveVoice() throws FileNotFoundException{
		Map<String, String> subToObj = new HashMap<String, String>();
		subToObj.put("i", "me");
		subToObj.put("we", "us");
		subToObj.put("he", "him");
		subToObj.put("she", "her");
		subToObj.put("it", "it");
		subToObj.put("you", "you");
		subToObj.put("they", "them");
		
		
		Map<String, String> objToSub = new HashMap<String, String>();
		objToSub.put("me", "I");
		objToSub.put("us", "we");
		objToSub.put("him", "he");
		objToSub.put("her", "she");
		objToSub.put("it", "it");
		objToSub.put("you", "you");
		objToSub.put("them", "they");
		
		List<Relation> passiveRelation = new ArrayList<Relation>();
		List<Relation> activeRelation = new ArrayList<Relation>();
		
		Scanner scan = new Scanner(new File("relation.txt"));
		while(scan.hasNextLine()){
			//System.out.println(scan.nextLine());
			String s = scan.nextLine();
			String relation = s.substring(0, s.indexOf("("));
			String head = s.substring(s.indexOf("(")+1, s.indexOf(","));
			String tail = s.substring(s.indexOf(",")+2, s.indexOf(")"));
			Relation rel = new Relation();
			rel.relation = relation;
			rel.head = head;
			rel.tail = tail;
			passiveRelation.add(rel);
		}
		int isPlural = 0;
		Relation auxRelation = null;
		for(Relation rel : passiveRelation){
			if(rel.relation.equalsIgnoreCase("det")){
				activeRelation.add(rel);
			}else if(rel.relation.equalsIgnoreCase("case")){
				if((rel.head.charAt(rel.head.length()-1) == 's') || rel.head.equalsIgnoreCase("us") || rel.head.equalsIgnoreCase("me") || rel.head.equalsIgnoreCase("you") || rel.head.equalsIgnoreCase("them")){
					isPlural = 1;
				}
			}else if(rel.relation.equalsIgnoreCase("nsubjpass")){
				if(subToObj.containsKey(rel.tail.toLowerCase())){
					Relation temp = new Relation();
					temp.relation = "dobj";
				    temp.head = rel.head;
					temp.tail = subToObj.get(rel.tail.toLowerCase());
					activeRelation.add(temp);
				}else{
					Relation temp = new Relation();
					temp.relation = "dobj";
				    temp.head = rel.head;
					temp.tail = rel.tail;
					activeRelation.add(temp);
				}
			}else if(rel.relation.equalsIgnoreCase("root")){
				activeRelation.add(rel);
			}else if(rel.relation.equalsIgnoreCase("nmod:agent") || rel.relation.equalsIgnoreCase("nmod")){
				if(objToSub.containsKey(rel.tail.toLowerCase())){
					Relation temp = new Relation();
					temp.relation = "nsubj";
				    temp.head = rel.head;
					temp.tail = objToSub.get(rel.tail.toLowerCase());
					activeRelation.add(temp);
				}else{
					Relation temp = new Relation();
					temp.relation = "nsubj";
				    temp.head = rel.head;
					temp.tail = rel.tail;
					activeRelation.add(temp);
				}
			}else if(rel.relation.equalsIgnoreCase("aux")){
				auxRelation = rel;
			}else if(rel.relation.equalsIgnoreCase("auxpass")){
				
			}else{
				activeRelation.add(rel);
			}
		}
		if(isPlural == 0){
			Relation temp = new Relation();
			temp.relation = "aux";
		    temp.head = auxRelation.head;
			if(auxRelation.tail.equalsIgnoreCase("have")){
				temp.tail = "has";
			}else if(auxRelation.tail.equalsIgnoreCase("do")){
				temp.tail = "does";
			}else{
				temp.tail = auxRelation.tail;
			}
			activeRelation.add(temp);
		}else{
			Relation temp = new Relation();
			temp.relation = "aux";
		    temp.head = auxRelation.head;
			if(auxRelation.tail.equalsIgnoreCase("has")){
				temp.tail = "have";
			}else if(auxRelation.tail.equalsIgnoreCase("does")){
				temp.tail = "do";
			}else{
				temp.tail = auxRelation.tail;
			}
			activeRelation.add(temp);
		}
		
		for(Relation rel : activeRelation){
			System.out.println(rel.relation+"("+rel.head+", "+ rel.tail+ " )");
		}
	}
	public static void main(String[] args) throws FileNotFoundException {
		String parserModel = "taggers/englishPCFG.ser.gz";
	    if (args.length > 0) {
	      parserModel = args[0];
	    }
	    PrintWriter out = new PrintWriter(new File("relation.txt"));
	    LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	    String sentence1 = "The ball have been hit by me very fast.";
		String tree1 = demoAPI(lp, sentence1);
	      
	    String sent1token[] = tree1.split("\\),");
		 
		    for(String s:sent1token){
		    	s = s.replace("[", "");
		    	s = s.replaceAll("\\d+", "");
		    	s = s.replaceAll("-", "");
		    	s = s.replace(")]", "");
		    	s = s.trim();
		    	s = s+")";
		    	//System.out.println(s);
		    	out.write(s);
		    	out.write("\n");
		    }
		 out.close();
		 printActiveVoice();
	}
}

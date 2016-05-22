package com.assignment.part1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Tagging {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		String infilename = "iofiles/input.txt";
		String outfilename = "iofiles/output.txt";
		PrintWriter out = new PrintWriter(outfilename);
		MaxentTagger tagger =  new MaxentTagger("taggers/left3words-wsj-0-18.tagger");
		List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader(infilename)));
	    for (List<HasWord> sentence : sentences) {
	      List<TaggedWord> tSentence = tagger.tagSentence(sentence);
	      String sent = Sentence.listToString(tSentence, false);
	      out.write(sent);
	      out.write("\n");
	    }
		out.close();
	}

}

package com.wbs.app;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.*;
import java.util.ArrayList;

//public class CustomAnalyzer extends StopwordAnalyzerBase {
//	public CustomAnalyzer(CharArraySet stopwords) {
//		super(stopwords);
//	}
//	public CharArraySet getStopwords() {
//			return stopwords;
//	}
//	@Override
//	protected TokenStreamComponents createComponents(final String fieldName) {
//		final Tokenizer src;
//	    StandardTokenizer t = new StandardTokenizer();
//	    src = t;
//			CharArraySet stopWords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
//
//	    TokenStream tok = new StandardFilter(src);
//	    tok = new LowerCaseFilter(tok);
//	    tok = new StopFilter(tok, stopWords);
//      	tok = new PorterStemFilter(tok);
//
//	    return new TokenStreamComponents(src, tok);
//	}
//
//}


public class CustomAnalyzer extends StopwordAnalyzerBase{

	protected TokenStreamComponents createComponents(String s) {
		final Tokenizer source = new StandardTokenizer();

		TokenStream tok = new StandardFilter(source);
		tok = new LowerCaseFilter(tok);
		try {
			tok = new StopFilter(tok, getStopWords());
		} catch (IOException e) {
			e.printStackTrace();
		}

		tok = new PorterStemFilter(tok);

		return new TokenStreamComponents(source, tok);
	}

	protected CharArraySet getStopWords() throws IOException {
		FileInputStream fileInputStream = new FileInputStream("../project/stopwords.txt");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
		String line = null;
		ArrayList<String> stopWordsSet = new ArrayList<String>();

		while ((line = bufferedReader.readLine()) != null)
		{
			stopWordsSet.add(line);
		}

		return  new CharArraySet(stopWordsSet, true);
	}
}

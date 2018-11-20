package com.wbs.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class CustomAnalyzer extends StopwordAnalyzerBase{

	protected TokenStreamComponents createComponents(String s) {
		final Tokenizer source = new StandardTokenizer();

		TokenStream tok = source;
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
		bufferedReader.close();
		return  new CharArraySet(stopWordsSet, true);
	}
}

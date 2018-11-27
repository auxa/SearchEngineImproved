package com.wbs.app;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.tartarus.snowball.ext.EnglishStemmer;


public class CustomAnalyzer extends StopwordAnalyzerBase{

	protected TokenStreamComponents createComponents(String s) {

        final Tokenizer source = new StandardTokenizer();
        TokenStream tok = new LowerCaseFilter(source);

        try {
            tok = new TrimFilter(tok);
//            tok = new SynonymGraphFilter(tok, buildMap(), true);
            // tok = new FlattenGraphFilter(tok);
            //tok = new SynonymAwareStopFilter(tok, getStopWords());
            tok = new StopFilter(tok, getStopWords());
            tok = new SnowballFilter(tok, new EnglishStemmer());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

	protected SynonymMap buildMap() throws IOException, ParseException {
        FileReader reader = new FileReader("../project/wn_s.pl");
        WordnetSynonymParser synonymParser = new WordnetSynonymParser(true, true, new StandardAnalyzer(CharArraySet.EMPTY_SET));
        synonymParser.parse(reader);
        SynonymMap synonymMap = synonymParser.build();
        return synonymMap;
    }
}

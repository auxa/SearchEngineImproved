package com.wbs.app;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.search.TokenStreamToTermAutomatonQuery;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

public class QueryAnalyzer extends StopwordAnalyzerBase {


    protected TokenStreamComponents createComponents(String s) {

        final Tokenizer source = new StandardTokenizer();
        TokenStream tok = new LowerCaseFilter(source);

        try {
            tok = new TrimFilter(tok);
            tok = new SynonymGraphFilter(tok, buildMap(), true);
            tok = new SynonymAwareStopFilter(tok, getStopWords());
            tok = new SnowballFilter(tok, new EnglishStemmer());
            //        tok = new PorterStemFilter(tok);
        } catch (IOException | ParseException e) {
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

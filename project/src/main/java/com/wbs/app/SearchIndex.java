package com.wbs.app;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.store.Directory;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.*;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.similarities.*;

public class SearchIndex {
          private static String INDEX_DIRECTORY = "../index";

          private static int MAX_RESULTS = 30; //subject to change

          public static void main(String[] args) throws Exception {

            Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
            IndexReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            // Change the "ClassicSimilarity" to change similarity scoring
					 	isearcher.setSimilarity(new ClassicSimilarity());

            Map<String, Float> boost = createBoostMap();

            CharArraySet stopWords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

            Analyzer analyzer = new CustomAnalyzer(stopWords);

            MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[] {"filename", "text"}, analyzer, boost);

            ArrayList<Document> loadedQueries = loadQueriesFromFile();

            ArrayList<String> vars = new ArrayList<String>();
            for (int j=0; loadedQueries.size()>j; j++){
              Document qd = loadedQueries.get(j);
              String q = qd.get("desc");
              q = q.trim();
              if (q.length() >0){
                Query query = null;
                String stringify = QueryParser.escape(q);

                try{
                  query = qp.parse(stringify);
                } catch (ParseException e){
                  System.out.println("Failed to parse: "+ e);
                }
                ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;
                for (int i = 0; i < hits.length; i++) {
                  Document hitDoc = isearcher.doc(hits[i].doc);
                  int rank = i+1;
                  double noms = hits[i].score;
                  if (noms >0){
                    System.out.println(qd.get("id") + " -- " + hitDoc.get("id") + " "+ rank + " "+ noms  +" -- \n");
                  }
                }
              }
            }
            ireader.close();
            directory.close();
          }
      private static Map<String, Float> createBoostMap(){
        Map<String, Float> boost = new HashMap<>();
        boost.put("filename", (float) 0.52);
        boost.put("text",(float) 0.48);
        return boost;
      }

      /*
      private static void writeToFile(ArrayList<String> results){
        BufferedWriter writer = null;
        try {
            File logFile = new File("results.txt");
            writer = new BufferedWriter(new FileWriter(logFile));
            for (String res : results){
              writer.write(res);
            }
          } catch (Exception e) {
              e.printStackTrace();
          } finally {
              try {
                  writer.close();
              } catch (Exception e) {
              }
          }
      }
      */
      private static ArrayList<Document> loadQueriesFromFile() {
        try {
          TopicsParser myParser = new TopicsParser();
          ArrayList<Document> al = myParser.parseFile();
          return al;
        }

      catch(Exception ex) {
          System.out.println( "Error reading file '" + ex + "'");

      }
      return null;
    }
}

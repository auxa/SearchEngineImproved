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
          private static Analyzer analyzer;
          private static String INDEX_DIRECTORY = "../index";

          private static int MAX_RESULTS = 30; //subject to change

          public static void main(String[] args) throws Exception {

            Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
            IndexReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);
            // Change the "ClassicSimilarity" to change similarity scoring
			// isearcher.setSimilarity(new ClassicSimilarity());

            Map<String, Float> boost = createBoostMap();

            CharArraySet stopWords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

            analyzer = new CustomAnalyzer(stopWords);

            ArrayList<Document> loadedQueries = loadQueriesFromFile();

            ArrayList<String> vars = new ArrayList<String>();
            for (int j=0; loadedQueries.size()>j; j++){
              Document qd = loadedQueries.get(j);
              BooleanQuery qf = getQuery(qd);
              ScoreDoc[] hits = isearcher.search(qf, MAX_RESULTS).scoreDocs;
              for (int i = 0; i < hits.length; i++) {
                  Document hitDoc = isearcher.doc(hits[i].doc);
                  int rank = i+1;
                  double noms = hits[i].score;
                  System.out.print(qd.get("id") + " -- " + hitDoc.get("id") + " "+ rank + " "+ noms  +" -- EXP \n");
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
    private static BooleanQuery getQuery(Document d) throws Exception {
      String q = d.get("title") + " " + d.get("desc");
      String n = d.get("narr");
      Map<String, Float> boost = createBoostMap();
      q = q.replace("Description:", "");
      n = n.replace("Narrative:", "");
      MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[] {"filename", "text"}, analyzer, boost);
      String[] arr = n.split("\\. ");

      BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
      booleanQuery.add(qp.parse(QueryParser.escape(q.trim())), BooleanClause.Occur.MUST);

      for(int i =0; i< arr.length; i++){
        String s = arr[i];
        s = (s.toLowerCase()).replace("documents", "");
        if(s.contains("not relevant")){
            s = s.replace("not relevant", "");
            booleanQuery.add(qp.parse(QueryParser.escape(q.trim())), BooleanClause.Occur.MUST_NOT);
        }else{
            s = s.replace("relevant", "");
            booleanQuery.add(qp.parse(QueryParser.escape(q.trim())), BooleanClause.Occur.SHOULD);
        }
      }

      return booleanQuery.build();
    }
}

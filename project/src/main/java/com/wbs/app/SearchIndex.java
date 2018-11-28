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
import org.apache.lucene.search.BoostQuery;
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

          private static int MAX_RESULTS = 10000; //subject to change

          public static void main(String[] args) throws Exception {

            Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
            IndexReader ireader = DirectoryReader.open(directory);
            IndexSearcher isearcher = new IndexSearcher(ireader);

            analyzer = new CustomAnalyzer();

            ArrayList<Document> loadedQueries = loadQueriesFromFile();
            ArrayList<String> qrels = new ArrayList<String>();
            String bob = "";
            for (int j=0; loadedQueries.size()>j; j++){
                Document qd = loadedQueries.get(j);
                Query qf = getQuery(qd);
                ScoreDoc[] hits = isearcher.search(qf, MAX_RESULTS).scoreDocs;
                System.out.println(" " +  qd.get("id")  + " " + hits.length);
                for (int i = 0; i < hits.length; i++) {

                    Document hitDoc = isearcher.doc(hits[i].doc);
                    int rank = i+1;
                    double noms = hits[i].score;
                    bob = (qd.get("id") + " -- " + hitDoc.get("id") + " "+ rank + " "+ noms  +" -- EXP \n");
                    qrels.add(bob);
                }
              }
            writeToFile(qrels);
            ireader.close();
            directory.close();
          }
      private static Map<String, Float> createBoostMap(){
        Map<String, Float> boost = new HashMap<>();
        boost.put("filename", (float) 0.1);
        boost.put("text",(float) 0.9);
        return boost;
      }


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
    private static Query getQuery(Document d) throws Exception {
        String q = d.get("title");
        String[] tops = q.split(",");
        String des = d.get("desc");
        String[] var = des.split("Narrative:");
        des = var[0];
        String n = d.get("narr");
        n = n.replace("etc.", "");
        n = n.replace("i.e.", "");
        n = n.replace("e.g.", "");
        n = n.replace("U.S.", "United States");


        Map<String, Float> boost = createBoostMap();
        des = des.replace("Description:", "");
        n = n.replace("Narrative:", "");
        MultiFieldQueryParser qp = new MultiFieldQueryParser(new String[] {"filename", "text"}, analyzer, boost);
        String[] arr = n.split("\\. ");

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

        System.out.println(des);

        for(int i =0; i< arr.length; i++){
            String s = arr[i];
            s = (s.toLowerCase()).replace("documents", "");
            if((s.contains("not relevant") || s.contains("irrelevant")) && !s.contains("unless") ){
                s = s.replace("not relevant", "");
                booleanQuery.add(wrapWithBoost(qp.parse(QueryParser.escape(s.trim())), -0.08f), BooleanClause.Occur.SHOULD);

            }else{
                s = s.replace("relevant", "");
                booleanQuery.add(wrapWithBoost(qp.parse(QueryParser.escape(s.trim())), 0.98f), BooleanClause.Occur.SHOULD);
            }
        }
        for(String top : tops){
            top = top.trim();
            if(Character.isUpperCase(top.charAt(0))){
                System.out.println(top);
                booleanQuery.add(wrapWithBoost(qp.parse(QueryParser.escape(top)), 3.5f), BooleanClause.Occur.SHOULD);
            }else{
                booleanQuery.add(wrapWithBoost(qp.parse(QueryParser.escape(top)), 3.5f), BooleanClause.Occur.SHOULD);
            }

        }
        booleanQuery.add(wrapWithBoost(qp.parse(QueryParser.escape(des.trim())), 1.65f), BooleanClause.Occur.SHOULD);
        return booleanQuery.build();
    }
    private static Query wrapWithBoost(Query query, float boost) {
        return new BoostQuery(query, boost);
    }
}
package com.wbs.app;

import java.io.IOException;
import java.nio.file.Paths;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.search.similarities.*;

public class CreateIndex {

	// Directory where the search index will be saved
	private static String INDEX_DIRECTORY = "../index";

	public static void main(String[] args) throws IOException {
//		CharArraySet stopwords = CharArraySet.copy(StopAnalyzer.ENGLISH_STOP_WORDS_SET);

		Analyzer analyzer = new CustomAnalyzer();

		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

		indexWriterConfig = indexWriterConfig.setSimilarity(new ClassicSimilarity());

		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

		IndexWriter iwriter = new IndexWriter(directory, indexWriterConfig);

		iwriter = addDocuments(iwriter);

		iwriter.close();
		directory.close();

	}

	private static IndexWriter addDocuments(IndexWriter iw){
		try {

			LAParser laParser = new LAParser();
			FrParser frParser = new FrParser();
			FTParser ftParser = new FTParser();
			FbisParser fbParser = new FbisParser();

			ArrayList<Document> myDocs = laParser.parseFile();
			myDocs.addAll(frParser.parseFile());
			myDocs.addAll(ftParser.parseFile());
			myDocs.addAll(fbParser.parseFile());
			// HashMap zipfDist = zipfCalculator(myDocs);
			// printMap(zipfDist);

			for(Document doc : myDocs){
				iw.addDocument(doc);
				System.out.println("Building index "+ doc.get("id"));
			}
		}
		catch(Exception ex) {
			System.out.println( "Unable to open file '" + ex + "'");
		}
		return iw;
	}

	public static HashMap zipfCalculator(ArrayList<Document> myDocs){
		HashMap zipfDist = new HashMap();

		for(int i=0; i<myDocs.size(); i++){
			Document doc = myDocs.get(i);
			String text = doc.get("text");
			String[] textArr = text.split(" ");

			for(int j=0; j<textArr.length; j++){
				if(zipfDist.get(textArr[j])==null){
					zipfDist.put(textArr[j], new Integer(1));
				}
				else {
					int val = (Integer) zipfDist.get(textArr[j]);
					zipfDist.put(textArr[j], new Integer(val+1));
				}
			}
		}
		return zipfDist;
	}

	public static void printMap(HashMap mp) {

		for (Integer key : mp.keySet()) {
			System.out.println(key mp.get(key));
		}
	}
}

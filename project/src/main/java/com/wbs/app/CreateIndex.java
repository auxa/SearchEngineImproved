package com.wbs.app;

import java.io.IOException;
import java.nio.file.Paths;
import java.io.*;
import java.text.ParseException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.TreeMap;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class CreateIndex {

	// Directory where the search index will be saved
	private static String INDEX_DIRECTORY = "../index";

	public static void main(String[] args) throws IOException, ParseException {

		Analyzer analyzer = new CustomAnalyzer();

		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

		indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

		IndexWriter iwriter = new IndexWriter(directory, indexWriterConfig);

		iwriter = addDocuments(iwriter);

		iwriter.close();
		directory.close();

	}

	private static IndexWriter addDocuments(IndexWriter iw){
		try {


			FrParser frParser = new FrParser();
			LAParser laParser = new LAParser();
			FTParser ftParser = new FTParser();
			FbisParser fbParser = new FbisParser();

			ArrayList<Document> myDocs = laParser.parseFile();
			myDocs.addAll(frParser.parseFile());
			myDocs.addAll(ftParser.parseFile());
			myDocs.addAll(fbParser.parseFile());
			// TreeMap zipfDist = zipfCalculator(myDocs);
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

	public static TreeMap zipfCalculator(ArrayList<Document> myDocs){
		// HashMap zipfDist = new HashMap();
		TreeMap<String, Integer> zipfDist = new TreeMap<>();
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

	public static void printMap(TreeMap mp) {
		System.out.println(entriesSortedByValues(mp));
	}

	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
        new Comparator<Map.Entry<K,V>>() {
            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                return res != 0 ? res : 1;
            }
        }
    );
    sortedEntries.addAll(map.entrySet());
    return sortedEntries;
}
}

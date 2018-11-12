package com.wbs.app;

import java.util.ArrayList;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FrParser {

	public FrParser() {}

	// Parses fr94 file doc's
	// Return Arraylist of lucene Documents
	// Document fields:
	// 		- id: unique id of document, TextField, String
	// 		- filename: <TITLE> tag of document, TextField, String ** Not present in all docs
	// 		- text: cleaned body of document text, TextField, String
	// 		- date: <DATE> tag contents, TextField, String ** Not present in all docs

	public ArrayList<Document> parseFile(){
		String docDir="../Assignment Two/fr94";
		ArrayList<Document> docList = new ArrayList<Document>();
		File dir = new File(docDir);
  		File[] directoryListing = dir.listFiles();
  		if (directoryListing != null) {
    		for (File child : directoryListing) {
    			File subDocDir = new File(child.getAbsolutePath());
    			File[] subDirectoryListing = subDocDir.listFiles();
    			if (subDirectoryListing != null) {
    				for(File child2 : subDirectoryListing ){
	    				File input = child2;
	    				try {
	    					org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8");
							Elements documents = doc.getElementsByTag("DOC");
							for (Element docI: documents){
								String docNum=docI.getElementsByTag("DOCNO").text();
								Elements text= docI.getElementsByTag("TEXT");
								Elements date = docI.getElementsByTag("DATE");
								Elements title= docI.getElementsByTag("DOCTITLE");
								String textField=getBasicText(text.text());
								String titleText= title.text();
								Document customDoc = new Document();
								customDoc.add(new TextField("id", docNum, Field.Store.YES));
								customDoc.add(new TextField("filename", title.text(), Field.Store.YES));
								customDoc.add(new TextField("text", textField, Field.Store.YES));
								customDoc.add(new TextField("date", date.text(), Field.Store.YES));
								docList.add(customDoc);
								System.out.println("added doc number FT:  "+ docNum);

							}
						} catch (IOException e) {
							e.printStackTrace();
						}
	    			}
    			}
    		}
      	}
  		else {

  		}
		return docList;
	}

	// Function which takes in all text between <TEXT> tags and return cleaned version with no tags or comments
	public String getBasicText(String text){
		String [] lines = text.split("\r\n");
		String cleanedText="";
		for(int i=0; i<lines.length; i++){
			if(!(lines[i].contains("<!--") || lines[i].contains("<AGENCY>") || lines[i].contains("<DATE>") || lines[i].contains("<DOCTITLE>") ||
				lines[i].contains("<ACTION>") || lines[i].contains("<SUMMARY>") || lines[i].contains("<FOOTNAME>") || lines[i].contains("<FURTHER>") ||
				lines[i].contains("<SIGNER>") || lines[i].contains("<SIGNJOB>") || lines[i].contains("<FRFILING>") || lines[i].contains("<BILLING>") ||
				lines[i].contains("<FOOTNOTE>") || lines[i].contains("<FOOTCITE>") || lines[i].contains("<TABLE>") || lines[i].contains("<ADDRESS>") ||
				lines[i].contains("<IMPORT>") || lines[i].contains("<SUPPLEM>") || lines[i].contains("<USDEPT>") || lines[i].contains("<USBUREAU>") ||
				lines[i].contains("<CFRNO>") || lines[i].contains("<RINDOCK>") || lines[i].contains("<FRFILING>") || lines[i].contains("<BILLING>") ||
				lines[i].contains("</AGENCY>") || lines[i].contains("</DATE>") || lines[i].contains("</DOCTITLE>") ||
				lines[i].contains("</ACTION>") || lines[i].contains("</SUMMARY>") || lines[i].contains("</FOOTNAME>") || lines[i].contains("</FURTHER>") ||
				lines[i].contains("</SIGNER>") || lines[i].contains("</SIGNJOB>") || lines[i].contains("</FRFILING>") || lines[i].contains("</BILLING>") ||
				lines[i].contains("</FOOTNOTE>") || lines[i].contains("</FOOTCITE>") || lines[i].contains("</TABLE>") || lines[i].contains("</ADDRESS>") ||
				lines[i].contains("</IMPORT>") || lines[i].contains("</SUPPLEM>") || lines[i].contains("</USDEPT>") || lines[i].contains("</USBUREAU>") ||
				lines[i].contains("</CFRNO>") || lines[i].contains("</RINDOCK>") || lines[i].contains("</FRFILING>") || lines[i].contains("</BILLING>" )))
				{
					cleanedText+=(lines[i] + "\r\n");
				}
			}
			return cleanedText;
		}
}

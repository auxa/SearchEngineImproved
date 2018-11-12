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

public class LAParser {

	public LAParser() {}


	public ArrayList<Document> parseFile(){
		String docDir="../Assignment Two/latimes";
		ArrayList<Document> docList = new ArrayList<Document>();
		File dir = new File(docDir);
		int index =0;
		File[] dl = dir.listFiles();
		for(File files : dl){
			index++;
			System.out.println(files.getName() + " " + index);

    		File input = files;
  			try {
  				org.jsoup.nodes.Document doc = Jsoup.parse(input, "UTF-8");
  				Elements documents = doc.getElementsByTag("DOC");
  				for (Element docI: documents){
  					String docNum=docI.getElementsByTag("DOCNO").text();
  					Elements date = docI.getElementsByTag("DATE");

  					Elements title= docI.getElementsByTag("HEADLINE");

    				Elements text= docI.getElementsByTag("TEXT");

    				String textField=getBasicText(text.text());
    				Document customDoc = new Document();
    				customDoc.add(new TextField("id", docNum, Field.Store.YES));
    				customDoc.add(new TextField("filename", title.text(), Field.Store.YES));
    				customDoc.add(new TextField("text", textField, Field.Store.YES));
    				customDoc.add(new TextField("date", date.text(), Field.Store.YES));
    				docList.add(customDoc);
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
		}
		return docList;
	}

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

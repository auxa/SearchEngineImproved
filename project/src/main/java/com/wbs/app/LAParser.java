package com.wbs.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LAParser {

	public LAParser() {}


	public ArrayList<Document> parseFile(){
		String docDir="../project/AssignmentTwo/latimes";
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

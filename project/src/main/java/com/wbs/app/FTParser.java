package com.wbs.app;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FTParser {

	public String FT_FOLDER = "../project/AssignmentTwo/ft";

	public FTParser() {}

	public ArrayList<Document> parseFile(){
		ArrayList<Document> docList = new ArrayList<Document>();
		File dir = new File(FT_FOLDER);
		File[] subDirs = dir.listFiles();
		if (subDirs == null) {
			return null;
		}
		for (File subDir : subDirs) {
			if(!subDir.isDirectory())
				continue;
			else{
				File[] docs = subDir.listFiles();
				for(File inputDoc : docs){
					if(inputDoc.exists() == false)
						continue;
    				try {
    					org.jsoup.nodes.Document doc = Jsoup.parse(inputDoc, "UTF-8");
						Elements documents = doc.getElementsByTag("DOC");
						for (Element docI: documents){
							String docNum= docI.getElementsByTag("DOCNO").text();
							Elements text= docI.getElementsByTag("TEXT");
							Elements dateElement = docI.getElementsByTag("DATE");
							String title= docI.getElementsByTag("HEADLINE").text();
							String textField= text.text();
							Document customDoc = new Document();
							customDoc.add(new TextField("id", docNum, Field.Store.YES));
							customDoc.add(new TextField("filename", title, Field.Store.YES));
							customDoc.add(new TextField("text", textField, Field.Store.YES));
							customDoc.add(new TextField("date", convertDate(dateElement.text()), Field.Store.YES));
							docList.add(customDoc);
							System.out.println("added doc number FT:  "+ docNum);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
    			}
			}
		}
		return docList;
	}
	
	private String convertDate(String date) throws ParseException {
	    DateFormat parser = new SimpleDateFormat("yymmdd");
	    Date formattedDate = parser.parse(date);

	    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	    return formatter.format(formattedDate);
	}
}



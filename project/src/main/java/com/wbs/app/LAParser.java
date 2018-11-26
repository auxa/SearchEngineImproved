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
					Elements text1= docI.getElementsByTag("GRAPHIC");

					String final_text = text.text() + text1.text();

    				Document customDoc = new Document();
    				customDoc.add(new TextField("id", docNum, Field.Store.YES));
    				customDoc.add(new TextField("filename", title.text(), Field.Store.YES));
    				customDoc.add(new TextField("text", final_text, Field.Store.YES));
    				customDoc.add(new TextField("date", date.text(), Field.Store.YES));
    				docList.add(customDoc);
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
		}
		return docList;
	}

}

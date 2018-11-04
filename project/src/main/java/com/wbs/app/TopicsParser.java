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

public class TopicsParser {

	public TopicsParser() {}


	public ArrayList<Document> parseFile(){
		String docDir="../project/topics";
		ArrayList<Document> docList = new ArrayList<Document>();
		File dir = new File(docDir);
    int index =0;
  	try {
  			  org.jsoup.nodes.Document doc = Jsoup.parse(dir, "UTF-8");
					Elements documents = doc.getElementsByTag("top");
  				for (Element docI: documents){
    			     String docNum=docI.getElementsByTag("num").text();
               String[] nums = docNum.split(" ");

               Elements title = docI.getElementsByTag("title");

               Elements desc = docI.getElementsByTag("desc");

               Elements narr= docI.getElementsByTag("narr");

    					 Document customDoc = new Document();
    					 customDoc.add(new TextField("id", nums[1], Field.Store.YES));
    					 customDoc.add(new TextField("title", title.text(), Field.Store.YES));
    					 customDoc.add(new TextField("desc", desc.text(), Field.Store.YES));
    					 customDoc.add(new TextField("narr", narr.text(), Field.Store.YES));
    					 docList.add(customDoc);

   				}
    		} catch (IOException e) {
    					e.printStackTrace();
    		}
		return docList;
	}

}

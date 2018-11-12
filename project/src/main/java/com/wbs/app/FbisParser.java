package com.wbs.app;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.FilenameFilter;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FbisParser{

    public FbisParser(){}

    public String FBIS_FOLDER = "../Assignment Two/fbis";

    public ArrayList<Document> parseFile() throws ParseException{
        ArrayList<Document> docList = new ArrayList<Document>();
        File dir = new File(FBIS_FOLDER);
        File[] docs = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if(name.contains("readchg") || name.contains("readmefb")) {
                    return false;
                }
                return  true;
            }
        });
        for(File inputDoc: docs) {
            try {
                org.jsoup.nodes.Document doc = Jsoup.parse(inputDoc, "UTF-8");
                Elements documents = doc.getElementsByTag("DOC");
                for (Element docI : documents) {
                    String docNum = docI.getElementsByTag("DOCNO").text();
                    String textField = docI.getElementsByTag("TEXT").text();
                    String dateElement = docI.getElementsByTag("DATE1").text();
                    // Capturing every F tag for now even the ones in Text field
                    String title = docI.getElementsByTag("HEADER").text() + " " + docI.getElementsByTag("ABS").text() + " " + docI.getElementsByTag("F").text();

                    Document customDoc = new Document();
                    customDoc.add(new TextField("id", docNum, Field.Store.YES));
                    customDoc.add(new TextField("filename", title, Field.Store.YES));
                    customDoc.add(new TextField("text", textField, Field.Store.YES));
                    customDoc.add(new TextField("date", convertDate(dateElement), Field.Store.YES));
                    docList.add(customDoc);
                    System.out.println("added doc number: " + docNum);
                }
                System.out.println(docList.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return docList;
    }

    private String convertDate(String date) throws ParseException {
        DateFormat parser = new SimpleDateFormat("dd MMMMM yyyy");
        Date formattedDate = parser.parse(date);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(formattedDate);
    }
}

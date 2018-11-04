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

/**
 * Hello world!
 *
 */
public class App {
  public static void main(String[] args) throws IOException {
    	TopicsParser myParser = new TopicsParser();
    	ArrayList<Document> myDocs = myParser.parseFile();

    	for(int i=0; i<1; i++){

    		System.out.println(myDocs.get(i).get("id"));
    		System.out.println(myDocs.get(i).get("title"));
            System.out.println("????????????????????????");
    		System.out.println(myDocs.get(i).get("desc"));
            System.out.println("????????????????????????");
    		System.out.println(myDocs.get(i).get("narr"));
    		System.out.println("///////////////////////////////////////////////////////////////////////////");

    	}
        System.out.println( "Hello World!" );
    }
}

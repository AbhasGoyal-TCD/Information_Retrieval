package agoyal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
 

public class CreateIndex
{
	/*
	 * Parameter args - user input for corpus and queries directories
	 * Parameter analyzer - type of analyzer used for creating index
	 * Parameter directoryname - appends to default directory location to create indexed directories
	 */
	public void Create_Index_with_Analyzer(String[] args, Analyzer analyzer, String directory_name ) throws IOException
	{
		// Make sure we were given something to index
		if (args[0].length() <= 0)
		{
            System.out.println("Expected corpus as input");
            System.exit(1);            
        }
		if (args[1].length() <= 0)
		{
            System.out.println("Expected queries as input");
            System.exit(1);            
        }
		
		//Set path for creating directories
		String INDEX_DIRECTORY_CORPUS = "../index_corpus" + directory_name;
		String INDEX_DIRECTORY_QUERIES = "../index_queries" + directory_name;
		
		//Variables to keep track of field and content to be stored in it
		String flag = "None";
		String info = "";
		
		// ArrayList of documents in the corpus
		ArrayList<Document> documents = new ArrayList<Document>();

		// Open the directory that contains the corpus
		Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY_CORPUS));
		
		// Set up an index writer to add process and save documents to the index
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		IndexWriter iwriter = new IndexWriter(directory, config);
		
		//Read the corpus line by line, identify fields and content and store in list of documents
		try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) 
		{
		    String line;
		    Document doc = new Document();
		    while ((line = br.readLine()) != null) 
		    {
		       
		       if(line.startsWith(".I"))
		       {
	    		   if(flag != "None")
	    		   {
	    			   doc.add(new TextField(flag, info, Field.Store.YES));
	    			   info = "";
	    			   flag = "None";
	    			   
	    			   documents.add(doc);
	    		   }
		    	   doc = new Document();
		    	   doc.add(new StringField("Doc_Number", line.substring(2), Field.Store.YES));
		    	   info = "";
		    	   flag = "None";

		       }
		       else 
		       {
		    	   if(line.equals(".T"))
			       {
		    		   //Handling erroneous symbols indicating start of new field incorrectly
		    		   if(flag == "Work")
		    		   {continue;}
		    		   if(flag != "None")
		    		   {
		    			   doc.add(new StringField(flag, info, Field.Store.YES));
		    			   info = "";
		    			   flag = "None";
		    		   }
			    	   flag = "Title";
			       }
		    	   else if(line.equals(".A"))
		    	   {
		    		 //Handling erroneous symbols indicating start of new field incorrectly
		    		   if(flag == "Work")
		    		   {continue;}
		    		   if(flag != "None")
		    		   {
		    			   doc.add(new TextField(flag, info, Field.Store.YES));
		    			   info = "";
		    			   flag = "None";
		    		   }
		    		   flag = "Author";
		    	   }
		    	   else if(line.equals(".W"))
		    	   {
		    		 //Handling erroneous symbols indicating start of new field incorrectly
		    		   if(flag == "Work")
		    		   {continue;}
		    		   if(flag != "None")
		    		   {
		    			   doc.add(new StringField(flag, info, Field.Store.YES));
		    			   info = "";
		    			   flag = "None";
		    		   }
		    		   flag = "Work";
		    	   }
		    	   else if(line.equals(".B"))
		    	   {
		    		 //Handling erroneous symbols indicating start of new field incorrectly
		    		   if(flag == "Work")
		    		   {continue;}
		    		   if(flag != "None")
		    		   {
		    			   doc.add(new StringField(flag, info, Field.Store.YES));
		    			   info = "";
		    			   flag = "None";
		    		   }
		    		   flag = "Bibliography";
		    	   }
		    	   else
		    	   {
		    		   info = info + " " + line;
		    	   }
		    	}
		      }
		    //add last document to list
		    doc.add(new TextField(flag, info, Field.Store.YES));
		    info = "";
		    flag = "None";
		   
		    documents.add(doc);
		}
		
		// Write all the documents in the linked list to the search index
		iwriter.addDocuments(documents);
		iwriter.close();
		directory.close();
		
		//Reset to start indexing query file
		documents.removeAll(documents);
		directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY_QUERIES));
		IndexWriterConfig config_queries = new IndexWriterConfig(analyzer);
		config_queries.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		iwriter = new IndexWriter(directory, config_queries);
		
		//Counter for queries
		int counter = 1;
		
		//Read the queries line by line, identify fields and content and store in list of documents
		try (BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
		    String line;
		    Document doc = new Document();
		    while ((line = br.readLine()) != null) 
		    {
		       if(line.startsWith(".I"))
		       {

	    		   if(flag != "None")
	    		   {
	    			   
	    			   doc.add(new StringField(flag, info, Field.Store.YES));
	    			   info = "";
	    			   flag = "None";
	    			   
	    			   documents.add(doc);
	    		   } 
	    		   
		    	   doc = new Document();
		    	   doc.add(new StringField("Query_Number", String.valueOf(counter), Field.Store.YES));
		    	   counter++;
		    	   info = "";
		    	   flag = "None";
		       }
		       else if(line.startsWith(".W"))
		       {		    		   
		    	   flag = "Query";
		       }
		       else
		       {
		    	   info = info + " " + line;
		       }
		   }
		    doc.add(new StringField(flag, info, Field.Store.YES));
		    info = "";
		    flag = "None";
		   
		    documents.add(doc);
		}
		// Write all the documents in the linked list to the search index
		iwriter.addDocuments(documents);
		
		// Commit everything and close
		iwriter.close();
		directory.close();		
	}
}

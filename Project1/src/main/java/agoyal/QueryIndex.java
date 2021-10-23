package agoyal;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class QueryIndex
{
	
	// Limit the number of search results we get
	private static int MAX_RESULTS = 100;
	
	/*
	 * Parameter similarity - scoring similarity to be used
	 * Parameter filename - appends analyzer and scoring for results file
	 * Parameter directory_name - appends to default directory location to create indexed directories
	 * Parameter analyzer - type of analyzer used for creating index
	 */
	public void Search(Similarity similarity, String filename, String directory_name,Analyzer analyzer ) throws IOException, ParseException
	{
		//Matrix to store the results of query hits
		float[][] results = new float[1400][225];

		//Set path for reading directories
		String INDEX_DIRECTORY_CORPUS = "../index_corpus" + directory_name;
		String INDEX_DIRECTORY_QUERIES = "../index_queries" + directory_name;
		
		// Open the folder that contains our search index
		Directory directory_corpus = FSDirectory.open(Paths.get(INDEX_DIRECTORY_CORPUS));

		// create objects to read and search across the index
		DirectoryReader ireader_corpus = DirectoryReader.open(directory_corpus); 
		IndexSearcher isearcher = new IndexSearcher(ireader_corpus);
		isearcher.setSimilarity(similarity);
		
		// create objects to query the index
		Directory directory_queries = FSDirectory.open(Paths.get(INDEX_DIRECTORY_QUERIES));
		DirectoryReader ireader_queries = DirectoryReader.open(directory_queries);
		MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {"Title","Work"},analyzer);
		
		//for each query, extract results and store in matrix
		for(int i=0; i <ireader_queries.numDocs();i++)
		{

			Query term1 = parser.parse(ireader_queries.document(i).get("Query").replace("?", ""));
	
			// Get the set of results from the searcher
			ScoreDoc[] hits = isearcher.search(term1, MAX_RESULTS).scoreDocs;
			
			// Store the results
			for (int j = 0; j < hits.length; j++)
			{
				Document hitDoc = isearcher.doc(hits[j].doc);
				results[Integer.valueOf(hitDoc.get("Doc_Number").trim())-1][i] = hits[j].score;
			}
		
		}

		//Output results to file for trec eval
		FileWriter myWriter = new FileWriter("Results_" + filename + ".txt");
		for(int i=0;i<225;i++)
		{
			for(int j=0;j<1400;j++)
			{
				myWriter.write(String.valueOf(i+1) + " Q0 " + String.valueOf(j+1) + " 0 " + results[j][i] + " " + filename + "\n");
				
			}
		}

		// close everything we used
		myWriter.close();
		ireader_corpus.close();
		ireader_queries.close();
		directory_corpus.close();
		directory_queries.close();
	}
}
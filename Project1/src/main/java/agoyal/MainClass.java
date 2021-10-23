package agoyal;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.analysis.CharArraySet;

public class MainClass {
	
	public static void main(String[] args) throws IOException
	{
		//Instantiate objects for indexing and querying
		CreateIndex CI = new CreateIndex();
		QueryIndex QI = new QueryIndex();
		
		//Creating custom stop word list for Stop and English Analzyer
		List<String> StopList = Arrays.asList(".","a","in","of","the","an","and","as","at","by","effects","experimental","flow","for","found","made","or","results","that","theory","this","to","was","were","with","be","been","boundary","can","from","has","have","is","it","layer","shown","are","presented","pressure","also","obtained","given","method","on","mach","number","these","two","which","but","such","then","no","will","not","their","if","into","there","they");
		CharArraySet StopLists = new CharArraySet(StopList, false);
		
		//Create Index using Simple analyzer and extract results for queries using BM 25 and VSM similarities
		Analyzer analyzer = new SimpleAnalyzer();
		CI.Create_Index_with_Analyzer(args, analyzer,"_Simple");
		try 
		{
			QI.Search(new BM25Similarity(),"Simple_BM25","_Simple",analyzer);
			QI.Search(new ClassicSimilarity(),"Simple_VSM","_Simple",analyzer);
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		System.out.println("Indexing and Quering Completed for Simple analyzer");
		//Create Index using Stop analyzer and extract results for queries using BM 25 and VSM similarities
		analyzer = new StopAnalyzer(StopLists);
		CI.Create_Index_with_Analyzer(args, analyzer,"_Stop");
		try 
		{
			QI.Search(new BM25Similarity(),"Stop_BM25","_Stop",analyzer);
			QI.Search(new ClassicSimilarity(),"Stop_VSM","_Stop",analyzer);
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		System.out.println("Indexing and Quering Completed for Stop analyzer");
		//Create Index using English analyzer and extract results for queries using BM 25 and VSM similarities
		analyzer = new EnglishAnalyzer(StopLists);
		CI.Create_Index_with_Analyzer(args, analyzer,"_English");
		try 
		{
			QI.Search(new BM25Similarity(),"English_BM25","_English",analyzer);
			QI.Search(new ClassicSimilarity(),"English_VSM","_English",analyzer);
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		System.out.println("Indexing and Quering Completed for English analyzer");
		
		analyzer.close();
	}

}
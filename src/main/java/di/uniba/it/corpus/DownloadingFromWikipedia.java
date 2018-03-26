package di.uniba.it.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.vocabulary.FOAF;

public class DownloadingFromWikipedia {
	
	public static void Download(String sito, int i) throws IOException
	{
		String cartella = "C:/Users/seven/Documents/RITA/Universita/MAGISTRALE/"
							+ "Semeraro Giovanni (Accesso intelligente all'informazione ed elaborazione del linguaggio naturale)/"
							+ "Tesi di Laurea/English Tourism/"
							+ "Wikipedia/";
		
		InputStream is = null;
	    URL url;
	    URLConnection urlConn = null;	    
	    
	    byte[] buffer = new byte[1024];
	    
	    int len;
		url = new URL(sito);
		urlConn = url.openConnection ();
		urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2)"
				+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");
		try
		{
			File fileOutput = new File(cartella + String.valueOf(i) + ".txt");
			FileOutputStream fos = new FileOutputStream (fileOutput);
			is = urlConn.getInputStream ();
			while ((len = is.read (buffer)) > 0)
				fos.write (buffer, 0, len);
			fos.close();			
		}
		catch (Exception e)
		{
			System.out.println("Il sito " + sito + " non c'è!!!");
		}		
	}


	public static void main(String[] args) {
		
		String queryString, queryString2, linkWikipedia, linkEsterni = null,temp = " ";
		
		QueryExecution qexec, qexec2;
		ResultSet results, results2;
		QuerySolution soln, soln2;
		
		File isDefinedBy;
		BufferedReader br;
		HashSet<String> siti = new HashSet<>();
		
		int i = 0;
				
		isDefinedBy = new File("C:/Users/seven/Documents/RITA/Universita/workspace/ontology/isDefinedBy.txt");
		try(FileReader fr = new FileReader(isDefinedBy))
		{
			br = new BufferedReader(fr);
			while (temp != null)
			{
				temp = br.readLine();
				if (temp != null)
				{
					queryString =
							"select ?linkWikipedia  where { "
							+ "?soggetto ?predicato <" + temp + ">. "
							+ "?soggetto <" + FOAF.isPrimaryTopicOf + "> ?linkWikipedia. "
							+ "}";
					System.out.println(queryString.replace(' ', '\n'));
					qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", queryString);
					results = qexec.execSelect();					
					
					while (results.hasNext())
					{
						soln = results.nextSolution();
						linkWikipedia = soln.get("linkWikipedia").asNode().toString();
						
						siti.add(linkWikipedia);
						
						System.out.println(linkWikipedia);
						
						queryString2 =
								"select ?linkEsterni  where { "
								+ "?soggetto <http://dbpedia.org/ontology/wikiPageExternalLink> ?linkEsterni. "
								+ "?soggetto <" + FOAF.isPrimaryTopicOf + "> <" + linkWikipedia + ">. "
								+ "}";
						System.out.println();
						System.out.println(queryString2.replace(' ', '\n'));
						qexec2 = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", queryString2);
						results2 = qexec2.execSelect();
						
						while (results2.hasNext())
						{							
							soln2 = results2.nextSolution();
							linkEsterni = soln2.get("linkEsterni").asNode().toString();
							
							siti.add(linkEsterni);
							
							System.out.println("\t\t" + linkEsterni);
						}
					}
					System.out.println();
				}
			}
		}
		catch (IOException e)
		{
			System.out.println("Non riesco a collegarmi al file isDefinedBy!");
			e.printStackTrace();
		}
		for (String temp2: siti)
		{
			++i;
			try
			{
				Download(temp2, i);
				System.out.println(temp2 + " è stato aggiunto!!!!");
			}
			catch (IOException e)
			{
				System.out.println("Non riesco a scaricare " + temp2); 
				e.printStackTrace();
			}			
		}
	}
}
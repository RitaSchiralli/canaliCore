package di.uniba.it.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class DownloadingGooglePages {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		InputStream is = null;
	    FileOutputStream fos = null;
	    
	    String sito = "https://www.google.it/search?num=10&hl=en&safe=off&q=",
	    		//seguito dalle parole da cercare intervallate da + ///url+Cycling_Infrastructure+point
	    cartella = "C:/Users/seven/Documents/RITA/Universita/MAGISTRALE/Semeraro Giovanni (Accesso intelligente all'informazione "
	    		+ "ed elaborazione del linguaggio naturale)/Tesi di Laurea/BootCat/Tourism in Apulia/",
	    salvaFile = cartella + "queries/",
	    tuples = cartella + "tuples.txt",
	    temp = null;
	    
	    File locale,
	    filetuples = new File(tuples);
	    
	    URL url;URLConnection urlConn = null;
	    
	    byte[] buffer = new byte[1024];
	    
	    int len;
	    Random r = new Random();
	    
	    FileReader tupleReader = new FileReader(filetuples);
	    BufferedReader br = new BufferedReader(tupleReader);
	    temp = " ";
	    while (temp != null)
	    {
	    	Thread.sleep(r.nextInt(100000));
	    	temp = br.readLine();
	    	if (temp != null)
	    	{
	    		System.out.println("Tuple: " + temp);
	    		System.out.println("Downloading " + salvaFile + temp + " - Google Search.html...");
	    		locale = new File(salvaFile + temp.replace(':', '_') + " - Google Search.html");
	    		url = new URL(sito + temp.replace(' ', '+'));
	    		urlConn = url.openConnection ();
	    		urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2)"
	    				+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36");
	    		is = urlConn.getInputStream ();
	    		fos = new FileOutputStream (locale);
	    		
	    		while ((len = is.read (buffer)) > 0)
	    			fos.write (buffer, 0, len);
	    	}
	    }
	    fos.close();
	    br.close();
	}
}
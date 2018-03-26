/**
 * 
 */
package di.uniba.it.corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.store.FSDirectory;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;

/**
 * @author rita
 *
 */

public class ExtractingWords {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws TikaException 
	 */
	
	private static String eliminaACapo(String daPerfezionare) {
		String perfezionata = daPerfezionare.replace("\n", " ");//sostituisce a capo con spazio
		for (int i = 0; i < 20; ++i)
			perfezionata = perfezionata.trim()//Returns a string whose value is this string, with any leading and trailing whitespace removed.
						.replace("	", " ")//sostituisce tab con spazio
						.replace("...", " ")//sostituisce tre punti con spazio
						.replace("___", " ")//sostituisce tre underscore con spazio
						.replace("  ", " ");//sostituisce due spazi con uno*/
		return perfezionata;
	}
		
	public static void main(String[] args) throws IOException{		
		int i,j;
		Tika tika = new Tika(), tika2 = new Tika();
		
		String path = "C:/Users/seven/Documents/RITA/Universita/MAGISTRALE/Semeraro Giovanni (Accesso intelligente all'informazione"
				+ " ed elaborazione del linguaggio naturale)/Tesi di Laurea/English Tourism/",
				text = null, stringFileTemp, stringFileSave = "./Word file.txt",type, stringCartFileTemp;
		FSDirectory directory = FSDirectory.open(Paths.get(path)), directory2;
		String[] files = directory.listAll(), files2;
		File fileTemp, fileTemp2;
		FileWriter writeFileSave = new FileWriter(stringFileSave);
		Metadata metadata = new Metadata(), metadata2 = new Metadata();
		FileInputStream is = null;
		for(i = 0;i < files.length;++i)
		{
			stringFileTemp = path + files[i];
			System.out.println("File Name:\n" + stringFileTemp);
			type = tika.detect(stringFileTemp);
			System.out.println("\tFile type:" + type);
			fileTemp = new File(stringFileTemp);
			if (type.compareTo("application/octet-stream") != 0)
			{
				is = new FileInputStream(fileTemp);
				try {
					text = tika.parseToString(is, metadata);
					if (text.compareTo("") != 0)
					{
						writeFileSave.write(eliminaACapo(text));
						writeFileSave.write("\n");
						System.out.println("Inserted");
					}
					else
						System.out.println("Ignored");
				is.close();
				} catch (TikaException | IOException e) {
					System.out.println("Is not possible to read the file.");
				}
			}
			else
			{
				System.out.println("Found folder:\n" + stringFileTemp);
				if (stringFileTemp.endsWith("_files"))
					System.out.println("Ignored");
				else
				{
					System.out.println("His files are:");
					directory2 = FSDirectory.open(Paths.get(stringFileTemp));
					files2 = directory2.listAll();
					for(j = 0;j < files2.length;++j)
					{
						stringCartFileTemp = stringFileTemp + "/" + files2[j];
						System.out.println("File Name:\n" + stringCartFileTemp);
						type = tika2.detect(stringCartFileTemp);
						System.out.println("\tFile type:" + type);
						fileTemp2 = new File(stringCartFileTemp);
						if ((type.compareTo("text/css") != 0) && (type.compareTo("application/octet-stream") != 0))
						{
							is = new FileInputStream(fileTemp2);
							try {
								text = tika2.parseToString(is, metadata2);
								if (text.compareTo("") != 0)
								{
									writeFileSave.write(eliminaACapo(text));
									writeFileSave.write("\n");
									System.out.println("Inserted");
								}
								else
									System.out.println("Ignored");
								is.close();
								} catch (TikaException | IOException e) {
									System.out.println("Is not possible to read the file.");
								}
						}
					}
				}
			}
		}
		writeFileSave.close();
	}
}

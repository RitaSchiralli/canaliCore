/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.it.nlpita.vectors.word2vec;

import di.uniba.it.nlpita.vectors.MemoryVectorReader;
import di.uniba.it.nlpita.vectors.ObjectVector;
import di.uniba.it.nlpita.vectors.Vector;
import di.uniba.it.nlpita.vectors.VectorReader;
import di.uniba.it.nlpita.vectors.utils.SpaceUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pierpaolo
 */
public class TestVectorReader {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
        	int i = 0;
        	//VectorReader vr = new MemoryVectorReader(new File("/home/gaetangate/Dev/nlp2sparql-data/abstract_200_20.w2v.bin"));
        	VectorReader vr = new MemoryVectorReader(new File("C:/Users/seven/Documents/RITA/Universita/MAGISTRALE/"
        			+ "Semeraro Giovanni (Accesso intelligente all'informazione ed elaborazione del linguaggio naturale)/"
        			+ "Tesi di Laurea/file per e con vettori/seconda versione/tourism.clean.w2v.bin"));//rita
            vr.init();
            System.out.println("Space dimensions: " + vr.getDimension());
            //System.out.println("tall");
            //Vector v1 = vr.getVector("tall");
            
            String DaCercare = "telephone";
            System.out.println(DaCercare);//rita
            Vector v1 = vr.getVector(DaCercare);//rita
           
            List<ObjectVector> nearestVectors = SpaceUtils.getNearestVectors(vr, v1, 100);
            for (ObjectVector ov : nearestVectors) {
                System.out.println(++i + "\t" + ov);
            }
            System.out.println();
        } catch (IOException ex) {
            Logger.getLogger(TestVectorReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

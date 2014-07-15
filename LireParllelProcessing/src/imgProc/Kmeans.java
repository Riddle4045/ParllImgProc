package imgProc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.bcel.internal.generic.NEW;

import net.semanticmetadata.lire.clustering.KMeans;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;


/***
 * Kmeans : Take set of observations obtained from SIFT features extraction and Cluster those observations
 * Deafult : Number of Clusters = 6
 * @author hduser
 * TODO : write now the features are read from the file 
 * connect the pipe from SIFT features to Kmeans Cluster class to avoid writing and reading from file
 * possibly switch to mahout clustering library
 */



public class Kmeans {
	
	public static  int NUM_CLUSTERS = 6;
	public static KMeans kmeans = new KMeans();

	
	
	public void readFeaturesFromFiles(File file ){
			
	}
	public void setNumberOfClusters(int num){
				
	}
	
	public void getNumberOfClusters(){
		
	}
	
	/**
	 * go through the file containing features
	 * and add all the features to be clustered
	 * @throws IOException 
	 */
	public void addAllImages(File file) throws IOException{
		
			FileInputStream fis = new FileInputStream(file); 
			//Construct BufferedReader from InputStreamReader
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = null;
			int count =0;
			while ((line = br.readLine()) != null) {
				System.out.println("Line number: "  + count);
				System.out.println(line);
				count++;
			}
			br.close();
	}
	
	/***
	 * Computes the clusters on the given set of features
	 */
	public void computeClusters(List<double[]> features){
			//from the features compue the clusters 
		setNumberOfClusters(NUM_CLUSTERS);
	}
}

package imgProc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.function.Abs;
import org.apache.jasper.tagplugins.jstl.core.ForEach;

import com.sun.org.apache.bcel.internal.generic.NEW;

import net.semanticmetadata.lire.clustering.Cluster;
import net.semanticmetadata.lire.clustering.KMeans;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;


/***
 * KmeansClustering : Take set of observations obtained from SIFT features extraction and Cluster those observations
 * This class a 1 level abstraction of the Kmeans class implemented by LIRE
 * Deafult : Number of Clusters = 6
 * @author hduser
 */



public class KmeansClustering {
	
	public static  int NUM_CLUSTERS = 6;
	private  double threshold = 1;
	private  KMeans kmeans = new KMeans();

	
	
	public void readFeaturesFromFiles(File file ){
			
	}
	
	
	public  void setNumberOfClusters(int num){
				kmeans.setNumClusters(NUM_CLUSTERS);
	}
	
	public void getNumberOfClusters(){
		
	}
	
	/**
	 * go through the file containing features
	 * and add all the features to be clustered
	 * @throws IOException 
	 */
	public void addAllImages(File file) throws IOException{
			//in case we have to read from the file
	}
	
	
	/***
	 * add the feature descriptor to kmeans
	 * @param ImageIdentifier
	 * @param features
	 */
	public void addFeatures(String ImageIdentifier , List<double[]> features){
				kmeans.addImage(ImageIdentifier, features);
	}
	
	/***
	 * Computes the clusters on the given set of features
	 */
	public void computeClusters(){
			//from the features compue the clusters 
		setNumberOfClusters(NUM_CLUSTERS);
		System.out.println("Computing Clusters");
		double old_stress = 0;	double current_error = 0.0;
		kmeans.init();
		do{
			double temp = kmeans.clusteringStep();
			current_error  = temp - old_stress;
			old_stress = temp;
			System.out.println(current_error);
		}while ( Math.abs(current_error) > threshold);
		System.out.println("finsihed");
	
	Cluster[] clusters = kmeans.getClusters();
	
	for (Cluster cluster : clusters) {
		System.out.println(cluster.toString());
	}

	
	
	
	}
	
}

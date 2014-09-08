package imgProc;


import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import edu.berkeley.compbio.jlibsvm.legacyexec.svm_train;






import net.semanticmetadata.lire.clustering.Cluster;
import net.semanticmetadata.lire.clustering.KMeans;



/***
 * KmeansClustering : Take set of observations obtained from SIFT features extraction and Cluster those observations
 * This class a 1 level abstraction of the Kmeans class implemented by LIRE
 * Deafult : Number of Clusters = 6
 * @author hduser
 */



public class KmeansClustering {
	
	public static  int NUM_CLUSTERS = 10;
	private  double threshold = 5;
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
	 * @throws IOException 
	 */
	public void computeClusters() throws IOException{
			//from the features compue the clusters 
		setNumberOfClusters(NUM_CLUSTERS);
		System.out.println("Creating visual vocabularly with traiing images");
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
		
	//these cluster are visual words
	for (Cluster cluster : clusters) {
		System.out.println(cluster.toString());
	}
	
	
	}
	
}

package imgProc;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;








import net.semanticmetadata.lire.clustering.Cluster;
import net.semanticmetadata.lire.clustering.KMeans;



/***
 * KmeansClustering : Take set of observations obtained from SIFT features extraction and Cluster those observations
 * This class a 1 level abstraction of the Kmeans class implemented by LIRE
 * Deafult : Number of Clusters = 6
 * @author hduser
 */



public class KmeansClustering {
	
	public static  int NUM_CLUSTERS = 200;
	private  double threshold = 2;
	private  KMeans kmeans = new KMeans();
	private static Cluster[] clusters = new Cluster[NUM_CLUSTERS];
	private static String  clusterFilePath = "/home/hduser/Documents/OpenCV-testing Images/ClusterFile.txt";
	
	
	public void readFeaturesFromFiles(File file ){
			
	}
	
	
	public  void setNumberOfClusters(int num){
				kmeans.setNumClusters(NUM_CLUSTERS);
	}
	
	public void getNumberOfClusters(){
		
	}
	
	public Cluster[] getClusters(){
		return clusters;
			
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
			//from the features compute the clusters 
		setNumberOfClusters(NUM_CLUSTERS);
		System.out.println("Creating visual vocabulary with base images");
		double old_stress = 0;	double current_error = 0.0;
		kmeans.init();
		do{
			double temp = kmeans.clusteringStep();
			current_error  = temp - old_stress;
			old_stress = temp;
			System.out.println(current_error);
		}while ( Math.abs(current_error) > threshold);
		System.out.println("Conveged to "+current_error + " < than "+threshold);
		System.out.println("finsihed creating vocabulary");
	
	clusters = kmeans.getClusters();
	writeClustersToFile(clusterFilePath);
	
	}
	
	public void writeClustersToFile(String FilePath) throws IOException{
			File clusterFile = new File(FilePath);
			BufferedWriter fs = new BufferedWriter(new FileWriter(clusterFile));
			for (Cluster cluster : clusters) {
				fs.write(cluster.toString());
			}
			
	}
	public static void readClusterInformation(String cluster_file_path) throws IOException {
							File clusterFile = new File(cluster_file_path);
							if ( cluster_file_path == " "){
											cluster_file_path = clusterFilePath;
							}
							BufferedReader buf = new BufferedReader(new FileReader(clusterFile));
							String line;
							while ( (line = buf.readLine()) != null){
											//store the information in CLusters[]?
							}
	}
	
}

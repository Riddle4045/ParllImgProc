package imgProc;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.collections.map.HashedMap;
import org.imgscalr.Scalr;

import net.semanticmetadata.lire.clustering.Cluster;
import net.semanticmetadata.lire.clustering.KMeans;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;

import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.SvmProblem;
import edu.berkeley.compbio.jlibsvm.legacyexec.svm_predict;
import edu.berkeley.compbio.jlibsvm.legacyexec.svm_train;

public class MultiClassSVMclassification {

	public MultiClassSVMclassification(){

	}

	public static String train_img = "/home/hduser/Documents/OpenCV-testing Images/train";
	public static String destination_path = "/home/hduser/Documents/OpenCV-testing Images/TrainingImageFeatures.txt";
	public static ArrayList<Integer[]> quantized_images = new ArrayList<>();
	private static MserSiftFeatureOperations mserFileOperations = new MserSiftFeatureOperations("");
	private static KmeansClustering kmeans = new KmeansClustering();
	private static Map<Integer,Map<Integer,Integer> >  trainDataSVM =null;
	
	public static void main(String[] args) throws IOException {
		mserFileOperations._init_("");
		_init_(train_img);

	}


	public static void _init_(String train_images) throws IOException{	
		//handle a image at a time , get the image , extract the features , quantize the features.
		int label = 0;
		File dir = new File(train_images);
		File[] directoryListing = dir.listFiles();
		System.out.println("directory size:"+directoryListing.length);
		if (directoryListing != null) {
			System.out.println("Quantizing training images for SVM-trainer");
			for (File child : directoryListing) {
				// Do something with child

				if ( child.isFile()){
					BufferedImage img = ImageIO.read(child);
					//in case we are dealing with thumbnails 
					//scaling will not and should not affect SIFT features ( scale invariant )
					if ( img.getTileHeight() < 64 || img.getTileWidth() < 64  ){
						img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 100, null);
					}
					System.out.println("fetching training features"+child.getAbsolutePath());
					List<Feature> temp_features = MserSiftParallel.getSiftMserFeatures(img);
					quantizeFeatures(temp_features,label);
					temp_features.clear();
					img.flush();
				}else {
					label++;
					File[] newfiles = child.listFiles();
					for (File file : newfiles) { 			    	  		    	
						BufferedImage img = ImageIO.read(file);   		    	
						//in case we are dealing with thumbnails 
						//scaling will not and should not affect SIFT features ( scale invariant )
						if ( img.getTileHeight() < 64 || img.getTileWidth() < 64  ){
							img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 100, null);
						}		    		    	
						System.out.println("fetching features for "+file.getAbsolutePath());
						List<Feature> temp_features = MserSiftParallel.getSiftMserFeatures(img);
						quantizeFeatures(temp_features,label);
						temp_features.clear();
						img.flush();
					}
				}}}
		writeLibSvmDataToFile();
	}

	public static void 	quantizeFeatures(List<Feature> features,Integer label) throws IOException {
				ArrayList<double[]>  descriptors = new ArrayList<double[]>();
				for (Feature feature : features) {
							double[] des = feature.descriptor;
							descriptors.add(des);
		}
				assignVisualWord(descriptors,label);

	}

	public static void assignVisualWord(ArrayList<double[]> feature_vector,Integer label){
					Integer[] histogram  = new Integer[KmeansClustering.NUM_CLUSTERS];
					Arrays.fill(histogram, new Integer(0));
					Cluster[] clusters = kmeans.getClusters();
					int word_index =0;
					double min_distance =Double.MAX_VALUE; double temp;
					for (double[] feature : feature_vector) {
							
					 for(int i =0 ; i < KmeansClustering.NUM_CLUSTERS;i++){
						 	 temp = clusters[i].getDistance(feature);
						 	 if ( temp < min_distance){
						 		 		word_index = i;
						 		 		min_distance = temp;
						 	 }
					 }
				//	 System.out.println("DEBUG: word_index"+word_index);
				//	 System.out.println("DEBUG: word_index"+histogram.length);
					 histogram[word_index]++;
					 word_index= 0;
					 min_distance = Double.MAX_VALUE;
					 quantized_images.add(histogram);
					}	
					convertToLibSVMtrainData(histogram, label);
					//printUtility(quantized_images);
	}

	public static void printUtility(ArrayList<Integer[]> hist){
		
		//iamges converted to visual words
		System.out.println("ENCODED  TRAINING IMAGES");
					for (Integer[] image : hist) {
								System.out.println(image.toString());
						}
	}
	
	public static void convertToLibSVMtrainData(Integer[] histogram, Integer label){
					//now just append values to train data.
					//once done , write to file to be safe.
					int counter = 1;
					Map<Integer, Integer> trainValue = new HashMap<Integer,Integer>();
					for (Integer integer : histogram) {
						trainValue.put(counter,integer);
						counter++;
					}
						trainDataSVM.put(label, trainValue);
	}
	public static void writeLibSvmDataToFile(){
					for(Entry<Integer, Map<Integer, Integer>> entrySet : trainDataSVM.entrySet()){
										System.out.println(entrySet.getKey()+" "+entrySet.getValue().toString());
					}
	}

}

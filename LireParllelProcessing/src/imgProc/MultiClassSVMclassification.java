package imgProc;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;




import org.imgscalr.Scalr;

import net.semanticmetadata.lire.clustering.Cluster;
import net.semanticmetadata.lire.clustering.KMeans;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;



public class MultiClassSVMclassification {

	public MultiClassSVMclassification(){

	}

	public static String train_img = "/home/hduser/Documents/OpenCV-testing Images/Caltech 256/train";
	public static String test_img = "/home/hduser/Documents/OpenCV-testing Images/Caltech 256/train";
	public static String destination_path = "/home/hduser/Documents/OpenCV-testing Images/TrainingImageFeatures.txt";
	public static String destination_path_test = "/home/hduser/Documents/OpenCV-testing Images/TestingImageFeatures.txt";

	public static ArrayList<Integer[]> quantized_train_images = new ArrayList<Integer[]>();
	public static ArrayList<Integer[]> quantized_test_images = new ArrayList<Integer[]>();
	private static MserSiftFeatureOperations mserFileOperations = new MserSiftFeatureOperations("");
	private static KmeansClustering kmeans = new KmeansClustering();
	private static String training_data_file = "/home/hduser/Documents/OpenCV-testing Images/trainingData.txt";
	private static String testing_data_file = "/home/hduser/Documents/OpenCV-testing Images/testingData.txt";
	//trainDataSVM needs to be of the type 
	//Label  <index1>:<value1> <index2>:<value2>.....
	//private static  MultiValueMap<Integer, Integer[]> trainDataSVM ;

	
	public static void main(String[] args) throws IOException {
		mserFileOperations._init_("");
		_init_(train_img,true,destination_path);
		_init_(test_img,false,destination_path_test);

	}
	
	

	public static void _init_(String file_path,Boolean train,String destination_filepath) throws IOException{	
		//handle a image at a time , get the image , extract the features , quantize the features.
		int label = 0;
		File dir = new File(file_path);
		File[] directoryListing = dir.listFiles();
		System.out.println("directory size:"+directoryListing.length);
		if (directoryListing != null) {
		if(train)	{
			System.out.println("Quantizing training images for SVM-trainer");
		}else{
				System.out.println("Preparing testing images");
		}
			for (File child : directoryListing) {
				// Do something with child

				if ( child.isFile()){
					BufferedImage img = ImageIO.read(child);
					//in case we are dealing with thumbnails 
					//scaling will not and should not affect SIFT features ( scale invariant )
			    	if ( img.getTileHeight() < 64 || img.getTileWidth() < 64  ){
	    				img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 400	, 400, null);
	    	}
	    	
	    	if (img.getTileHeight() >  800 || img.getTileWidth() > 800){
	    					img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 400	, 400, null);
	    	}
					System.out.println("fetching training features"+child.getAbsolutePath());
					List<Feature> temp_features = MserSiftParallel.getSiftMserFeatures(img);
					writeToFile(temp_features, destination_filepath);
					quantizeFeatures(temp_features,label,train);
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
		    				img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 400	, 400, null);
		    	}
		    	
		    	if (img.getTileHeight() >  800 || img.getTileWidth() > 800){
		    					img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 400	, 400, null);
		    	}	    		    	
						System.out.println("fetching features for "+file.getAbsolutePath());
						List<Feature> temp_features = MserSiftParallel.getSiftMserFeatures(img);
						writeToFile(temp_features, destination_filepath);
						quantizeFeatures(temp_features,label,train);
						temp_features.clear();
						img.flush();
					}
				}}}
		
	}

	public static void 	quantizeFeatures(List<Feature> features,Integer label,Boolean train) throws IOException {
				ArrayList<double[]>  descriptors = new ArrayList<double[]>();
				for (Feature feature : features) {
							double[] des = feature.descriptor;
							descriptors.add(des);
		}
				assignVisualWord(descriptors,label,train);

	}

	public static void assignVisualWord(ArrayList<double[]> feature_vector,Integer label,Boolean train) throws IOException{
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
					if(train){
						quantized_train_images.add(histogram);
					}else {
							quantized_test_images.add(histogram);
					}
					}
					if (train){
					writeLibSvmDataToFile(histogram,label,"");
					//convertToLibSVMtrainData(histogram, label);
					//printUtility(quantized_train_images);
					}else {
							writeLibSvmDataToFile(histogram, label, testing_data_file);
					}
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
					//	trainDataSVM.put(label, trainValue);
					
	}
	public static void writeLibSvmDataToFile(Integer[] hist,Integer label,String file_path) throws IOException{
					
					File file;
					if (file_path == "") {
						 file = new File(training_data_file);
					}else {
							 file = new File(file_path);
					}
					BufferedWriter buf = new BufferedWriter(new FileWriter(file,true));
					StringBuilder sb = new StringBuilder();
					int counter =  1;
					sb.append(label);
					sb.append(" ");
					for (Integer integer : hist) {
						sb.append(counter);
						sb.append(":");
						sb.append(integer);	
						sb.append(" ");
						counter++;
					}
					counter = 1;
					buf.append(sb);
					buf.newLine();
					buf.close();			
					buf.close();				
	}
	
	public static void writeToFile(List<Feature> sift_features,String destination_path) throws IOException{
		

		File file = new File(destination_path);
        BufferedWriter output = new BufferedWriter(new FileWriter(file));


		for (Feature feature : sift_features) {
	  		double[] descriptor_values = feature.descriptor;
	  		for (double d : descriptor_values) {
	  			String str = Double.toString(d);
	  			output.write(" " +str);
			}
	  		output.newLine();
        }	
		output.close();
	}

}

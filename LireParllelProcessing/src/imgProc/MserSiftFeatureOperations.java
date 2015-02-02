package imgProc;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.clustering.KMeans;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;



import org.imgscalr.Scalr;

/**
 * Entry class for the following
 * MSER-SIFT feature extraction 
 * Clustering the base images to create visual vocabulary.
 * Quantize traiing images to visual vocabulary.
 * Train a SVM classifier with training image.
 * Test the image labels on the images.
 * Cross validate to get PR numbers. 
 * @author hduser
 *
 */
public class MserSiftFeatureOperations {
	
	
	
	//deafult base_image_path for the testing set on local system
	//public static String base_image_path = "/home/hduser/Dropbox/Disambiguation Project/Tank use case data/ImagNet";
	//public static String base_image_path = "/Users/Ishan/Documents/Pictures/ImagePro/TrainSet/";
	//public static String base_image_path = "/home/hduser/Documents/OpenCV-testing Images/train";
	public static String base_image_path ="/home/hduser/Documents/OpenCV-testing Images/Caltech 256/BaseImages";
	//deafult path where all the descriptors are written.
	
	//public static String filePath = "/Users/Ishan/Documents/mserSiftFeatures.txt";
	public static String filePath = "/home/hduser/Documents/OpenCV-testing Images/BaseImageMserSiftFeatures.txt";
	//handle for the mserSiftParallel class , that does all the operations
	public static MserSiftParallel mserSift = new MserSiftParallel();
	//handle for Clustering Class kmeansClustering
	public static KmeansClustering kMeansClustering = new KmeansClustering();
	
	
	public MserSiftFeatureOperations(String base_image_path){
				if ( base_image_path !=""){
						setDirPath(base_image_path);
				}
	}

	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if ( args.length > 0 ){
			setDirPath(args[0]);
			setOutputFilePath(args[1]);
		}
		getAllSiftMserFeatures(base_image_path,"",true);
	};

	public static void _init_(String baseImages_path) throws IOException {
		if (baseImages_path == ""){
		getAllSiftMserFeatures(base_image_path,"",true);
		}else {
			getAllSiftMserFeatures(baseImages_path, "", true);
		}
	}
	/**
	 * set the data path through command line if entered
	 * @param path
	 */
	public static void setDirPath(String path) {
					
					if ( path != null ) {
									base_image_path = path;
					}
	
	}
	
	/**
	 * set the path of output file containing all the descriptors
	 * @param file_path
	 */
	public static void setOutputFilePath(String file_path) {
							if ( file_path != null){
									filePath = file_path;
							}
	}
	
	/**
	 * iterate through the directory structure 
	 * get the MSER-SIFT features and write them to a file
	 * @throws IOException 
	 */
	public static void getAllSiftMserFeatures(String file_path,String dest_path, Boolean feedToKmeans) throws IOException {
		if (file_path == ""){
				file_path = base_image_path;
		}
		if(dest_path == ""){
					dest_path = filePath;
		}
		  File dir = new File(file_path);
		  File[] directoryListing = dir.listFiles();
		  System.out.println("directory size:"+directoryListing.length);
		  int count  = 1;
		  if (directoryListing != null) {
			  System.out.println("Extracting MSER features for Visual Vocab");
		    for (File child : directoryListing) {
		      // Do something with child
		    	
		    		if ( child.isFile()){
		    			System.out.println("Reading "+child.getAbsolutePath().toString());
		    		BufferedImage img = ImageIO.read(child);
		    	
		    	
		    	//in case we are dealing with thumbnails 
		    	//scaling will not and should not affect SIFT features ( scale invariant )
		    	if ( img.getTileHeight() < 64 || img.getTileWidth() < 64  ){
		    				img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 400	, 400, null);
		    	}
		    	
		    	if (img.getTileHeight() >  800 || img.getTileWidth() > 800){
		    					img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 400	, 400, null);
		    	}
		    	
		    	System.out.println("fetching features for"+count+" image "+child.getAbsolutePath());
		    	count++;
		    	List<Feature> temp_features = MserSiftParallel.getSiftMserFeatures(img);
		    	writeToFile(temp_features,dest_path);
		     	if(feedToKmeans){
    		    	feedToKmeans(child.getName(),temp_features);
    		    	}
		    	img.flush();
		    		}else {
		    				File[] newfiles = child.listFiles();
		    				System.out.println("Directory Size"+newfiles.length);
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
		    		    	System.out.println("fetching features for"+count+" image "+file.getAbsolutePath());
		    		    	count++;
		    		    	List<Feature> temp_features = MserSiftParallel.getSiftMserFeatures(img);
		    		    	writeToFile(temp_features,dest_path);
		    		    	if(feedToKmeans){
		    		    	feedToKmeans(child.getName(),temp_features);
		    		    	}
		    		    	img.flush();
		    		}
		    }
		  }} else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
			  System.out.println("Some wrong entry in directory check");
		  }
		  
		  	if(feedToKmeans){
		  //when all the features are extracted and dealt with 
		  //perform kmeans clustering on them
		  kMeansClustering.computeClusters();
		  	}
	
	}
	
	/**
	 * get MSER-SIFT features for a image and return the list
	 * @useCase: while quantizing the training images.
	 * @param img : target image.
	 * @param Identifier : image_name;
	 * @param feedToKmeans : true if the features needed to be clustered through Kmeans.
	 * @return
	 * @throws IOException
	 */
	public static List<Feature> getAllSiftMserFeaturesForImage(BufferedImage img,String Identifier,Boolean feedToKmeans) throws IOException{
    	//in case we are dealing with thumbnails 
    	//scaling will not and should not affect SIFT features ( scale invariant )
    	if ( img.getTileHeight() < 64 || img.getTileWidth() < 64  ){
			img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 400	, 400, null);
}

if (img.getTileHeight() >  800 || img.getTileWidth() > 800){
				img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 400	, 400, null);
}
    	List<Feature> temp_features = MserSiftParallel.getSiftMserFeatures(img);
     	if(feedToKmeans){
	    	feedToKmeans(Identifier,temp_features);
	    	}
    	img.flush();
    	return temp_features;
	}
	
	/**
	 * Write the descriptors of  extracted features to a single File
	 * @throws IOException 
	 * 
	 */
	public static void writeToFile(List<Feature> sift_features,String destination_path) throws IOException{
	
		if ( destination_path == "" ){
			destination_path = filePath;
		}
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

	/***
	 *Calculate SIFT-MSER features for an Image and feed the features to clustering Engine. 
	 * @param ImageIdentifier : Name or any string uniquely identifying Image
	 * @param features : MSER-SIFT features extracted from the image.
	 */
	public static void feedToKmeans(String ImageIdentifier, List<Feature> features){
						ArrayList<double[]>  descriptors = new ArrayList<double[]>();
						for (Feature feature : features) {
							double[] des = feature.descriptor;
							descriptors.add(des);
						}
						//TODO : resolve this fuckery.
						kMeansClustering.addFeatures(ImageIdentifier, descriptors);
	}
}

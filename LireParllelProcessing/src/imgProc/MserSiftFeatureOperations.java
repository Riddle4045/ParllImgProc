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

public class MserSiftFeatureOperations {
	
	
	
	//deafult dir_path for the testing set on local system
	//public static String dir_path = "/home/hduser/Dropbox/Disambiguation Project/Tank use case data/ImagNet";
	//public static String dir_path = "/Users/Ishan/Documents/Pictures/ImagePro/TrainSet/";
	public static String dir_path = "/home/hduser/Documents/OpenCV-testing Images/BaseImages/";
	//deafult path where all the descriptors are written.
	
	//public static String filePath = "/Users/Ishan/Documents/mserSiftFeatures.txt";
	public static String filePath = "/home/hduser/Documents/mserSiftFeatures.txt";
	//handle for the mserSiftParallel class , that does all the operations
	public static MserSiftParallel mserSift = new MserSiftParallel();
	//handle for Clustering Class kmeansClustering
	public static KmeansClustering kMeansClustering = new KmeansClustering();
	
	
	public MserSiftFeatureOperations(String dir_path){
				if ( dir_path !=""){
						setDirPath(dir_path);
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
		}
		if ( args.length > 0){
					setOutputFilePath(args[1]);
		}
		getAllSiftMserFeatures();
			
	};

	
	/**
	 * set the data path through command line if entered
	 * @param path
	 */
	public static void setDirPath(String path) {
					
					if ( path != null ) {
									dir_path = path;
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
	public static void getAllSiftMserFeatures() throws IOException {
		
		  File dir = new File(dir_path);
		  File[] directoryListing = dir.listFiles();
		  System.out.println("directory size:"+directoryListing.length);
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		      // Do something with child
		    	
		    		if (child.isFile()){
		    			System.out.println("File:"+child.getAbsolutePath());
		    		}else{
		    				System.out.println("Not a file!");
		    		}
		    		BufferedImage img = ImageIO.read(child);
		    	
		    	
		    	//in case we are dealing with thumbnails 
		    	//scaling will not and should not affect SIFT features ( scale invariant )
		    	if ( img.getTileHeight() < 64 || img.getTileWidth() < 64  ){
		    				img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 100, null);
		    	}
		    	
		    	
		    //	System.out.println("fetching features for "+child.getAbsolutePath());
		    	List<Feature> temp_features = MserSiftParallel.getSiftMserFeatures(img);
		    	writeToFile(temp_features);
		    	feedToKmeans(child.getName(),temp_features);
		    	img.flush();
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
			  System.out.println("Some wrong entry in directory check");
		  }
		  
		  //when all the features are extracted and dealt with 
		  //perform kmeans clustering on them
		  kMeansClustering.computeClusters();
	
	}
	
	/**
	 * Write the descriptors of  extracted features to a single File
	 * @throws IOException 
	 * 
	 */
	public static void writeToFile(List<Feature> sift_features) throws IOException{
	
		if ( filePath == null ){
					System.out.println("set the output file path");
		}
		File file = new File(filePath);
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

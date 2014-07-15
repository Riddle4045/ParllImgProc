package imgProc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.imageanalysis.sift.Feature;

import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;
import org.imgscalr.Scalr;

public class MserSiftFileOperations {
	
	
	
	//deafult dir_path for the testing set on local system
	//public static String dir_path = "/home/hduser/Dropbox/Disambiguation Project/Tank use case data/ImagNet";
	public static String dir_path = "/home/hduser/Documents/Kaggle-CFAIR/sample_train_set2";
	//deafult path where all the descriptors are written.
	public static String filePath = "/home/hduser/Documents/Kaggle-CFAIR/features1.txt";
	public static MserSiftParallel mserSift = new MserSiftParallel();
	
	public static Kmeans kmeans = new Kmeans();
	
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
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		      // Do something with child
		    	BufferedImage img = ImageIO.read(child);
		    	if ( img.getTileHeight() < 64 || img.getTileWidth() < 64  ){
		    				img = Scalr.resize(img, Scalr.Method.AUTOMATIC, 100, null);
		    	}
		    	System.out.println("fetching features for "+child.getAbsolutePath());
		    	List<Feature> temp_features = mserSift.getSiftMserFeatures(img);
		    	System.out.println("Writing the file "+child.getAbsolutePath());
		    	writeToFile(temp_features);
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
			  System.out.println("Some wrong entry in directory check");
		  }
		  clusterGatheredFeatures();
	}
	
	/**
	 * Write the extracted features to a single File
	 * @throws IOException 
	 * 
	 */
	public static void writeToFile(List<Feature> sift_features) throws IOException{
	
		if ( filePath == null ){
					System.out.println("set the output file path");
		}
		File file = new File(filePath);
		
		FileOutputStream fs = new FileOutputStream(file, true);
		FileWriter fw = new FileWriter(file);
		for (Feature feature : sift_features) {
				//System.out.println(feature.toString());
				String str = feature.toString();
				fw.write(feature.toString());
		}
		fs.close();
		fw.close();
	}
	
	
	public static void clusterGatheredFeatures() throws IOException{
		 System.out.println("Clustering features");
		   File file = new File(filePath);
			kmeans.addAllImages(file);
			return;
	}
}

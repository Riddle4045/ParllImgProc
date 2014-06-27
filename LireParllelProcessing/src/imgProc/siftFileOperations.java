package imgProc;

import java.io.File;

import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;

public class siftFileOperations {
	
	
	
	//deafult dir_path for the testing set on local system
	public static String dir_path = "/home/hduser/Dropbox/Disambiguation Project/Tank use case data/ImagNet";
	public static String filePath = "/home/hduser/Documents/ParllImgProc/features.txt";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if ( args[0] != null){
			setDirPath(args[0]);
		}
		if ( args[1] != null){
					setOutputFilePath(args[1]);
		}
		
			
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
	 */
	public static void getSiftMserFeatures() {
		
		  File dir = new File(dir_path);
		  File[] directoryListing = dir.listFiles();
		  if (directoryListing != null) {
		    for (File child : directoryListing) {
		      // Do something with child
		    	
		    }
		  } else {
		    // Handle the case where dir is not really a directory.
		    // Checking dir.isDirectory() above would not be sufficient
		    // to avoid race conditions with another process that deletes
		    // directories.
			  System.out.println("Some wrong entry in directory check");
		  }
	}
	
	/**
	 * Write the extracted features to a single File
	 * 
	 */
	public static void writeToFile(){
	
		if ( filePath == null ){
					System.out.println("set the output file path");
		}
		File file = new File(filePath);					
	}
}

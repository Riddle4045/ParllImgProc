package imgProc;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import net.semanticmetadata.lire.imageanalysis.sift.Feature;

import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.legacyexec.svm_train;

public class MultiClassSVMclassification {

	public MultiClassSVMclassification(){

	}

	public static String train_img = "/home/hduser/Documents/OpenCV-testing Images/train";
	public static String destination_path = "/home/hduser/Documents/OpenCV-testing Images/TrainingImageFeatures.txt";
	public static ArrayList<double[]> quantized_images = new ArrayList<>();

	private static MserSiftFeatureOperations mserFileOperations = new MserSiftFeatureOperations(train_img);

	public static void main(String[] args) throws IOException {

		_init_(train_img);

	}


	public static void _init_(String train_images) throws IOException{	
		//handle a image at a time , get the image , extract the features , quantize the features.

		File dir = new File(train_images);
		File[] directoryListing = dir.listFiles();
		System.out.println("directory size:"+directoryListing.length);
		if (directoryListing != null) {
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
					quantizeFeatures(temp_features);
					temp_features.clear();
					img.flush();
				}else {
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
						quantizeFeatures(temp_features);
						temp_features.clear();
						img.flush();
					}
				}}}
	}

	public static void 	quantizeFeatures(List<Feature> features) throws IOException {
			

	}

	public static void getClosestCenter(ArrayList<Double> feature_vector){

	}





}

package imgProc;

import java.io.File;
import java.io.IOException;

import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.legacyexec.svm_train;

public class MultiClassSVMclassification {

				//Things to code here
	
				//We have the visual vocabulary available from k-means clustering.
				//This class trains the Multiclass svm classifier
				//training labels are gathered from  "k" dimmenional vectors ( feature histrograms ) for each training image..
				//test images are fed to this trained classifier to predict labels..
	
	
				//so far we just have the clusters from the visual vocab created through  k-means.
				
	
				//eucleadian distance will be a measure of similarity to assign descriptors of a traiing image to a vord.
	
	
				//from the path passed in the argument get the directory structure
				//from the directory structure get the files->features.
				
				//write a helper function to create the historgram for the images and its features.
				//write another helper function to write all those quantized features in a file.
	
				public static String train_img = "/home/hduser/Documents/OpenCV-testing Images/train";
				public static String destination_path = "/home/hduser/Documents/OpenCV-testing Images/TrainingImageFeatures.txt";
				
				private static MserSiftFeatureOperations mserFileOperations = new MserSiftFeatureOperations(train_img);
				
				
				
				public static void writeTrainingImageFeatures(String train_images) throws IOException{	
									
						File dir = new File(train_img);
						File[] files =	dir.listFiles();
						for (File file : files) {
								if(!file.isFile()){
										File[] newFile  = file.listFiles();
										for (File newfile : newFile) {
													mserFileOperations.getAllSiftMserFeatures(train_img,destination_path);
										}
								}
						}
					}
				
				public static void 	quantizeFeatures(String source_file_path) throws IOException {
						//read the file line by line.
						//caculate the euclidean distance from all the points 
						File source_file = new File(source_file_path);
						
				}
				

	
				
				
}

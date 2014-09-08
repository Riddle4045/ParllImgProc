package imgProc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import edu.berkeley.compbio.jlibsvm.SVM;
import edu.berkeley.compbio.jlibsvm.legacyexec.svm_train;

public class MultiClassSVMclassification {
	
				public MultiClassSVMclassification(){
						
				}

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
				
				public static void main(String[] args) throws IOException {
										writeTrainingImageFeatures(train_img);
										quantizeFeatures(destination_path);
				}
				
				
				public static void writeTrainingImageFeatures(String train_images) throws IOException{	
						mserFileOperations.getAllSiftMserFeatures(train_img,destination_path);
					}
				
				public static void 	quantizeFeatures(String source_file_path) throws IOException {
						//read the file line by line.
						//Calculate the euclidean distance from all the points 
						File source_file = new File(source_file_path);
						BufferedReader br = new BufferedReader(new FileReader(source_file));
						String line;
						while ((line = br.readLine()) != null) {
										//now we have a the vector in this line 
										//we need to extract all the numbers.
										//get the euclidean distance from which ever words they are closest to
										//and incremenet the vector with those words as index by 1 to reate the histrogram.
										//once this histrogram is done we will feed it to mSVM to train and test images.
										
						}
						br.close();
				}
				

	
				
				
}

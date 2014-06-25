package imgProc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.imageanalysis.LireFeature;
import net.semanticmetadata.lire.imageanalysis.bovw.SiftFeatureHistogramBuilder;
import net.semanticmetadata.lire.imageanalysis.mser.*;
import net.semanticmetadata.lire.imageanalysis.sift.Extractor;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;
import net.semanticmetadata.lire.imageanalysis.sift.Point;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;

/**
 * This class currently Iteates through a directory structure 
 * calculates MSER over SIFT features for all the image files and throws out a text file with all the features
 * @author Ishan Patwa<br>
 * TODO : use sequence file input and parallize the process
 */
public class MserSiftParallel {
	
		public static int numFeatures = 0;
		public static MSERGrowthHistory[] mser_blobs;
		public static MSER mser = new MSER();
		public static Extractor sift_extractor = new Extractor();
		public static List<Feature> sift_features ;
		public static List<Feature> sift_filtered_features;
		public static ImagePoint[] points;  // coordinates of the extracted mser_regions
		public static List<ImagePoint> point_list= null; //list of the points extracted from mser_regions
		 
		
		public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
			// TODO Auto-generated method stub
			File file = new File("/home/hduser/Pictures/Tank.jpg");
			BufferedImage img = ImageIO.read(file);
			//getSiftOverMser(img);
			setSIFTfeatures(img);
			
		}
		
	/**
	 * set SIFT features	
	 */
	public static void  setSIFTfeatures(BufferedImage img) throws IOException {
				sift_features = sift_extractor.computeSiftFeatures(img);
		}
	
	/**
	 * Compute MSER features for a given Image
	 */
	public static void setMserRegions(BufferedImage img){			
			   	mser_blobs	=  mser.extractMSER(img);
		}
	
	/**
	 * appending coordinates that are extracted as part of mser_blobs to a list
	 * @param nser_blobs
	 */
	 public static void setMserCoordinates(MSERGrowthHistory[] mser_blobs){
						for (MSERGrowthHistory mserGrowthHistory : mser_blobs) {
									point_list = Arrays.asList(mserGrowthHistory.getPoints());
						}
	}
	
	/** 
	 * set overlapping MSER and SIFT features
	 * @param img
	 * @throws IOException 
	 */
	public static void getSiftOverMser(BufferedImage img) throws IOException{
						//obtain SIFT features and MSER blobs
						setMserRegions(img);
						setMserCoordinates(mser_blobs);
						setSIFTfeatures(img);
						
						//find the intersection of features and include only those in boVW
						

	}
}

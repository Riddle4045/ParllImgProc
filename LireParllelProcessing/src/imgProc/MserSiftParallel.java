package imgProc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

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
		public static List<java.awt.Point> cord_points ;  // coordinates of the extracted mser_regions
		public static List<ImagePoint> point_list= null; //list of the points extracted from mser_regions
		
		
		public static Extractor sift_extractor = new Extractor();
		public static List<Feature> sift_features ;
		public static List<Feature> sift_filtered_features;

		public   MserSiftParallel() {
				cord_points =  new Vector<java.awt.Point>();
				sift_features = new Vector<Feature>();
				sift_filtered_features = new Vector<Feature>();
		};
		
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
	 public static void setMserRegions(MSERGrowthHistory[] mser_blobs){
						for (MSERGrowthHistory mserGrowthHistory : mser_blobs) {
									point_list = Arrays.asList(mserGrowthHistory.getPoints());
						}
						setCoordinatesofMser();
	}
	/**
	 * Utility function to get Cooridnate objects
	 *  use : retrieveing MSER and SIFT overlap
	 */
	public static void setCoordinatesofMser() {
				if ( point_list.size() == 0 ){
								System.out.println("get the Mser regions first");
								
				} else {
			 		for (ImagePoint point : point_list) {
 						cord_points.add(new java.awt.Point(point.getX(), point.getY()));
		}
				}

	 }
	/** 
	 * set overlapping MSER and SIFT features
	 * @param img
	 * @throws IOException 
	 */
	public static List<Feature> getSiftMserFeatures(BufferedImage img) throws IOException{
			
					
						//obtain SIFT features and MSER blobs
						setMserRegions(img);
						setMserRegions(mser_blobs);
						setSIFTfeatures(img);
					
						//find the intersection of features and include only those in boVW
						try {
							for (Feature keypoint : sift_features) {
								
										java.awt.Point temp = new java.awt.Point();
										temp.x = Math.round(keypoint.location[0]);
										temp.y = Math.round(keypoint.location[1]);
										if ( cord_points.contains(temp)){
													sift_filtered_features.add(keypoint);
										}
							}
						}catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					img.flush();
					return sift_filtered_features;
							
	}
	
}

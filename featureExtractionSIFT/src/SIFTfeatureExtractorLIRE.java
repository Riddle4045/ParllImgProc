import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.Object;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.imageanalysis.LireFeature;
import net.semanticmetadata.lire.imageanalysis.bovw.SiftFeatureHistogramBuilder;
import net.semanticmetadata.lire.imageanalysis.sift.Extractor;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.DocumentFactory;
import net.semanticmetadata.lire.AbstractDocumentBuilder;


import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_features2d.DescriptorExtractor;
import com.googlecode.javacv.cpp.opencv_features2d.Feature2D;
import com.googlecode.javacv.cpp.opencv_features2d.FeatureDetector;
import com.googlecode.javacv.cpp.opencv_features2d.KeyPoint;
import com.googlecode.javacv.cpp.opencv_nonfree.SIFT;
import com.sun.xml.internal.txw2.Document;




public class SIFTfeatureExtractorLIRE {
	// parameters used to detect SIFT key points
	public static int nFeatures = 0;
	public static int nOctaveLayers = 3;
	public static double contrastThreshold = 0.03;
	public static int edgeThreshold = 10;
	public static double sigma = 1.6;
	public static SIFT sift = new SIFT(nFeatures, nOctaveLayers, contrastThreshold, edgeThreshold, sigma);
	public static DescriptorExtractor des = sift.getDescriptorExtractor();
	public static DescriptorExtractor newdes = DescriptorExtractor.create("SiftDescriptorExtractor");
	
	public static String[] extract(String imageName) throws IOException{


		ArrayList<String> list = new ArrayList<String>();
		File file = new File(imageName);
		System.out.println(file.getAbsolutePath());
		CvMat image = com.googlecode.javacv.cpp.opencv_highgui.cvLoadImageM(file.getAbsolutePath(), com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE);
		
		//LIRE image-feature extraction...
		IplImage bImage = com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage(imageName);
		BufferedImage newbImage = bImage.getBufferedImage();
		Extractor extractor = new Extractor();
		SiftDocumentBuilder test = new SiftDocumentBuilder();
		Field[]  fields  = test.createDescriptorFields(newbImage);
		for(int i =0 ; i < fields.length ; i++) { System.out.println(fields[i].toString()); } 
		// end --- LIRE Image extraction .
		
		
		
		
		//SIFT descriptors of keypoints in opencv-javacv
		KeyPoint keyPoints = new KeyPoint();
		sift.detect(image, null, keyPoints);
		KeyPoint[] points = toArray(keyPoints);
		CvMat cv_mat = new CvMat(null);
		des.compute(image, keyPoints, cv_mat);
		
		if(cv_mat.isNull()) return null;
		
	    for(int i = 0;  i < cv_mat.rows(); i++){
	    	// output some information to see whether the results are correct
	    	//System.out.println(points[i].position() + " " + points[i].pt() + " " + points[i].response() + " " + points[i].angle() + " " + points[i].size() + " ");
	    	Paras paras = getSIFTParameters(points[i]);
	    	//System.out.println((paras.octave + 1) + " " + paras.layer + " " + paras.scale * 0.5f + " " + paras.radius  + " ");
	    	String s = points[i].position() + " " + points[i].pt() + " " + points[i].response() + " " + points[i].angle() + " " 
	    			+ points[i].size() + " " + (paras.octave + 1) + " " + paras.layer + " " + paras.scale * 0.5f + " " + paras.radius;
	    	
	    	int scale = paras.octave + 1; // filtering by scale information
	    	//System.out.println(scale);
	    	if(scale > 1){ // filter out smaller scales
	    		double[] row = new double[cv_mat.cols()];
	    		for(int j = 0; j < cv_mat.cols(); j++){
	    			row[j] = cv_mat.get(i, j);
	    			s = s + " " + row[j];
	    		} 	
	    	list.add(s);
	    	
	    	}
	    }
	    System.out.println(list.size());
	    return list.toArray(new String[list.size()]);
	}

	
public static Paras getSIFTParameters(KeyPoint point){
	
	 // get octave
	int octave = point.octave() & 255;
   if (octave >= 128) octave = octave | -128;
   // get layer
   int layer = (point.octave() >> 8) & 255;
   // get scale
   float scale = 0;
   if (octave >= 0) scale = 1.0f/(1 << octave);
   else scale = (float)(1 << -octave);
   // multiply the point's radius by the calculated scale
   float scl = point.size() * 0.5f * scale;
   // determines the size of a single descriptor orientation histogram
   float histWidth = 3.0f * scl;
   // descWidth is the number of histograms on one side of the descriptor
   int radius = (int)(histWidth * 1.4142135623730951f * (4 + 1) * 0.5f);
   
   return new Paras(octave, layer, scale, radius); 
}



private static class Paras{
	   int octave;
	   int layer;
	   float scale;
	   int radius;
	   public Paras(int octave, int layer, float scale, int radius){
		   this.octave = octave; this.layer = layer; this.scale = scale; this.radius = radius;
	   }
	}



public static KeyPoint[] toArray(KeyPoint keyPoints){
	int oldPosition = keyPoints.position();
	// Convert keyPoints to Scala sequence
	KeyPoint[] points = new KeyPoint[keyPoints.capacity()];
	for (int i = 0; i < keyPoints.capacity(); i++) 
		points[i] = new KeyPoint(keyPoints.position(i));
	// Reset position explicitly to avoid issues from other uses of this position-based container. 				  
	keyPoints.position(oldPosition);
	return points;
}
}


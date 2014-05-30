package sfit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.hadoop.hdfs.server.namenode.FileChecksumServlets.GetServlet;
import org.apache.lucene.document.Field;

import net.semanticmetadata.lire.imageanalysis.sift.Extractor;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;

public class siftFeatureExtractor  {
	
	public static int numFeatures=0;
	
	public static void main(String[] args) throws IOException{
		
			String basePath = "";
			File file = new File("/home/hduser/Pictures/Bhvgt8dCQAAzGiK.jpg");
			BufferedImage img = ImageIO.read(file);
			getSIFTfeatures(img);
			System.out.println("Total Number of Features:"+numFeatures);
	}
	
	public static void  getSIFTfeatures(BufferedImage img) {
	

		
		sfit.SiftDocumentBuilder test = new sfit.SiftDocumentBuilder();
		Field[] fields = test.createDescriptorFields(img);
		String[] features= new String[fields.length];
		numFeatures = numFeatures+features.length;
		for(int i =0 ; i < fields.length;i++) {  features[i] = fields[i].toString();
				//	System.out.println(features[i]);
		}
		
}
}

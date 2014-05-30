package sfit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.lucene.document.Field;

import net.semanticmetadata.lire.imageanalysis.sift.Extractor;
import net.semanticmetadata.lire.impl.SiftDocumentBuilder;

public class siftFeatureExtractor  {
	
	public String[] getSIFTfeatures(BufferedImage img) {
	

		
		sfit.SiftDocumentBuilder test = new sfit.SiftDocumentBuilder();
		Field[] fields = test.createDescriptorFields(img);
		String[] features= new String[fields.length];
		for(int i =0 ; i < fields.length;i++) {  features[i] = fields[i].toString();
					System.out.println(features[i]);
		}
		return features;
}
}

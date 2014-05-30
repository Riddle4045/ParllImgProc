package sfit;

import hipi.image.FloatImage;
import hipi.image.ImageHeader.ImageType;
import hipi.image.io.JPEGImageUtil;
import hipi.imagebundle.HipiImageBundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class testingHIPI {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// HipiImageBundle hib = new HipiImageBundle(new Path("/home/hduser/Documents/test1.hib"), conf_new);
		Path  hipiPath = new Path("/home/hduser/Documents/Softwares/bundle1.hib");
		Configuration conf_new = new Configuration();
		HipiImageBundle hib = new HipiImageBundle(hipiPath,conf_new) ;
		hib.open(HipiImageBundle.FILE_MODE_WRITE, true);
	
		//adding image
		FileInputStream file = new FileInputStream("/home/hduser/Pictures/Bhvd_kCIYAER2kQ.jpg");
		FloatImage image_f = JPEGImageUtil.getInstance().decodeImage(file);

		 InputStream is = new BufferedInputStream(file);
		 hib.addImage(is, ImageType.JPEG_IMAGE);
		 hib.close();
		 hib.open(HipiImageBundle.FILE_MODE_WRITE,true);
		 System.out.println(hib.getImageCount());	  
		 hib.close();
	}

}

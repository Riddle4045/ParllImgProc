package sfit;

import hipi.image.FloatImage;
import hipi.image.ImageHeader;
import hipi.image.ImageHeader.ImageType;
import hipi.image.io.ImageEncoder;
import hipi.image.io.JPEGImageUtil;
import hipi.imagebundle.AbstractImageBundle;
import hipi.imagebundle.HipiImageBundle;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapred.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;



public class MasterClass {
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		//Start of Job1
		JobConf conf = new JobConf(ImageMapper.class);
		//conf.set("mapred.jar","/path/to/my/jar/CountRows.jar");`
		Job job = new Job(conf);
		FileSystem fs = FileSystem.get(conf);
		Path input = conf.getLocalPath("input");
		
		
		//setting up hipibundle
		
		// HipiImageBundle hib = new HipiImageBundle(new Path("/home/hduser/Documents/test1.hib"), conf_new);
		Path  hipiPath = new Path("/home/hduser/Documents/Softwares/bundle.hib");
		Configuration conf_new = new Configuration();
		HipiImageBundle hib = new HipiImageBundle(hipiPath,conf_new) ;
		hib.open(HipiImageBundle.FILE_MODE_WRITE, true);
		
		
		//adding image
		FileInputStream file = new FileInputStream("/home/hduser/Pictures/Bhvd_kCIYAER2kQ.jpg");
		FloatImage image_f = JPEGImageUtil.getInstance().decodeImage(file);
		 //file.close();
		 InputStream is = new BufferedInputStream(file);
		 hib.addImage(image_f);
		 hib.addImage(is, ImageType.JPEG_IMAGE);
		 
	//s	 hib.wait();
	//	 hib.close();

//		 hib.open(HipiImageBundle.FILE_MODE_READ, true);
		
		 
	// String[] image_input_path = {"/home/hduser/Pictures/Bhvd_kCIYAER2kQ.jpg","/home/hduser/Pictures/Bhvgf3sIIAASwuz.jpg","/home/hduser/Pictures/Bhvgt8dCQAAzGiK.jpg"};
		String image_input_path = "/home/hduser/Pictures/test.png";
		File new_file = new File(image_input_path);
		FileInputStream new_fis = new FileInputStream(new_file);
		hib.addImage(new_fis, ImageType.PNG_IMAGE);
		 System.out.println(hib.getImageCount());	    	
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(Text.class);

		conf.setMapperClass(ImageMapper.class);
		//conf.setReducerClass(IMageReducer.class);
		conf.setNumReduceTasks(1);

		conf.setInputFormat(TextInputFormat.class);
	//	FileSystem fs = FileSystem.get(conf);
		FileInputFormat.setInputPaths(conf, new Path(fs.getHomeDirectory(),"hdfs://localhost:54310/user/hduser/input"));
		FileOutputFormat.setOutputPath(conf, new Path(fs.getHomeDirectory(),"hdfs://localhost:54310/user/hduser/output1"));
		hib.close();
		System.out.println(hib.getImageCount());
		JobControl jc = new JobControl(null);
		jc.addJob(job);
		jc.run();
	    job.setMessage("job is runnning...");
	    Path output_path = new Path(fs.getHomeDirectory(), "hdfs://localhost:54310/user/hduser/output1");
	    if ( job.isCompleted() && fs.exists(output_path)) {
	    						fs.delete(output_path, true);
	    }
	  
	}

}

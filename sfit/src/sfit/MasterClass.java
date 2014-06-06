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
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapred.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import com.sun.corba.se.impl.ior.ByteBuffer;
import com.sun.corba.se.spi.ior.MakeImmutable;
import org.apache.commons.*;


public class MasterClass {
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub

		
		Configuration conf = new Configuration();
		conf.set("fs.default.name","hdfs://localhost:54310");
		FileSystem fs = FileSystem.get(conf);
		
		
		org.apache.hadoop.mapreduce.Job job = new org.apache.hadoop.mapreduce.Job(conf);
		job.setJarByClass(MasterClass.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
				
		job.setMapperClass(ImageMapper.class);
		job.setReducerClass(ImageReducer.class);
		job.setNumReduceTasks(1);

	    job.setInputFormatClass(SequenceFileAsTextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	   
	    org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job, new Path(fs.getHomeDirectory(),"hdfs://localhost:54310/user/hduser/input"));
	    org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job, new Path(fs.getHomeDirectory(),"hdfs://localhost:54310/user/hduser/input"));

		makeSequenceFileFromHdfs(conf, fs);
	}
	
public static void makeSequenceFileFromHdfs(Configuration conf,FileSystem fs) throws IOException {
					
					FSDataInputStream  in = null;
					BytesWritable value = new BytesWritable();
					Text key = new Text();
					System.out.println(fs.getHomeDirectory());
					Path inpath = new Path(fs.getHomeDirectory(),"/user/hduser/input/testingImagesInput/magdalen_000097.jpg");
					Path seq_path = new Path(fs.getHomeDirectory(),"/user/hduser/output/seq");
					Path outpath = new Path("output/file.seq");
					
					SequenceFile.Writer writer = null;
					try {
								System.out.println("reading from :"+inpath);
								in =  fs.open(inpath);
								
								byte bufffer[] = new  byte[in.available()];
								in.read(bufffer);
								System.out.println("Writing to:"+seq_path.toString());
								writer = SequenceFile.createWriter(fs,conf,seq_path,key.getClass(),value.getClass());
								writer.append(new Text(inpath.getName()), new BytesWritable(bufffer));
							
								System.out.println("inside try!");
								
								
					}catch (Exception e) {
			            System.out.println("Exception MESSAGES = "+e.getMessage());
			            e.printStackTrace();
			        }
			        finally {
			            IOUtils.closeStream(writer);
			      //      System.out.println("last line of the code....!!!!!!!!!!");
			        }
					
					//once the wrting of the sequence file is done 
					//we need to read again and convert to buffered image and process and spit out text 
					convertSequenceFileToImage(conf,fs,seq_path);
}

public static void convertSequenceFileToImage(Configuration conf,FileSystem fs, Path seq_path ) throws IOException {
		
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, seq_path, conf);
		Text key = new Text();
		BytesWritable value  = new BytesWritable();
		reader.next(key, value);
		byte[] buf = value.getBytes();
		
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(buf));
		ImageIO.write(img,"jpg",new File("/home/hduser/Documents/snap.jpg"));
				

		
		
}
}

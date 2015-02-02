package imgProc;


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
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
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
import com.sun.org.apache.bcel.internal.generic.NEW;

import org.apache.commons.*;


public class MasterClass {
	
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub

		
		Configuration conf = new Configuration();
		conf.set("fs.default.name","hdfs://localhost:54310");
		conf.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem");
		FileSystem fs = FileSystem.get(conf);
		makeSequenceFileFromHdfs(conf,fs);
	//	makeSequenceFileFromLocalFs(conf, fs);
		org.apache.hadoop.mapreduce.Job job = new org.apache.hadoop.mapreduce.Job(conf);
		job.setJarByClass(MasterClass.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(BytesWritable.class);
		
		job.setMapperClass(ImageMapper.class);
		job.setReducerClass(ImageReducer.class);

		job.setNumReduceTasks(1);
	
		job.setInputFormatClass(SequenceFileAsTextInputFormat.class);
	    job.setOutputFormatClass(TextOutputFormat.class);
	     
	 //  DistributedCache.addFileToClassPath(new Path("/user/hduser/lib/lire.jar"), conf);
	    org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job, new Path(fs.getHomeDirectory(),"hdfs://localhost:54310/user/hduser/output/file.seq"));
	   // org.apache.hadoop.mapreduce.lib.input.FileInputFormat.setInputPaths(job, new Path("home/hduser/Documents/file.seq"));
	    org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job,new Path(fs.getHomeDirectory(),"hdfs://localhost:54310/user/hduser/output1"));
	  //  org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.setOutputPath(job,new Path("/home/hduser/Documents/"));
	    
	  
		//makeSequenceFileFromHdfs(conf, fs);
		  job.waitForCompletion(true);
	  //  makeSequenceFileFromLocalFs(conf, fs);
	}
	
	
/*
 * read files from hdfs , convert to sequence file and write back to hdfs
 * @param:
 * Configuration : for the hdfs system to write on
 * FileSystem fs : hdfs filesystem handle.
 */
public static void makeSequenceFileFromHdfs(Configuration conf,FileSystem fs) throws IOException {
					
					FSDataInputStream  in = null;
					BytesWritable value = new BytesWritable();
					Text key = new Text();

					//the images are stored in  localHDFS/input folder.
					Path inpath = new Path(fs.getHomeDirectory(),"/user/hduser/input");		
					Path seq_path = new Path(fs.getHomeDirectory(),"/user/hduser/output/file.seq");
					SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, seq_path, key.getClass(), value.getClass());
					FileStatus[] files = fs.listStatus(inpath);
					System.out.println("Number of files fetched "+files.length);		
					
					for (FileStatus fileStatus : files) {
						inpath = fileStatus.getPath();

					try {
								System.out.println("reading from :"+inpath);
								in =  fs.open(inpath);
								byte bufffer[] = new  byte[in.available()];
								in.read(bufffer);
						//		System.out.println("Writing to:"+seq_path.toString());
								//writer = SequenceFile.createWriter(fs,conf,seq_path,key.getClass(),value.getClass());
								writer.append(new Text(inpath.getName()), new BytesWritable(bufffer));	
								//writer.close();
					}catch (Exception e) {
			            System.out.println("Exception MESSAGES = "+e.getMessage());
			            e.printStackTrace();
			        }
			        finally {
			         //   IOUtils.closeStream(writer);
			      //      System.out.println("last line of the code....!!!!!!!!!!");
			        }
					}
					writer.close();
					//once the writing of the sequence file is done 
					//we need to read again and convert to buffered image and process and spit out text 
				//	convertSequenceFileToImage(conf,fs,seq_path);
}

/*
 * read the sequence file and convert them to buffered images for 
 * image manipulation
 * @param : 
 * conf : configuartion of the filesystem  ( hdfs )
 * fs : filesystem handle
 * seq_path : path to the sequence file
 */

public static void convertSequenceFileToImage(Configuration conf,FileSystem fs, Path seq_path ) throws IOException {
		
		SequenceFile.Reader reader = new SequenceFile.Reader(fs, seq_path, conf);
		Text key = new Text();
		BytesWritable value  = new BytesWritable();
		//TODO : introduce a while condition to iterate over all the keys 
		reader.next(key, value);
		byte[] buf = value.getBytes();
		System.out.println("Key:"+key);
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(buf));
		ImageIO.write(img,"jpg",new File("/home/hduser/Documents/snap.jpg"));
						
}

/*
 * This takes the images from a localfile system converts them
 * to sequence file and writes to the hdfs system
 * @param : 
 * Configuration : for the hdfs system to write on
 * FileSystem fs : hdfs filesystem handle.
 * 
 * TODO : handle exceptions and add ch
 */
public static void  makeSequenceFileFromLocalFs(Configuration conf,FileSystem fs) throws IOException {
			
	
			File dir = new File("/home/hduser/Documents/hadoop-tests");
			File[] files = dir.listFiles();
		
			BytesWritable value = new BytesWritable();
			Text key = new Text();
			Path seq_path = new Path("/home/hduser/Documents/file.seq");
		//	Path seq_path = new Path(fs.getHomeDirectory(),"/user/hduser/output/file.seq");
			SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, seq_path, key.getClass(), value.getClass());
			for (File image : files) {
				try {
					
					        FileInputStream  in = new FileInputStream(image);
							System.out.println("reading from :"+image.getAbsolutePath());
							byte bufffer[] = new  byte[in.available()];
							in.read(bufffer);
							System.out.println("Writing to:"+seq_path.toString());
						//	writer = SequenceFile.createWriter(fs,conf,seq_path,key.getClass(),value.getClass());
							writer.append(new Text(image.getAbsolutePath()), new BytesWritable(bufffer));					
				}catch (Exception e) {
		            System.out.println("Exception MESSAGES = "+e.getMessage());
		            e.printStackTrace();
		        }
		        finally {
		            IOUtils.closeStream(writer);
		      //      System.out.println("last line of the code....!!!!!!!!!!");
		        }
			}
			
}
}

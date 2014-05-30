package sfit;

import java.io.IOException;

import hipi.image.FloatImage;
import hipi.image.ImageHeader;
import hipi.imagebundle.HipiImageBundle;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer.Context;

public  class ImageMapper extends  MapReduceBase implements org.apache.hadoop.mapred.Mapper<FloatImage,Text, Text, Text> {
	private static Configuration conf;
	// This method is called on every node
	public void setup(Context jc) throws IOException
	{
		conf = jc.getConfiguration(); 
	}

	public void map(FloatImage key , Text value , OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		Path input_path = new Path("hdfs://localhost:54310/user/hduser/input");

		HipiImageBundle bundle = new HipiImageBundle(input_path,conf);
		bundle.addImage(key);
		Text key_text = new Text(key.toString());
		Text value_text = new Text(value.toString());
		output.collect(key_text, value_text);

	}
}

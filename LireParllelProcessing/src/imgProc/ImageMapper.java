package imgProc;

import java.io.IOException;


import hipi.image.FloatImage;
import hipi.image.ImageHeader;
import hipi.imagebundle.HipiImageBundle;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsBinaryInputFormat;

public class ImageMapper extends Mapper<Text, ByteWritable, IntWritable, IntWritable> {
	
	
				public void map(Text key,ByteWritable value,org.apache.hadoop.mapreduce.MapContext<Text, ByteWritable, IntWritable, IntWritable> context) throws IOException, InterruptedException{
					
					IntWritable test = new IntWritable(1);
					IntWritable out_val = new IntWritable(value.hashCode());
					Text out_key = new Text();
					out_key = key;
					context.write(test,test);
				}
}

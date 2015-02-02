package imgProc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;



import net.semanticmetadata.lire.imageanalysis.sift.Feature;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileAsBinaryInputFormat;

public class ImageMapper extends Mapper<Text, BytesWritable, Text, Text> {
	
			public static MserSiftFeatureOperations mserSiftOperations = new MserSiftFeatureOperations(null);
			@Override
			public void map(Text key , BytesWritable value, Context context) throws IOException , InterruptedException {
								String line = key.toString();
								List<Feature> features = getMSERSIFTFeatures(value, key);
								String final_value = features.toString() ;
								context.write(new Text(line), new Text(final_value));
			}
			
			
			public static List<Feature> getMSERSIFTFeatures(BytesWritable value,Text key) throws IOException{
				List<Feature> features = null;
				try{
							byte[] image_bytes=value.getBytes();
							if(image_bytes.length>0){
							BufferedImage img = ImageIO.read(new ByteArrayInputStream(value.getBytes()));
							features = mserSiftOperations.getAllSiftMserFeaturesForImage(img, key.toString(), false);
							
							}
					}catch (java.lang.IllegalArgumentException e){
								System.out.println("the image causing exception: ");
					}catch( java.awt.color.CMMException e){
								System.out.println("the image causing exception: ");
					}catch(javax.imageio.IIOException e){
							System.out.println("the image causing exception: ");
					}
				return features;
			}
}

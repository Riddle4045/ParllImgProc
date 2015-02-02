package imgProc;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class ImageReducer extends org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>{
		
	@Override
	public void reduce(Text key , Iterable<Text> value,Context context) throws IOException,InterruptedException{
				for (Text text : value) {
					context.write(key, key);
				}
	}
}

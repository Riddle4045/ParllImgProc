import java.io.IOException;
import java.util.ArrayList;


public class masterClass {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[] features = SIFTfeatureExtractorLIRE.extract("images.jpeg");
		int numKeyPoints = features.length;
			while ( numKeyPoints > 0 ) {
						//System.out.println(features[numKeyPoints-1]);
						numKeyPoints--;
			}
	}

}


#include "opencv2/highgui/highgui.hpp"
#include "opencv2/features2d/features2d.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include <iostream>
#include<vector>
#include<string>
#include <stdio.h>

using namespace cv;
using namespace std;

/** code to check the peformance of MSER over several set of images 
 *  the intention to tweak MSER to suit our requirements.
 *
**/

//static const string = "/home/hduser/Documents/OpenCV-testing Images/Train/Military Tank/images/1f4413b74f2282b02842a55090ceca63.jpg";
int main ( int argc , char** argv ) 
	{
			if (argc != 2 ){
						cout << "pass two stupid arguments!!\n";
			return -1 ;
	}
	Mat image;
	image = imread(argv[1] , -1);
	namedWindow("testimage",WINDOW_AUTOSIZE);
	imshow("testimage",image);
	waitKey(0);
	
	//lets detect a blob in the image 
	cv::SimpleBlobDetector::Params params; 
	params.minDistBetweenBlobs = 10.0;  // minimum 10 pixels between blobs
	params.filterByArea = true;         // filter my blobs by area of blob
	params.minArea = 20.0;              // min 20 pixels squared
	params.maxArea = 500.0;  
	
	//after redefining the params , let initialze the detector with them
	cv::SimpleBlobDetector myBlobDetector(params);
	vector<keyPoint> blobs;
	myBlobDetector.detect(image,blobs);

	//lets display  the detected blobs..
	Mat blob_image;
	drawKeyPoints(image,blobs,blob_image);
	namedWindow("blobImage",WINDOW_AUTOSIZE);
	imshow("blobImage",blob_image);
	waitKey(0);

	return 0;
}
		


#include "opencv2/highgui/highgui.hpp"
#include "opencv2/features2d/features2d.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/nonfree/nonfree.hpp"

#include <iostream>
#include <stdio.h>

using namespace cv;
using namespace std;

/**
 * Author : Ishan Patwa ipatwa@ufl.edu || riddle4045@gmail.com
 * This code  extracts all the SIFT keypoints and their descriptors over
 * the Blobls Extracted by MSER
 */


static const Vec3b bcolors[] =
{
    Vec3b(0,0,255),
    Vec3b(0,128,255),
    Vec3b(0,255,255),
    Vec3b(0,255,0),
    Vec3b(255,128,0),
    Vec3b(255,255,0),
    Vec3b(255,0,0),
    Vec3b(255,0,255),
    Vec3b(255,255,255)
};

int main( int argc, char** argv )
{
					if ( argc != 2 ){
									cout << "enter the file path <path_to_image>" << endl;
					}

					Mat img0 = imread(argv[1],1);
					if ( !img0.data ){
								cout << "Check the image, Unable to read\n" ;
					}else {
								cout << "we  have the image...\n";
					}
					Mat img,filtered_img;
					img0.copyTo(img);

					vector< vector<Point> > msers;

					//iterators for msers
					vector< vector<Point> >::iterator mser_rows;
					vector<Point>::iterator mser_colums;

					MSER()(img0,msers);
					cout << "extracted " << msers.size() << "  contours\n";



					//coloring the MSER regions in different colors to distinguish the detected regions
					for ( int i =0 ; i < msers.size(); i++){
								for ( int j= 0 ; j < msers[i].size();j++){
											//cout << "mser[" << i << "]["  <<  j  <<  "]\t" << msers[i][j] << "\n";
											Point pt =  msers[i][j];
											img0.at<Vec3b>(pt) = bcolors[i%9];
								}
					}

					//detecting SIFT features
					cv::SiftFeatureDetector  detector;
					vector<KeyPoint> keypoints;
					detector.detect(img,keypoints);
					cout << "number of keypoints detected by SIFT :" << keypoints.size() << endl;

					//the key points obtained from the SIFT implementation have X Y coordinates
					//right now , we inlucde only those coordinates which are detected as blobls in MSER
					//this gives a filtered set of Coordinates for each image
					//however this operation is very costly
					//TODO : optimize this

					vector<KeyPoint> filtered_keyPoints;
					for ( int i = 0 ; i < keypoints.size(); i++ ){

						//this is going to be O(n^3) :(
						for ( int j =0 ; j < msers.size();j++){
											for ( int k =0 ; k < msers[j].size();k++){
														if (  msers[j][k].x == (int)(keypoints[i].pt.x) && msers[j][k].y == (int)(keypoints[i].pt.y) ) {
																		filtered_keyPoints.push_back(keypoints[i]);
														}
											}
						}

					}
					cout << "Size of Filtered keypoints : " << filtered_keyPoints.size() << endl;
					drawKeypoints(img,filtered_keyPoints,filtered_img);
					imshow("filtered_keypoints",filtered_img);
					waitKey(0);
					return 0;

}

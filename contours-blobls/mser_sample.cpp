
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/features2d/features2d.hpp"
#include "opencv2/imgproc/imgproc.hpp"

#include <iostream>
#include <stdio.h>

using namespace cv;
using namespace std;



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
    cout << "enter here";
    string path;
    Mat img0, img, yuv, gray, ellipses;
    if( argc != 2 ) {
            cout << "\nUsage: mser_sample <path_to_image>\n";
       	    return 0; 
    }
    
    img0 = imread(argv[1], 1 );
		
    cout << "after the if else ";
    cvtColor(img0, yuv, COLOR_BGR2YCrCb);
    //cvtColor(img0, gray, COLOR_BGR2GRAY);
  //  cvtColor(gray, img, COLOR_GRAY2BGR);
    img0.copyTo(ellipses);

    vector<vector<Point> > contours;
    MSER()(yuv, contours);
 cout << "Contors detected";

    // draw mser's with different colors
    for( int i = (int)contours.size()-1; i >= 0; i-- )
    {
        const vector<Point>& r = contours[i];
        for ( int j = 0; j < (int)r.size(); j++ )
        {
            Point pt = r[j];
            img.at<Vec3b>(pt) = bcolors[i%9];
        }

        // find ellipse (it seems cvfitellipse2 have error or sth?)
        RotatedRect box = fitEllipse( r );

        box.angle=(float)CV_PI/2-box.angle;
        ellipse( ellipses, box, Scalar(196,255,255), 2 );
    }

    imshow( "original", img0 );
    imshow( "response", img );
    imshow( "ellipses", ellipses );

    waitKey(0);
}

//============================================================================
// Name        : initialize.cpp
// Author      : Alexandru Agape
// Version     :
// Copyright   : Your copyright notice
// Description : Functions used for initializing the application
//============================================================================

#include "initialize.h"
using namespace cv;
using namespace std;

//======================================================================================================================

int loadCascade(cv::CascadeClassifier *face_cascade)
{
	String face_cascade_name = "/usr/share/opencv/haarcascades/haarcascade_frontalface_alt2.xml";
	String face_cascade_lbp = "/usr/share/opencv/lbpcascades/lbpcascade_frontalface.xml";

	if (!face_cascade->load(face_cascade_lbp)){
		cout<< "--(!)Error loading";
		return -1;
	}

	return 0;
}

//======================================================================================================================



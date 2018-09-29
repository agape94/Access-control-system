//=================================================================================================
// Name        : frames.cpp
// Author      : Alexandru Agape
// Version     :
// Copyright   : Your copyright notice
// Description : The is the source file, where image processing functions are defined
//=================================================================================================
#include "frames.h"


using namespace std;
using namespace cv;
//=================================================================================================

cv::CascadeClassifier face_cascade;
long int start;
int height;
std::vector<cv::Mat> qFetch, qDetect;
std::vector<cv::Rect> faces; /* vector in care vor fi stocate fetele */
std::vector<cv::Mat> qFramesForStreaming;
bool streaming;

//=================================================================================================
void* detectAndDisplay(void *argument)
{
	//	clock_t begin,end;
	/*
    ---------------------Functia de Detectare a fetelor--------------------

    Aceasta functie foloseste identificatori Haar pentru detectarea fetelor
	 */
	cout<<"Started detection!"<<endl;
	Mat frame;
	while(1)
	{
		if(!qFetch.empty())
		{

			frame = qFetch.front(); /*In aceasta variabila va fi salvat ultimul frame adaugat in coada qFetch */
			qFetch.pop_back(); /* Ultimul frame adaugat se sterge din coada */

			//face_cascade.detectMultiScale(frame, faces, 1.2, 2, CASCADE_SCALE_IMAGE);

			//begin = clock(); /* Start procesare */
			face_cascade.detectMultiScale(frame, faces, 1.1, 3); /* detectia fetelor din imagine. */
			//end = clock(); /* Stop procesare - Nu stiu daca in acest interval nu intervine alt thread!! */
			//
			//double elapsed_secs = double(end - begin) / CLOCKS_PER_SEC; /* Calcul timp in secunde */
			//cout<<elapsed_secs<<" sec "<<endl;

			if(faces.size() > 0) /* Daca s-au detectat fete... */
			{
				for(unsigned int ic = 0 ; ic < faces.size() ; ic ++)
				{
					Point *pt1 = new Point(faces[ic].x,faces[ic].y);
					Point *pt2 = new Point( (faces[ic].x + faces[ic].width) , (faces[ic].y + faces[ic].height) );
					rectangle(frame,*pt1,*pt2,Scalar(255, 0, 0), 5, 8, 0);

					delete pt1;
					delete pt2;
					/* ...Acestea se parcurg si se incadreaza intr-un un dreptunghi */
				}

				if(qDetect.size() < limQDetect) /* Daca coada qDetect nu este plina */
				{
					qDetect.push_back(frame); /* Se adauga imaginea in coada si urmeaza sa fie afisata de thread-ul de Print */
					faces.empty(); /* Se goleste vectorul fetelor pentru urmatorul frame */
				}else
				{
					qDetect.erase(qDetect.begin(),qDetect.begin()+qDetect.size()-1);
				}
			}
			std::this_thread::sleep_for(std::chrono::milliseconds(30));
		}

	}
}

//=================================================================================================

void* print(void *argument)
{
	/*
    Functia de print preia o imagine din coada qDetect, daca exista, si o afiseaza pe ecran
	 */
	Mat frame;
	cout<<"Started print!"<<endl;
	while(1)
	{
		if(!qDetect.empty() && !faces.empty()) /* Daca exista frame-uri in coada qDetect... */
		{
			frame = qDetect.front();
			qDetect.pop_back();
			imshow("Display window",frame); /* ...se afiseaza ultimul adaugat */
			waitKey(1);

		}
		else if(!qFetch.empty() && faces.empty())
		{
			/* TODO de vazut o modalitate de a afisa imagine de la camera fara ca o persoana sa
			 * fie in cadru sau nu */
			frame = qFetch.front();
			qFetch.pop_back();

			imshow("Display window",frame); /* ...se afiseaza ultimul adaugat */
			waitKey(1);
		}
		std::this_thread::sleep_for(std::chrono::milliseconds(30));
	}
}

//=================================================================================================

int initApplication()
{
	if(loadCascade(&face_cascade) == -1)
	{
		cerr<<"Error loading cascade_classifiers!"<<endl;
		return -2;
	}

	cv::setNumThreads(1);

	return 0;
}

//=================================================================================================

void fetchFrames()
{

	Mat frame;
	//Mat frame_grayscale;
	cout<<"Started fetch!"<<endl;
	VideoCapture cap(0);
	if(!cap.isOpened())
	{
		cout<<"Could not open the webcam"<<endl; /* eroare la deschidere camera */
		exit(1); /* Inchide aplicatia cu eroare */

		/*
		            Interesant de implementat spre final: Daca la deschiderea sistemului, apare o eroare,
		            utilizatorul sa fie notificat, eventual sa poata da restart la aplicatie, remote.

		            Am observat ca se mai intampla cateodata sa nu deschida camera, si atunci aplicatia
		            se blocheaza.
		 */
	}
	else{
		cout<<"Camera is opened"<<endl; /* Succes deschidere camera -> Aplicatia poate porni */
	}

	while(1)
	{
		if(cap.read(frame)) /* se citeste frame-ul */
		{
			start = cv::getTickCount(); /* salvez momentul curent de timp */
			//imshow("Display window",frame);
			//cvtColor(frame, frame_grayscale,COLOR_BGR2GRAY); /* transformare din RGB in grayscale, pentru reducerea timpului de procesare */
			//equalizeHist(frame_grayscale, frame_grayscale); /* procesare elementara - TODO: Imbunatatiri!!! */
			if(qFetch.size() == limQFetch)
			{
				qFetch.erase(qFetch.begin(),qFetch.begin()+qFetch.size()-1);
			}
			qFetch.push_back(frame); /* Se adauga in coada de frame-uri neprocesate */

			if(streaming)
			{
				if(qFetch.size() == limQFetch)
				{
					qFramesForStreaming.erase(qFramesForStreaming.begin(),qFramesForStreaming.begin()+qFramesForStreaming.size()-1);
				}
				qFramesForStreaming.push_back(frame);
			}

		}
		//std::this_thread::yield();
		std::this_thread::sleep_for(std::chrono::milliseconds(30)); /* thread-ul main cedeaza procesorul */
	}

	//	delete cap;
}

//=================================================================================================


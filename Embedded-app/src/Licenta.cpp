//=================================================================================================
// Name        : Licenta.cpp
// Author      : Alexandru Agape
// Version     :
// Copyright   : Your copyright notice
// Description : The file contains the main() function for this application
//=================================================================================================

#include<iostream>
#include<opencv2/face.hpp>
#include <pthread.h>
#include "frames.h"
#include "tcpserver.h"
#include "udpserver.h"

using namespace std;
//===================================Explicatii logica pipeline====================================

/*
    Am incercat sa simulez functionalitatea de task-uri pe care am studiat-o la PATR. Pentru ca
 FreeRTOS nu are implementare oficiala pentru Raspberry Pi.

 Am creat 2 cozi: qFetch, cu frame-uri receptionate, si qDetect cu frame-uri procesate (in care
 fetele au fost detectate. Aceste frame-uri sunt afisate).

 Am creat 3 thread-uri:
                        thread-ul main, care se ocupa de Fetch;
                        thread-ul threadDetect, care face detectia fetelor;
                        thread-ul threadPrint, care face afisarea rezultatului detectiei.

 Thread-ul main preia imaginea de la camera, o transforma in RGB si o adauga in coada qFetch.

 Thread-ul threadDetect preia ultimul frame adaugat in coada qFetch, face detectia fetelor cu
 algoritmul Viola Jones (cu identificatori Haar), si adauga un dreptunghi in jurul acestora.
 Frame-ul rezultat este adaugat in coada qDetect.

 Thread-ul threadPrint preia ultimul frame din qDetect si il afiseaza.

 TODO:Trebuie foarte mult optimizat.

 */
//=================================================================================================
int main(int argc, char** argv)
{
	//	pthread_t ThreadServer;
	pthread_t ThreadDetect, ThreadPrint, ThreadServer, ThreadUDP;
	int errThread;
	if(initApplication() == 0)
	{

		errThread = pthread_create(&ThreadDetect, NULL, &detectAndDisplay, NULL); //Create thread for managing new client's requests
		if (errThread != 0)
		{
			printf("\nCan't create thread :[%s]", strerror(errThread));
		}
		errThread = pthread_create(&ThreadPrint, NULL, &print, NULL); //Create thread for managing new client's requests
		if (errThread != 0)
		{
			printf("\nCan't create thread :[%s]", strerror(errThread));
		}
		errThread = pthread_create(&ThreadServer, NULL, &runTcpServer, NULL); //Create thread for managing new client's requests
		if (errThread != 0)
		{
			printf("\nCan't create thread :[%s]", strerror(errThread));
		}
		errThread = pthread_create(&ThreadUDP, NULL, &runUdpServer, NULL); //Create thread for managing new client's requests
		if (errThread != 0)
		{
			printf("\nCan't create thread :[%s]", strerror(errThread));
		}

	}else
	{
		perror("Something went wrong when initializing the application!");
		return -1;
	}
	printf("Unknown command!\n=============================\n");

	fetchFrames(); //Thread-ul main se va ocupa de fetch frames
	//	while(1);

	cout<<"Program terminated!"<<endl;
	return 100;

}

//----------------------------------------------------------------------------------------------








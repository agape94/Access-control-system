//=================================================================================================
// Name        : frames.cpp
// Author      : Alexandru Agape
// Version     :
// Copyright   : Your copyright notice
// Description : The is the header file, where image processing functions are declared
//=================================================================================================

#ifndef FRAMES_H_
#define FRAMES_H_

#include<iostream>
#include<queue>
//#include<opencv2/face.hpp>
#include<thread>
#include "initialize.h"
#include "tcpserver.h"
#include "udpserver.h"
//=================================================================================================
#define  limQFetch   200
#define  limQDetect  200
//=================================================================================================
void* detectAndDisplay(void *argument);
//=================================================================================================
void* print(void *argument);
//=================================================================================================
void fetchFrames();
//=================================================================================================
int initApplication();
//=================================================================================================



#endif /* FRAMES_H_ */

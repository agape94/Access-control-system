//=================================================================================================
// Name        : udpserver.h
// Author      : Alexandru Agape
// Version     :
// Copyright   : Your copyright notice
// Description : The file is the header file for the udp server
//=================================================================================================

#ifndef UDPSERVER_H_
#define UDPSERVER_H_
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include <netdb.h>
#include <thread>
#include <vector>
//#include <opencv2/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "utils.h"

//=================================================================================================

#define PORT_UDP 3603
#define BUFSIZE 516
#define GET_BYTE(number,idx, sizeOfVarType) ((number) << (sizeOfVarType - idx -1)*8 >> (sizeOfVarType -1)*8)

extern bool streaming, headerReceived, imageReceived;
extern std::vector<cv::Mat> qFramesForStreaming;

//=================================================================================================
int initUDPServer();
//=================================================================================================
void* runUdpServer(void *argument);
//=================================================================================================
//void* recvFromUdpClients(void* argument);
//=================================================================================================
void sendToUdpClient(uchar* buf, int length);
//=================================================================================================
char* receiveFromUdpClient();
//=================================================================================================




#endif /* UDPSERVER_H_ */

//=================================================================================================
// Name        : udpserver.cpp
// Author      : Alexandru Agape
// Version     :
// Copyright   : Your copyright notice
// Description : The file is the source file for the udp server
//=================================================================================================

#include "udpserver.h"

int sockfdUdp;		/* socket */
unsigned int clientlen;		/* byte size of client's address */
struct sockaddr_in serveraddr;	/* server's addr */
struct sockaddr_in clientaddr;	/* client addr */
struct hostent *hostp;	/* client host info */
char bufRecvUdp[BUFSIZE] = "mesaaj";		/* message buf */
char *bufSendUdp = new char[BUFSIZE + 1];
char *hostaddrp;	/* dotted decimal host addr string */
int broadcastEnable = 1;		/* flag value for setsockopt */
int n;			/* message byte size */
std::vector<sockaddr_in> clients;

//=================================================================================================
void sendToUdpClient(uchar* buf, int length)
{
	n = sendto(sockfdUdp, buf, length, 0, (struct sockaddr *) &clientaddr, clientlen);

	if (n < 0)
	{
		printf("ERROR in sendto\n");
	}
	else{
		//		printf("\nSent %d bytes\n ",n);
	}
}
//=================================================================================================
char* receiveFromUdpClient()
{
	strcpy(bufRecvUdp,"");
	n = recvfrom(sockfdUdp, bufRecvUdp, BUFSIZE, 0,(struct sockaddr *) &clientaddr, &clientlen);
	if (n < 0)
	{
		printf("ERROR in recvfrom , %s",bufRecvUdp);
	}
	printf("server received %d/%d bytes: %s\n", (int)strlen(bufRecvUdp), n, bufRecvUdp);
	return bufRecvUdp;
}
//=================================================================================================
int initUDPServer()
{
	/*
	 * socket: create the parent socket
	 */
	sockfdUdp = socket(AF_INET, SOCK_DGRAM, 0);
	if (sockfdUdp < 0)
	{
		printf("ERROR opening socket\n");
		return -1;
	}

	/* setsockopt: Handy debugging trick that lets
	 * us rerun the server immediately after we kill it;
	 * otherwise we have to wait about 20 secs.
	 * Eliminates "ERROR on binding: Address already in use" error.
	 */
	setsockopt(sockfdUdp, SOL_SOCKET, SO_REUSEADDR, (const void *)&broadcastEnable , sizeof(broadcastEnable));

	/*
	 * build the server's Internet address
	 */
	bzero((char *) &serveraddr, sizeof(serveraddr));
	serveraddr.sin_family = AF_INET;
	serveraddr.sin_addr.s_addr = htonl(INADDR_ANY);
	serveraddr.sin_port = htons((unsigned short)PORT_UDP);

	/*
	 * bind: associate the parent socket with a port
	 */
	if (bind(sockfdUdp, (struct sockaddr *) &serveraddr, sizeof(serveraddr)) < 0)
	{
		printf("ERROR on binding\n");
		return -1;
	}

	clientlen = sizeof(clientaddr);

	return 0;
}
//=================================================================================================
//void* recvFromUdpClients(void* argument)
//{
//	while(1)
//	{
//		n = recvfrom(sockfdUdp, bufRecvUdp, BUFSIZE, 0,(struct sockaddr *) &clientaddr, &clientlen);
//		if (n < 0)
//		{
//			printf("ERROR in recvfrom , %s",bufRecvUdp);
//		}
//		printf("server received %d/%d bytes: %s\n", (int)strlen(bufRecvUdp), n, bufRecvUdp);
//
//		if(strcmp(bufRecvUdp,HOME_START_STREAMING) == 0)
//		{
//			clients.push_back(clientaddr);
//			printf("\nStream started\n");
//		}else if(strcmp(bufRecvUdp,HOME_STOP_STREAMING) == 0){
//			//TODO gasire index client si stergerea lui
//			printf("\nStream stopped\n");
//		}
//	}
//
//	return NULL;
//}
//=================================================================================================
void* runUdpServer(void *argument)
{
	if(initUDPServer() != 0)
	{
		printf("Error at initializing the UDP server\n");
	}

	//	pthread_t threadRecvUdp;
	//	int errThread;
	//	errThread = pthread_create(&threadRecvUdp, NULL, &recvFromUdpClients, NULL);
	//	if (errThread != 0)
	//	{
	//		printf("\nCan't create thread :[%s]", strerror(errThread));
	//	}

	uchar* imaginePNG = 0;
	cv::Mat currentFrame;
	std::vector<uchar> buf;
	int begin, end;

	while(strcmp(receiveFromUdpClient(),HOME_START_STREAMING) != 0) {}

	while (1)
	{
		if(streaming && !qFramesForStreaming.empty())
		{
			begin = 0;
			end = 0;
			currentFrame = qFramesForStreaming.back();
			qFramesForStreaming.pop_back();
			//			cv::Mat grayFrame;
			//			cv::cvtColor(currentFrame, grayFrame, CV_BGR2GRAY);
			buf.erase(buf.begin(),buf.end());
			cv::imencode(".jpg", currentFrame, buf);
			printf("NrBytes: %d\n",(int)buf.size());
			buf.shrink_to_fit();

			unsigned int numarPachete, numarBytes;
			numarBytes = buf.size();

			if(numarBytes % (BUFSIZE - sizeof(unsigned int)) != 0)
			{
				numarPachete = numarBytes / (BUFSIZE - sizeof(unsigned int)) + 1;
			}
			else
			{
				numarPachete = numarBytes / (BUFSIZE - sizeof(unsigned int));
			}

			std::vector<uchar> header;
			for(int i = sizeof(unsigned int) - 1 ; i >= 0 ; i--)
			{
				header.insert(header.begin(), GET_BYTE(numarBytes,i,sizeof(unsigned int)));
			}//adaug numarul total de bytes din imagine
			for(int i = sizeof(unsigned int)-1 ; i >= 0 ; i--)
			{
				header.insert(header.begin(), GET_BYTE(numarPachete,i,sizeof(unsigned int)));
			}//adaug numarul total de pachete

			header.shrink_to_fit();

			imaginePNG = header.data();
			sendToUdpClient(imaginePNG, header.size()); //Trimit cele doua valori clientului

			while(!headerReceived && streaming) //verific daca clientul a primit valorile
			{
				std::this_thread::sleep_for(std::chrono::milliseconds(5));
			}

			headerReceived = false;

			for(unsigned int k = 0 ; k < numarPachete ; k ++) //trimit pachete de cate BUFSIZE octeti
			{
				if(k == numarPachete - 1)
				{
					end = buf.size() - 1;
					std::vector<uchar> newBuf(&buf[begin],&buf[end]);
					for(int i = sizeof(unsigned int)-1 ; i >= 0 ; i--)
					{
						newBuf.insert(newBuf.begin() , GET_BYTE(k,i,sizeof(unsigned int)));
					}
					printf("\n==================\n");
					printf("Index: %d\n",k);
					printf("Begin: %d\n",begin);
					printf("End: %d\n",end);
					printf("Total: %d\n",(int)buf.size());
					printf("End - Begin: %d\n", end - begin + 1);
					newBuf.shrink_to_fit();
					imaginePNG = newBuf.data();
					printf("Length: %d\n",(int)newBuf.size());
					sendToUdpClient(imaginePNG,newBuf.size());
					printf("==================\n");

					while(!imageReceived && streaming)
					{
						std::this_thread::sleep_for(std::chrono::milliseconds(5));
					}

					imageReceived = false;
					//daca clientul a raspuns, inseamna ca se poate trece la urmatorul frame
				}else
				{
					printf("\n==================\n");
					printf("Index: %d\n",k);

					end = end + (BUFSIZE - sizeof(unsigned int));
					printf("Begin: %d\n",begin);
					printf("End: %d\n",end);
					printf("Total: %d\n",(int)buf.size());
					printf("End - Begin: %d\n", end - begin);

					std::vector<uchar> newBuf(&buf[begin],&buf[end]);
					for(int i = sizeof(unsigned int)-1 ; i >= 0 ; i--)
					{
						newBuf.insert(newBuf.begin() , GET_BYTE(k,i,sizeof(unsigned int)));
					}
					newBuf.shrink_to_fit();
					imaginePNG = newBuf.data();
					printf("Length: %d\n",(int)newBuf.size());
					sendToUdpClient(imaginePNG,newBuf.size());
					std::this_thread::sleep_for(std::chrono::milliseconds(10));


					begin = end;
					//				end = end + 1;
					printf("==================\n");
				}

			}

		}
		std::this_thread::sleep_for(std::chrono::milliseconds(5));
	}
	printf("Closing UDP server...\n");
	return NULL;

}








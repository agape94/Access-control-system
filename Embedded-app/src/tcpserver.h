//=================================================================================================
// Name        : tcpserver.h
// Author      : Alexandru Agape
// Version     :
// Copyright   : Your copyright notice
// Description : The file is the header file for the tcp server
//=================================================================================================

#include <stdio.h>
#include <iostream>
#include <string>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>
#include <vector>
#include <fstream>
#include <sstream>
#include <libxml/parser.h>
#include <libxml/tree.h>
#include <pthread.h>
#include <thread>
#include "User.h"
#include "utils.h"
#include "udpserver.h"

//=================================================================================================
#define PORT_TCP "3600"  // the port users will be connecting to

#define BACKLOG 10	 // how many pending connections queue will hold
#define BUFFERSIZE 1024

//=================================================================================================
void* runTcpServer(void *argument);
//=================================================================================================
void* manageClient(void *argument);
//=================================================================================================
char* receiveData(int client);
//=================================================================================================
void sendData(void* message, int client);
//=================================================================================================
int initTCPServer();
//=================================================================================================
void* get_in_addr(struct sockaddr *sa);
//=================================================================================================
void sigchld_handler(int s);
//=================================================================================================
char* readFileContent(char* path);
//=================================================================================================
User getUserWith(std::string username, std::string password);
//=================================================================================================









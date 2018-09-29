//=================================================================================================
// Name        : tcpserver.cpp
// Author      : Alexandru Agape
// Version     :
// Copyright   : Your copyright notice
// Description : The file is the source file for the tcp server
//=================================================================================================
#include "tcpserver.h"
//=================================================================================================

int sockfdTcp, connfdTcp;  // listen on sock_fd, new connection on new_fd
struct addrinfo hintsTcp, *servinfoTcp, *pTcp;
struct sockaddr_storage their_addrTcp; // connector's address information
socklen_t sin_size;
struct sigaction sa;
int yes=1;
char sTcp[INET6_ADDRSTRLEN];
int rvTcp;
int result;
bool streamIsRunning = false;
using namespace std;
std::vector <User> vectorUsers;
char logsPath[] = "/home/alex/Documents/logs.xml";
char usersPath[] = "/home/alex/Documents/users2.xml";
bool headerReceived = false, imageReceived = false;
//=================================================================================================
void sigchld_handler(int s)
{
	(void)s; // quiet unused variable warning

	// waitpid() might overwrite errno, so we save and restore it:
	int saved_errno = errno;

	while(waitpid(-1, NULL, WNOHANG) > 0);

	errno = saved_errno;
}
//=================================================================================================
// get sockaddr, IPv4 or IPv6:
void* get_in_addr(struct sockaddr *sa)
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}

	return &(((struct sockaddr_in6*)sa)->sin6_addr);
}
//=================================================================================================
int initTCPServer()
{
	memset(&hintsTcp, 0, sizeof hintsTcp);
	hintsTcp.ai_family = AF_UNSPEC;
	hintsTcp.ai_socktype = SOCK_STREAM;
	hintsTcp.ai_flags = AI_PASSIVE; // use my IP

	if ((rvTcp = getaddrinfo(NULL, PORT_TCP, &hintsTcp, &servinfoTcp)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rvTcp));
		return 1;
	}

	// loop through all the results and bind to the first we can
	for(pTcp = servinfoTcp; pTcp != NULL; pTcp = pTcp->ai_next) {
		if ((sockfdTcp = socket(pTcp->ai_family, pTcp->ai_socktype,
				pTcp->ai_protocol)) == -1) {
			perror("server: socket");
			continue;
		}

		if (setsockopt(sockfdTcp, SOL_SOCKET, SO_REUSEADDR, &yes,
				sizeof(int)) == -1) {
			perror("setsockopt");
			exit(1);
		}

		if (bind(sockfdTcp, pTcp->ai_addr, pTcp->ai_addrlen) == -1) {
			close(sockfdTcp);
			perror("server: bind");
			continue;
		}

		break;
	}

	freeaddrinfo(servinfoTcp); // all done with this structure

	if (pTcp == NULL)  {
		fprintf(stderr, "server: failed to bind\n");
		exit(1);
	}

	if (listen(sockfdTcp, BACKLOG) == -1) {
		perror("listen");
		exit(1);
	}

	sa.sa_handler = sigchld_handler; // reap all dead processes
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_RESTART;
	if (sigaction(SIGCHLD, &sa, NULL) == -1) {
		perror("sigaction");
		exit(1);
	}
	return 0;
}
//=================================================================================================
User getUserWith(string username, string password)
{
	User user;
	for(unsigned int i = 0; i < vectorUsers.size(); i++)
	{
		if(vectorUsers.at(i).getUsername().compare(username) == 0 && vectorUsers.at(i).getPassword().compare(password) == 0)
		{
			user = vectorUsers.at(i);
		}
	}
	return user;
}
//=================================================================================================
void sendData(void* message, int client)
{
	if (send(client, message, strlen((char*)message), 0) == -1)
	{
		perror("send");
	}
}
//=================================================================================================
char* receiveData(int client)
{
	char buffer[100000];
	int n1 = recv(client, buffer, 1024, 0);
	buffer[n1] = '\0';
	int nrChar = n1;
	if(strstr(buffer, MESSAGE_STOP_FLAG) == NULL && n1 > 0)
	{
		char aux[1025];
		while(strstr(buffer,MESSAGE_STOP_FLAG) == NULL)
		{
			int n = recv(client, aux, 1024,0);
			if(n > 0)
			{
				aux[n] = '\0';
				strncat(buffer,aux,strlen(aux)+1);
				nrChar += strlen(aux);
			}
		}
	}else if(n1 < 0)
	{
		perror("Error at receive data");
		return NULL;
	}else if(n1 == 0)
	{
		return NULL;
		printf("NULL\n");
	}

	char *message = 0;
	int nrCharNoFlag = strlen(buffer) - strlen(MESSAGE_STOP_FLAG) - 1;
	buffer[nrCharNoFlag] = '\0';
	message = (char*)malloc(nrCharNoFlag+1);
	strcpy(message, buffer);
	return message;
}
//=================================================================================================
void* manageClient(void *argument)
{
	int connfdLocal = *(int*)argument;
	User lastAdmin;


	while(1)
	{
		char* buffer;
		buffer = receiveData(connfdLocal);

		if(buffer == NULL)
		{
			close(connfdLocal);
			break;
		}

		string sBuffer = buffer;
		string username, password;
		User mUser;
		free(buffer);

		size_t spatiu = sBuffer.find_first_of(' ');
		string tipSolicitare = sBuffer.substr(0,spatiu);
		sBuffer = sBuffer.substr(spatiu+1);

		if( (tipSolicitare.compare(ADMINISTRATOR_GET_USERS)!=0) &&
				(tipSolicitare.compare(ADMINISTRATOR_USERS_CHANGED)!=0) )
		{

			spatiu = sBuffer.find_first_of(' ');
			username = sBuffer.substr(0,spatiu);
			password = sBuffer.substr(spatiu+1);

			mUser = getUserWith(username,password);
		}

		char *message = 0;
		if(tipSolicitare.compare(LOGIN_USER_COMMAND) == 0)
		{

			if(mUser.getUsername().compare(username)==0 && mUser.getPassword().compare(password)==0 && username.compare("")!=0 && password.compare("")!=0)
			{
				message = prepareMesaj((string)LOGIN_USER_RESPONSE_OK + " " + mUser.getFirstName() + " " + mUser.getLastName() + " " + bool2string(mUser.isAdministrator()));
				logAction(LOG_USER_LOGIN,mUser);
			}else
			{
				message = prepareMesaj(LOGIN_USER_RESPONSE_FAILED);
			}

		}else if(tipSolicitare.compare(HOME_OPEN_DOOR_BUTTON) == 0)
		{
			if(mUser.getPassword().compare(password) == 0)
			{
				message = prepareMesaj(HOME_OPEN_DOOR_BUTTON_RESPONSE_OK);
				logAction(LOG_ENTER,mUser);
			}else{
				message = prepareMesaj(HOME_OPEN_DOOR_BUTTON_RESPONSE_FAILED);
			}

		}else if(tipSolicitare.compare(HOME_OPEN_DOOR_FINGERPRINT) == 0)
		{
			message = prepareMesaj(HOME_OPEN_DOOR_FINGERPRINT_RESPONSE_OK);
			logAction(LOG_ENTER,mUser);

		}else if(tipSolicitare.compare(ADMINISTRATOR_LOGIN_COMMAND) == 0)
		{

			if(mUser.getUsername().compare(username)==0 && mUser.getPassword().compare(password)==0 && mUser.isAdministrator())
			{
				message = prepareMesaj(ADMINISTRATOR_LOGIN_RESPONSE_OK);
				logAction(LOG_ADMIN_LOGIN,mUser);
				lastAdmin = mUser;
			}else{
				message = prepareMesaj(ADMINISTRATOR_LOGIN_RESPONSE_FAILED);
			}
		}else if(tipSolicitare.compare(ADMINISTRATOR_GET_USERS) == 0)
		{
			char* fisierXML = readFileContent(usersPath);
			message = prepareMesaj(fisierXML);
			free(fisierXML);
		}
		else if(tipSolicitare.compare(ADMINISTRATOR_USERS_CHANGED) == 0)
		{

			writeFileContent(usersPath,(char*)sBuffer.c_str());
			parseUsersXmlFile(usersPath, vectorUsers);
			//			for(unsigned int i = 0; i < vectorUsers.size(); i++)
			//			{
			//				vectorUsers.at(i).printUserInfo();
			//			}
			logAction(LOG_ADMIN_USER_CHANGED,lastAdmin);
			continue;

		}
		else if(tipSolicitare.compare(HOME_START_STREAMING) == 0)
		{
			logAction(LOG_STREAM_STARTED,mUser);
			streaming = true;
			//			udpClients.push_back(connfdLocal);
			message = prepareMesaj(HOME_STREAM_STARTED);
		}
		else if(tipSolicitare.compare(HOME_STOP_STREAMING) == 0)
		{
			logAction(LOG_STREAM_STOPPED,mUser);
			streaming = false;
			message = prepareMesaj(HOME_STREAM_STOPPED);
			qFramesForStreaming.erase(qFramesForStreaming.begin(),qFramesForStreaming.end());
		}
		else if(tipSolicitare.compare(HEADER_RECEIVED) == 0)
		{
			headerReceived = true;
			continue;
		}
		else if(tipSolicitare.compare(IMAGE_RECEIVED) == 0)
		{
			imageReceived = true;
			continue;
		}
		else if(tipSolicitare.compare(LOGS_LOGIN_COMMAND) == 0)
		{
			if(mUser.getUsername().compare(username)==0 && mUser.getPassword().compare(password)==0 && mUser.isAdministrator())
			{
				message = prepareMesaj(LOGS_LOGIN_RESPONSE_OK);
				//				logAction(LOG_ADMIN_LOGIN,mUser);
			}else{
				message = prepareMesaj(LOGS_LOGIN_RESPONSE_FAILED);
			}
		}else if(tipSolicitare.compare(LOGS_GET_LOG_LIST) == 0)
		{
			char* fisierXML = readFileContent(logsPath);
			message = prepareMesaj(fisierXML);
			free(fisierXML);
		}else{
			printf("Unknown command!\n=============================\n");
			close(connfdLocal);
			message = prepareMesaj(" ");
			break;
		}
		sendData(message,connfdLocal);
		free(message);
		message = 0;
	}
	return NULL;
}
//=================================================================================================
void* runTcpServer(void *argument)
{
	int errThread,errServer;
	pthread_t tid;
	parseUsersXmlFile(usersPath, vectorUsers);
	errServer = initTCPServer();

	if(errServer != 0)
	{
		printf("There was an error when initializing the server! Error code: %d \n",result);
		return NULL;
	}

	parseLogsXmlFile(logsPath);

	while(1)
	{
		printf("server: waiting for connections...\n");

		sin_size = sizeof their_addrTcp;

		connfdTcp = accept(sockfdTcp, (struct sockaddr *)&their_addrTcp, &sin_size);
		if(connfdTcp < 0)
		{
			printf("Error in accept\n");
			continue;
		}

		inet_ntop(their_addrTcp.ss_family,	get_in_addr((struct sockaddr *)&their_addrTcp),sTcp, sizeof sTcp);
		printf("server: got connection from %s\n", sTcp);

		errThread = pthread_create(&tid, NULL, &manageClient, &connfdTcp); //Create thread for managing new client's requests
		if (errThread != 0)
		{
			printf("\nCan't create thread :[%s]", strerror(errThread));
		}
		std::this_thread::sleep_for(std::chrono::milliseconds(30));
	}

	return NULL;

}
//=================================================================================================




















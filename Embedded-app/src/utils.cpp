/*
 * utils.cpp
 *
 *  Created on: 21 mar. 2018
 *      Author: root
 */

#include "utils.h"
using namespace std;
using namespace cv;

std::vector<LogAction> logs;
//=================================================================================================
char* string2char(std::string mes)
{
	char* message;
	message = (char*)malloc(mes.size()+1);
	message = (char*)mes.c_str();
	message[mes.size()] = '\0';
	return message;
}
//=================================================================================================
bool string2bool(string m)
{
	bool b = false;

	if(m.compare("true") == 0){
		b = true;
	}else if(m.compare("false") == 0)
	{
		b = false;
	}
	return b;
}
//=================================================================================================
string bool2string(bool b)
{
	string m = "";

	if(b == true){
		m = "true";
	}else if(b == false)
	{
		m = "false";
	}
	return m;
}
//=================================================================================================
void parseUsersXmlFile(string path, vector <User> &vectorUsers)
{
	xmlDoc *doc = NULL;
	xmlNode *rootNode = NULL;

	User user;
	vectorUsers.erase(vectorUsers.begin(),vectorUsers.end());
	doc = xmlReadFile(path.c_str(),NULL,0);
	if(doc == NULL)
	{
		printf("error: could not parse file %s\n",path.c_str());
	}

	rootNode = xmlDocGetRootElement(doc);

	xmlNode *currentNode = NULL, *currentChildNode = NULL;

	for(currentNode = rootNode->children ; currentNode ; currentNode = currentNode->next)
	{
		if(currentNode->type == XML_ELEMENT_NODE)
		{
			for(currentChildNode = currentNode->children ; currentChildNode ;currentChildNode = currentChildNode->next)
			{
				if(strcmp((char *)currentChildNode->name,"first_name") == 0)
				{
					user.setFirstName((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"last_name") == 0)
				{
					user.setLastName((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"username") == 0)
				{
					user.setUsername((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"password") == 0)
				{
					user.setPassword((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"administrator") == 0)
				{
					user.setIsAdministrator(string2bool((string)((char*)xmlNodeGetContent(currentChildNode))));
				}
			}
			vectorUsers.push_back(user);
		}
	}


	//	xmlFreeNode(currentNode);
	//	xmlFreeNode(currentChildNode);
	//	xmlFreeNode(rootNode);
	xmlFreeDoc(doc);
}

//=================================================================================================
void parseLogsXmlFile(string path)
{
	xmlDoc *doc = NULL;
	xmlNode *rootNode = NULL;

	LogAction newAction;
	logs.erase(logs.begin(),logs.end());
	User user;
	doc = xmlReadFile(path.c_str(),NULL,0);
	if(doc == NULL)
	{
		printf("error: could not parse file %s\n",path.c_str());
	}

	rootNode = xmlDocGetRootElement(doc);

	xmlNode *currentNode = NULL, *currentChildNode = NULL;

	for(currentNode = rootNode->children ; currentNode ; currentNode = currentNode->next)
	{
		if(currentNode->type == XML_ELEMENT_NODE)
		{
			for(currentChildNode = currentNode->children ; currentChildNode ;currentChildNode = currentChildNode->next)
			{
				if(strcmp((char *)currentChildNode->name,"type") == 0)
				{
					newAction.setActionType((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"time") == 0)
				{
					newAction.setTime((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"first_name") == 0)
				{
					user.setFirstName((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"last_name") == 0)
				{
					user.setLastName((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"username") == 0)
				{
					user.setUsername((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"password") == 0)
				{
					user.setPassword((string)((char*)xmlNodeGetContent(currentChildNode)));
				}
				if(strcmp((char *)currentChildNode->name,"administrator") == 0)
				{
					user.setIsAdministrator(string2bool((string)((char*)xmlNodeGetContent(currentChildNode))));
				}

			}
			newAction.setUser(user);
			logs.push_back(newAction);
		}
	}


	//	xmlFreeNode(currentNode);
	//	xmlFreeNode(currentChildNode);
	//	xmlFreeNode(rootNode);
	xmlFreeDoc(doc);
}
//=================================================================================================
void writeFileContent(char* filename, char* buffer)
{
	FILE *f = fopen(filename, "w");
	if (f == NULL)
	{
		printf("Error opening file!\n");
		exit(1);
	}

	fprintf(f, "%s", buffer);
	fclose(f);
}
//=================================================================================================
char* readFileContent(char* path)
{
	//Deschid fisierul xml de la path-ul dat ca argument
	char* fcontent;
	FILE *fp = fopen(path,"rb");
	if(fp == NULL)
	{
		printf("Error opening the file %s\n",path);
		return NULL;
	}

	//Aflu dimensiunea acestuia pentru a il citi in totalitate in buffer
	long size;
	fseek(fp, 0, SEEK_END);
	size = ftell(fp);
	fseek(fp, 0, SEEK_SET);

	fcontent = (char*)malloc(size+1);
	fread(fcontent, 1, size, fp); //Citirea efectiva in buffer

	fclose(fp);
	fcontent[size] = '\0';
	return fcontent;
}
//=================================================================================================
char* prepareMesaj(string message)
{
	message = message + " " + string(MESSAGE_STOP_FLAG);
	cout << message << endl;
	char* result = (char*)malloc(message.length()+1);
	strncpy(result,message.c_str(),message.length());
	return result;
}
//=================================================================================================
void logAction(int actionType, User user)
{
	LogAction newAction;
	std::string action;

	switch(actionType){
	case LOG_USER_LOGIN:
		action = LOG_USER_LOGIN_STR;
		break;
	case LOG_ADMIN_LOGIN:
		action = LOG_ADMIN_LOGIN_STR;
		break;
	case LOG_ENTER:
		action = LOG_ENTER_STR;
		break;
	case LOG_ADMIN_USER_CHANGED:
		action = LOG_ADMIN_USER_CHANGED_STR;
		break;
	case LOG_STREAM_STARTED:
		action = LOG_STREAM_STARTED_STR;
		break;
	case LOG_STREAM_STOPPED:
		action = LOG_STREAM_STOPPED_STR;
		break;
	case LOG_LOGS_LOGIN:
		action = LOG_LOGS_LOGIN_STR;
		break;
	}

	newAction.setActionType(action);
	newAction.setUser(user);

	logs.push_back(newAction);
	string mXmlContent;
	mXmlContent += (string)XML_VERSION + "\n" + ROOT_NODE_START + "\n";

	for(unsigned int i = 0 ; i < logs.size() ; i++)
	{
		LogAction act = logs.at(i);
		User user = act.getUser();

		mXmlContent += "\t" + (string)ACTION_NODE_START + "\n";
		mXmlContent += "\t\t"+(string)ACTION_TYPE_START + act.getActionType() + (string)ACTION_TYPE_END + "\n";
		mXmlContent += "\t\t"+(string)ACTION_TIME_START + act.getTime() + (string)ACTION_TIME_END + "\n";
		mXmlContent += "\n\t\t"+(string)FIRST_NAME_START + user.getFirstName() + (string)FIRST_NAME_END + "\n";
		mXmlContent += "\t\t"+(string)LAST_NAME_START + user.getLastName() + (string)LAST_NAME_END + "\n";
		mXmlContent += "\t\t"+(string)USERNAME_START + user.getUsername() + (string)USERNAME_END + "\n";
		mXmlContent += "\t\t"+(string)PASSWORD_START + user.getPassword() + (string)PASSWORD_END + "\n";
		mXmlContent += "\t\t"+(string)ADMINISTRATOR_START + bool2string(user.isAdministrator()) + (string)ADMINISTRATOR_END + "\n";
		mXmlContent += "\t"+(string)ACTION_NODE_END + "\n";
	}
	mXmlContent += ROOT_NODE_END;

	char path[] = "/home/alex/Documents/logs.xml";
	writeFileContent(path,(char*)mXmlContent.c_str());

}
//=================================================================================================
char* encodeImagePng(cv::Mat img)
{
	char* imgEncoded = 0;
	std::vector<uchar> buf;
	imencode(".bmp", img, buf, std::vector<int>() );
	printf("Size: %d bytes\n",(int)buf.size());
	imgEncoded = (char*)malloc(buf.size());
	std::copy(buf.begin(), buf.end(), imgEncoded);
	return imgEncoded;
}
//=================================================================================================




















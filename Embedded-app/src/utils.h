/*
 * utils.h
 *
 *  Created on: 21 mar. 2018
 *      Author: root
 */

#ifndef UTILS_H_
#define UTILS_H_
#include <iostream>
#include <string>
#include <string.h>
#include <vector>
#include <time.h>
#include <libxml/parser.h>
#include <libxml/tree.h>
//#include <opencv2/core.hpp>
#include <opencv2/highgui.hpp>
#include "User.h"
#include "LogAction.h"
#include "tcpserver.h"


//=================================================================================================

#define 		LOGIN_USER_COMMAND         	 	  	     "login_user"
#define			LOGIN_USER_RESPONSE_OK                   "login_ok"
#define			LOGIN_USER_RESPONSE_FAILED               "login_failed"
#define			HOME_OPEN_DOOR_BUTTON                    "home_open"
#define		 	HOME_OPEN_DOOR_BUTTON_RESPONSE_OK        "home_open_ok"
#define			HOME_OPEN_DOOR_BUTTON_RESPONSE_FAILED    "home_open_failed"
#define 		HOME_OPEN_DOOR_FINGERPRINT               "home_fingerprint"
#define	 		HOME_OPEN_DOOR_FINGERPRINT_RESPONSE_OK	 "home_fingerprint_ok"
#define			HOME_START_STREAMING                     "home_start_stream"
#define			HOME_STREAM_STARTED                   	 "home_stream_started"
#define			HOME_STOP_STREAMING                      "home_stop_stream"
#define			HOME_STREAM_STOPPED                      "home_stream_stopped"
#define 		ADMINISTRATOR_GET_USERS                  "administrator_get_users"
#define 		ADMINISTRATOR_USER_SENT_SUCCESSFULLY 	 "users_sent_successfully"
#define			ADMINISTRATOR_USERS_CHANGED              "administrator_users_changed"
#define 		ADMINISTRATOR_LOGIN_COMMAND              "administrator_login"
#define 		ADMINISTRATOR_LOGIN_RESPONSE_OK          "administrator_login_ok"
#define			ADMINISTRATOR_LOGIN_RESPONSE_FAILED      "administrator_login_failed"
#define			TCP_BROADCAST_FILTER                     "tcp_broadcast_filter"
#define		 	UNKNOWN_COMMAND		                     "unknown_command"
#define 		SERVER_DOWN                              "server_down"
#define 		CONNECTION_LOST                          "connection_lost"
#define 		CONNECTION_ESTABLISHED                   "connection_established"
#define 		LOGS_LOGIN_COMMAND            			 "logs_login"
#define 		LOGS_LOGIN_RESPONSE_OK            		 "logs_login_ok"
#define 		LOGS_LOGIN_RESPONSE_FAILED     			 "logs_login_failed"
#define 		LOGS_GET_LOG_LIST     		        	 "logs_get_log_list"
#define 		MESSAGE_START_FLAG                       "message_start"
#define 		MESSAGE_STOP_FLAG                        "message_stop"
#define 		HEADER_RECEIVED							 "header_received"
#define 		IMAGE_RECEIVED							 "image_received"
#define 		IMAGE_TRANSMITTED						 "image_transmitted"

#define 		LOG_USER_LOGIN                        	 1
#define 		LOG_USER_LOGIN_STR                     	 "user_logged_in"
#define 		LOG_ADMIN_LOGIN                       	 2
#define 		LOG_ADMIN_LOGIN_STR                      "admin_logged_in"
#define 		LOG_ENTER		                      	 3
#define 		LOG_ENTER_STR                            "door_open"
#define 		LOG_ADMIN_USER_CHANGED                	 4
#define 		LOG_ADMIN_USER_CHANGED_STR               "user_changed"
#define 		LOG_STREAM_STARTED	                 	 5
#define 		LOG_STREAM_STARTED_STR             	     "stream_started"
#define 		LOG_STREAM_STOPPED	                 	 6
#define 		LOG_STREAM_STOPPED_STR                   "stream_stopped"
#define 		LOG_LOGS_LOGIN			                 7
#define 		LOG_LOGS_LOGIN_STR			             "logs_login"

#define 		MESSAGE_STOP_FLAG                        "message_stop"

#define		 	XML_VERSION								 "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
#define			ROOT_NODE_START 						 "<log_document>"
#define			ROOT_NODE_END 							 "</log_document>"
#define			ACTION_NODE_START 						 "<action>"
#define			ACTION_NODE_END 						 "</action>"
#define			ACTION_TYPE_START 						 "<type>"
#define			ACTION_TYPE_END  						 "</type>"
#define			ACTION_TIME_START					     "<time>"
#define			ACTION_TIME_END 						 "</time>",
#define			FIRST_NAME_START 						 "<first_name>"
#define			FIRST_NAME_END 							 "</first_name>"
#define			LAST_NAME_START 						 "<last_name>"
#define			LAST_NAME_END 							 "</last_name>"
#define			USERNAME_START 							 "<username>"
#define			USERNAME_END 							 "</username>"
#define			PASSWORD_START 							 "<password>"
#define			PASSWORD_END 							 "</password>"
#define			ADMINISTRATOR_START 					 "<administrator>"
#define			ADMINISTRATOR_END 						 "</administrator>"

//=================================================================================================
char* string2char(std::string mes);
//=================================================================================================
bool string2bool(std::string m);
//=================================================================================================
std::string bool2string(bool b);
//=================================================================================================
void parseUsersXmlFile(std::string path, std::vector <User> &vectorUsers);
//=================================================================================================
void parseLogsXmlFile(std::string path);
//=================================================================================================
void writeFileContent(char* filename, char* recvBuffer);
//=================================================================================================
char* readFileContent(char* path);
//=================================================================================================
char* prepareMesaj(std::string message);
//=================================================================================================
void logAction(int actionType, User user);
//=================================================================================================
char* encodeImagePng(cv::Mat img);
//=================================================================================================

#endif /* UTILS_H_ */

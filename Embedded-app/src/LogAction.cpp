/*
 * LogAction.cpp
 *
 *  Created on: 10 apr. 2018
 *      Author: root
 */

#include "LogAction.h"
//=================================================================================================
LogAction::LogAction() {
	mActionType = "";
	time_t rawtime;
	struct tm * timeinfo;
	char buffer[80];

	time(&rawtime);
	timeinfo = localtime(&rawtime);
	strftime (buffer,80,"%T|%a %d %b %Y",timeinfo);
	mTime = (std::string) buffer;
}
//=================================================================================================
LogAction::~LogAction() {
	// TODO Auto-generated destructor stub
}
//=================================================================================================
std::string LogAction::getActionType()
{
	return mActionType;
}
//=================================================================================================
void LogAction::setActionType(std::string actionType)
{
	mActionType = actionType;
}
//=================================================================================================
std::string LogAction::getTime()
{
	return mTime;
}
//=================================================================================================
void LogAction::setTime(std::string time)
{
	mTime = time;
}
//=================================================================================================
User LogAction::getUser()
{
	return mUser;
}
//=================================================================================================
void LogAction::setUser(User user)
{
	mUser = user;
}
//=================================================================================================

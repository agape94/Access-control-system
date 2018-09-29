/*
 * LogAction.h
 *
 *  Created on: 10 apr. 2018
 *      Author: root
 */

#ifndef LOGACTION_H_
#define LOGACTION_H_
#include <string>
#include <string.h>
#include <time.h>
#include <stdio.h>
#include "User.h"

class LogAction {
//=================================================================================================
private:
	std::string mActionType;
	std::string mTime;
	User mUser;
//=================================================================================================
public:
	LogAction();
	//=================================================================================================
	std::string getActionType();
	void setActionType(std::string actionType);
	//=================================================================================================
	std::string getTime();
	void setTime(std::string time);
	//=================================================================================================
	User getUser();
	void setUser(User user);
	//=================================================================================================
	virtual ~LogAction();
};

#endif /* LOGACTION_H_ */

/*
 * User.h
 *
 *  Created on: 25 feb. 2018
 *      Author: root
 */

#ifndef USER_H_
#define USER_H_
#include <string>
#include <iostream>
class User {
//=================================================================================================
private:
	std::string mFirstName;
	std::string mLastName;
	std::string mUsername;
	std::string mPassword;
	bool mIsAdministrator;
//=================================================================================================
public:
	User(std::string firstName, std::string lastName, std::string username, std::string password, bool administrator);
	User();
	//=================================================================================================
	void setFirstName(std::string firstName);
	std::string getFirstName();
	//=================================================================================================
	void setLastName(std::string lastName);
	std::string getLastName();
	//=================================================================================================
	void setUsername(std::string username);
	std::string getUsername();
	//=================================================================================================
	void setPassword(std::string password);
	std::string getPassword();
	//=================================================================================================
	void setIsAdministrator(bool isAdministrator);
	bool isAdministrator();
	//=================================================================================================
	void printUserInfo();
	//=================================================================================================
	~User();
};


#endif /* USER_H_ */

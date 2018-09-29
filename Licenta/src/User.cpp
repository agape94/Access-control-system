/*
 * User.cpp
 *
 *  Created on: 25 feb. 2018
 *      Author: root
 */

#include "User.h"

using namespace std;
//=================================================================================================
User::User() {
	mIsAdministrator = false;
}
//=================================================================================================
User::User(std::string firstName, std::string lastName, std::string username, std::string password, bool administrator){
	mFirstName = firstName;
	mLastName = lastName;
	mUsername = username;
	mPassword = password;
	mIsAdministrator = administrator;
}
//=================================================================================================
void User::setFirstName(std::string firstName){
	mFirstName = firstName;
}
//=================================================================================================
std::string User::getFirstName(){
	return mFirstName;
}
//=================================================================================================
void User::setLastName(std::string lastName){
	mLastName = lastName;
}
//=================================================================================================
std::string User::getLastName(){
	return mLastName;
}
//=================================================================================================
void User::setUsername(std::string username){
	mUsername = username;
}
//=================================================================================================
std::string User::getUsername(){
	return mUsername;
}
//=================================================================================================
void User::setPassword(std::string password){
	mPassword = password;
}
//=================================================================================================
std::string User::getPassword(){
	return mPassword;
}
//=================================================================================================
void User::setIsAdministrator(bool isAdministrator){
	mIsAdministrator = isAdministrator;
}
//=================================================================================================
bool User::isAdministrator(){
	return mIsAdministrator;
}
//=================================================================================================
void User::printUserInfo()
{
	cout<<endl<<"====================================" << endl
			<<"Name: " << mFirstName << endl
			<<"Last name: "<< mLastName<< endl
			<<"Username: "<< mUsername<< endl
			<<"Password: "<< mPassword<< endl
			<<"Is administrator: "<< mIsAdministrator << endl
			<<"====================================" << endl;
}
//=================================================================================================
User::~User() {
	// TODO Auto-generated destructor stub
}
//=================================================================================================

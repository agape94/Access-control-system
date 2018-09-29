################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/Licenta.cpp \
../src/LogAction.cpp \
../src/User.cpp \
../src/frames.cpp \
../src/initialize.cpp \
../src/tcpserver.cpp \
../src/udpserver.cpp \
../src/utils.cpp 

OBJS += \
./src/Licenta.o \
./src/LogAction.o \
./src/User.o \
./src/frames.o \
./src/initialize.o \
./src/tcpserver.o \
./src/udpserver.o \
./src/utils.o 

CPP_DEPS += \
./src/Licenta.d \
./src/LogAction.d \
./src/User.d \
./src/frames.d \
./src/initialize.d \
./src/tcpserver.d \
./src/udpserver.d \
./src/utils.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -std=c++11 -I/usr/include/libxml2/ -lxml2 -pthread -ld -I/usr/local/include/opencv -I/usr/local/include/ -I/usr/local/include/opencv2 -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '



#pragma once

#include <Arduino.h>
#include <SPI.h>
#include <LittleMaths.h>

/*
 * sets how many samples are saved and than averaged
 * use fillHistory() to set all samples to the same value to avoid slow buildup at the beginning (all samples start at 0)
 */
#ifndef GY953_SAMPLES_NUMBER
	#define GY953_SAMPLES_NUMBER		1
#endif

class GY953 {
	public:
		GY953(const uint8_t cs_pin,const uint8_t int_pin);
		void begin();
		bool update();
		void getRPY(int16_t *data);
		void getACC(int16_t *data);
		void getGYR(int16_t *data);
		void getMAG(int16_t *data);
		void getQ(int16_t *data);
		void setRefreshRate(uint8_t freq);
		void getAccuracy(byte *data);
		void getRange(byte *data);
		void fillHistory();
	private:
		uint8_t CS_PIN;
		uint8_t INT_PIN;
		void writeRegister(byte reg, byte *data, int len);
		void readRegister(byte reg, byte *data, int len);
		static void interrupt();
		int16_t rpy[3] = {0};
		int16_t gyr[3] = {0};
		int16_t acc[3] = {0};
		int16_t mag[3] = {0};
		int16_t	q[4] = {0};
		bool moveIndex = true;
		int historySize = 4;
		int historyIndex = 0;
		int16_t historyRPY[3][4];
		int16_t historyGYR[3][4];
		int16_t historyACC[3][4];
		int16_t historyMAG[3][4];
};

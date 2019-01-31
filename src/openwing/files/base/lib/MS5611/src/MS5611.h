#pragma once

#include <Arduino.h>
#include <Wire.h>
#include <LittleMaths.h>

//Change these two values to change measurement resolution (value must be one of 0, 2, 4, 6, 8)
#define MS5611_PRESSURE_OSR				4		//0, 2, 4, 6, 8
#define MS5611_TEMPERATURE_OSR			4		//0, 2, 4, 6, 8

/*
 * sets how many samples are saved and than averaged
 * use fillHistory() to set all samples to the same value to avoid slow buildup at the beginning (all samples start at 0)
 */
#ifndef MS5611_SAMPLES_NUMBER
	#define MS5611_SAMPLES_NUMBER		1
#endif

class MS5611 {
	public:
		MS5611();
		void begin();
		void reset();
		bool update();
		void fillHistory();
		int32_t getTemperature();
		int32_t getPressure();
		void readCalibration();
		void getCalibration(uint16_t *C);
		int32_t pressureHistorySize = MS5611_SAMPLES_NUMBER;
	private:
		uint32_t getRawTemperature();
		uint32_t getRawPressure();
		void sendCommand(uint8_t cmd);
		uint32_t readADC();
		uint16_t readPROM(uint16_t address);
		int32_t pressure;
		int32_t temperature;
		int32_t dT;
		uint16_t calibration[6];
		int32_t pressureHistory[MS5611_SAMPLES_NUMBER];
		int32_t	pressureHistoryIndex = 0;
};

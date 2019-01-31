#include "GY953.h"

#define GY953_inRange(x, low, high) ((high >= x) && (x >= low))

#define GY953_SETREG				0x41
#define GY953_INTREG				0xC1
#define GY953_REFRESH_RATE_50		0x73
#define GY953_REFRESH_RATE_100		0x74
#define GY953_REFRESH_RATE_200		0x75

static volatile bool dataReady = false;
static byte buffer[41] = {0};
static byte rawData[41] = {0};

GY953::GY953(uint8_t CS_PIN, uint8_t INT_PIN):
    CS_PIN(CS_PIN),
    INT_PIN(INT_PIN)
{}

void GY953::begin() {
	memset(rpy, 0, 3);
	memset(gyr, 0, 3);
	memset(acc, 0, 3);
	memset(mag, 0, 3);
	memset(q, 0, 4);

	pinMode(INT_PIN, INPUT_PULLUP);
	attachInterrupt(digitalPinToInterrupt(INT_PIN), interrupt, RISING);
	
	pinMode(CS_PIN, OUTPUT);
	digitalWrite(CS_PIN, HIGH);
	
	SPI.begin();
	SPI.usingInterrupt(digitalPinToInterrupt(INT_PIN));
	
	while (!dataReady); delay(1);
}

bool GY953::update() {
	if (dataReady){
		readRegister(GY953_INTREG, buffer, 41);
		dataReady = false;
		uint8_t sum = 0;
		for (uint8_t i = 0; i < 6; i++) {
			sum += buffer[20 + i];
		}
		if (buffer[34] == 0x0D && sum == buffer[39]){
			memcpy(rawData, rawData, sizeof(rawData));
			moveIndex = true;
			return true;
		}
		else {
			return false;
		}
	} else {
		return false;
	}
}

void GY953::writeRegister(byte reg, byte *data, int length) {
	digitalWrite(CS_PIN, LOW);
	SPI.beginTransaction(SPISettings(200000, MSBFIRST, SPI_MODE3));
	SPI.transfer(reg);
	byte i = 0;
	while (i < length){
		SPI.transfer(data[i++]);
	}
	digitalWrite(CS_PIN, HIGH);
	SPI.endTransaction();
}

void GY953::readRegister(byte reg, byte *data, int length) {
	digitalWrite(CS_PIN, LOW);
	SPI.beginTransaction(SPISettings(200000, MSBFIRST, SPI_MODE3));
	SPI.transfer(reg);
	byte i = 0;
	while (i < length){
		data[i++] = SPI.transfer(0);
	}
	digitalWrite(CS_PIN, HIGH);
	SPI.endTransaction();
}

void GY953::interrupt() {
	if (!dataReady) {
		dataReady = true;
	}
}

void GY953::fillHistory() {
	while(!update());
	memset(historyRPY[0], (rawData[20] << 8) | rawData[21], historySize * sizeof(int16_t));
	memset(historyRPY[1], (rawData[22] << 8) | rawData[23], historySize * sizeof(int16_t));
	memset(historyRPY[2], (rawData[24] << 8) | rawData[25], historySize * sizeof(int16_t));
	
	memset(historyACC[0], (rawData[2] << 8) | rawData[3], historySize * sizeof(int16_t));
	memset(historyACC[1], (rawData[4] << 8) | rawData[5], historySize * sizeof(int16_t));
	memset(historyACC[2], (rawData[6] << 8) | rawData[7], historySize * sizeof(int16_t));
	
	memset(historyGYR[0], (rawData[8] << 8) | rawData[9], historySize * sizeof(int16_t));
	memset(historyGYR[1], (rawData[10] << 8) | rawData[11], historySize * sizeof(int16_t));
	memset(historyGYR[2], (rawData[12] << 8) | rawData[13], historySize * sizeof(int16_t));
	
	memset(historyMAG[0], (rawData[14] << 8) | rawData[15], historySize * sizeof(int16_t));
	memset(historyMAG[1], (rawData[16] << 8) | rawData[17], historySize * sizeof(int16_t));
	memset(historyMAG[2], (rawData[18] << 8) | rawData[19], historySize * sizeof(int16_t));

}

void GY953::setRefreshRate(uint8_t rate) {
	byte data;
	if (rate <= 50){
		data = GY953_REFRESH_RATE_50 | 0x08;
		writeRegister(GY953_SETREG, &data, 1);
	} else if (rate <=100){
		data = GY953_REFRESH_RATE_100 | 0x08;
		writeRegister(GY953_SETREG, &data, 1);
	} else {
		data = GY953_REFRESH_RATE_200 | 0x08; 
		writeRegister(GY953_SETREG, &data, 1);
	}
}

void GY953::getRPY(int16_t *data) {
	rpy[0] = (rawData[20] << 8) | rawData[21];
	rpy[1] = (rawData[22] << 8) | rawData[23];
	rpy[2] = (rawData[24] << 8) | rawData[25];

	rpy[0] = (GY953_inRange(rpy[0], -20000, 20000) && rpy[0] != 0) ? rpy[0] : data[0];
	rpy[1] = (GY953_inRange(rpy[1], -20000, 20000) && rpy[1] != 0) ? rpy[1] : data[1];
	rpy[2] = (GY953_inRange(rpy[2], -20000, 20000) && rpy[2] != 0) ? rpy[2] : data[2];

	if(moveIndex) {
		if(++historyIndex >= historySize) {
			historyIndex = 0;
		}
		moveIndex = false;
	}

	historyRPY[0][historyIndex] = rpy[0];
	historyRPY[1][historyIndex] = rpy[1];
	historyRPY[2][historyIndex] = rpy[2];


	rpy[0] = LittleMaths::average(historyRPY[0], historySize);
	rpy[1] = LittleMaths::average(historyRPY[1], historySize);
	rpy[2] = LittleMaths::average(historyRPY[2], historySize);

	memcpy(data, rpy, sizeof(rpy));
}

void GY953::getACC(int16_t *data) {
	acc[0] = (rawData[2] << 8) | rawData[3];
	acc[1] = (rawData[4] << 8) | rawData[5];
	acc[2] = (rawData[6] << 8) | rawData[7];
	
	acc[0] = (GY953_inRange(acc[0], -20000, 20000) && acc[0] != 0) ? acc[0] : data[0];
	acc[1] = (GY953_inRange(acc[1], -20000, 20000) && acc[1] != 0) ? acc[1] : data[1];
	acc[2] = (GY953_inRange(acc[2], -20000, 20000) && acc[2] != 0) ? acc[2] : data[2];

	if(moveIndex) {
		if(++historyIndex >= historySize) {
			historyIndex = 0;
		}
		moveIndex = false;
	}

	historyACC[0][historyIndex] = acc[0];
	historyACC[1][historyIndex] = acc[1];
	historyACC[2][historyIndex] = acc[2];

	acc[0] = LittleMaths::average(historyACC[0], historySize);
	acc[1] = LittleMaths::average(historyACC[1], historySize);
	acc[2] = LittleMaths::average(historyACC[2], historySize);
	memcpy(data, acc, sizeof(acc));
}

void GY953::getGYR(int16_t *data) {
	gyr[0] = (rawData[8] << 8) | rawData[9];
	gyr[1] = (rawData[10] << 8) | rawData[11];
	gyr[2] = (rawData[12] << 8) | rawData[13];
	
	gyr[0] = (GY953_inRange(gyr[0], -20000, 20000) && gyr[0] != 0) ? gyr[0] : data[0];
	gyr[1] = (GY953_inRange(gyr[1], -20000, 20000) && gyr[1] != 0) ? gyr[1] : data[1];
	gyr[2] = (GY953_inRange(gyr[2], -20000, 20000) && gyr[2] != 0) ? gyr[2] : data[2];

	if(moveIndex) {
		if(++historyIndex >= historySize) {
			historyIndex = 0;
		}
		moveIndex = false;
	}

	historyGYR[0][historyIndex] = gyr[0];
	historyGYR[1][historyIndex] = gyr[1];
	historyGYR[2][historyIndex] = gyr[2];

	gyr[0] = LittleMaths::average(historyGYR[0], historySize);
	gyr[1] = LittleMaths::average(historyGYR[1], historySize);
	gyr[2] = LittleMaths::average(historyGYR[2], historySize);

	memcpy(data, gyr, sizeof(gyr));
}

void GY953::getMAG(int16_t *data) {
	mag[0] = (rawData[14] << 8) | rawData[15];
	mag[1] = (rawData[16] << 8) | rawData[17];
	mag[2] = (rawData[18] << 8) | rawData[19];

	mag[0] = (GY953_inRange(mag[0], -20000, 20000)) ? mag[0] : data[0];
	mag[1] = (GY953_inRange(mag[1], -20000, 20000)) ? mag[1] : data[1];
	mag[2] = (GY953_inRange(mag[2], -20000, 20000)) ? mag[2] : data[2];

	if(moveIndex) {
		if(++historyIndex >= historySize) {
			historyIndex = 0;
		}
		moveIndex = false;
	}

	historyMAG[0][historyIndex] = mag[0];
	historyMAG[1][historyIndex] = mag[1];
	historyMAG[2][historyIndex] = mag[2];

	mag[0] = LittleMaths::average(historyMAG[0], historySize);
	mag[1] = LittleMaths::average(historyMAG[1], historySize);
	mag[2] = LittleMaths::average(historyMAG[2], historySize);

	memcpy(data, mag, sizeof(mag));
}

void GY953::getQ(int16_t *data) {
	q[0] = (rawData[26] << 8) | rawData[27];
	q[1] = (rawData[28] << 8) | rawData[29];
	q[2] = (rawData[30] << 8) | rawData[31];
	q[3] = (rawData[32] << 8) | rawData[33];
	
	memcpy(data, q, sizeof(q));
}

void GY953::getAccuracy(byte *data) {
	data[0] = ((rawData[35] >> 4) & 0x03);
	data[1] = ((rawData[35] >> 2) & 0x03);
	data[2] = (rawData[35] & 0x03);
	data[3] = (rawData[0] & 0x07);
}

void GY953::getRange(byte *data) {
	data[0] = ((rawData[34] >> 4) & 0x03);
	data[1] = ((rawData[34] >> 2) & 0x03);
	data[2] = (rawData[34] & 0x03);
}

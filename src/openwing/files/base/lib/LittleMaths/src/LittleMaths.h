#pragma once

#include <stdint.h>

class LittleMaths {
    public:
        static int median(int* array, int from, int to);
        static int32_t average(int32_t* array, int size);
        static int16_t average(int16_t* array, int size);
        static float average(float* array, int size);
        //sorts the array, gets value values in set distance from middle and averages them
        static int middleValuesAverage(int* array, int from, int to, int middleDistance);
        static void insertionSort(int* array, int size);
};
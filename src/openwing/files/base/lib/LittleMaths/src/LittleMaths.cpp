#include "LittleMaths.h"

int LittleMaths::median(int* arr, int from, int to) {
    int copySize = to - from;
    int* copy = new int[copySize];
    
    for(int i = 0; i < copySize; i++) {
        copy[i] = arr[i];
    }
    
    insertionSort(&copy[0], copySize);
    
    int median = copy[(copySize - 1)/2];

    delete [] copy;

    return median;
}

int LittleMaths::middleValueAverage(int* arr, int from, int to, int middleDistance) {
    int copySize = to - from;
    int* copy = new int[copySize];
    
    for(int i = 0; i < copySize; i++) {
        copy[i] = arr[i];
    }
    
    insertionSort(&copy[0], copySize);
    int middle = (copySize - 1)/2;
    int value = copy[middle];
    for(int i = 1; i < middleDistance; i++) {
        value += copy[middle + i] + copy[middle - i];
    }

    value /= middleDistance * 2 + 1;

    delete [] copy;

    return value;
}

void LittleMaths::insertionSort(int* arr, int size) {
    for(int i = 1; i < size; i++) {
        int temp = arr[i];
        int j = i - 1;
        while(j >= 0 && arr[j] > temp) {
            arr[j + 1] = arr[j];
            j--;
        }
        arr[j + 1] = temp;

    }
}

int32_t LittleMaths::average(int32_t* arr, int size) {
    int32_t sum = 0;
    for(int i = 0; i < size; i++) {
        sum += arr[i];
    }
    return sum / size;
}

int16_t LittleMaths::average(int16_t* arr, int size) {
    int16_t sum = 0;
    for(int i = 0; i < size; i++) {
        sum += arr[i];
    }
    return sum / size;
}

float LittleMaths::average(float* arr, int size) {
    float sum = 0;
    for(int i = 0; i < size; i++) {
        sum += arr[i];
    }
    return sum / size;
}
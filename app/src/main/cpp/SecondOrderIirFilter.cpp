//
// Created by philipp on 18.09.23.
//

#include "SecondOrderIirFilter.h"

float SecondOrderIirFilter::processSample(float sampleIn) {
    float out;
    w[0] = sampleIn - coeffA[0]*w[1] - coeffA[1]*w[2];
    out = coeffB[0]*w[0]+coeffB[1]*w[1]+coeffB[2]*w[2];
    w[2]=w[1];
    w[1]=w[0];
    return out;

}

SecondOrderIirFilter::SecondOrderIirFilter() {
    coeffA[0]=0.0f;
    coeffA[1]=0.0f;
    coeffB[0]=1.0f;
    coeffB[1]=0.0f;
    coeffB[2]=0.0f;
    w[0]=0.0f;
    w[1]=0.0f;
    w[2]=0.0f;
}

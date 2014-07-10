/*
 * Copyright David Weber 2014
 * Released under the Creative Commons License (http://creativecommons.org/licenses/by/4.0/legalcode)
 */
#include "za_co_monadic_scopus_speex_Speex__.h"
#include <speex/speex.h>
#include <stdlib.h>


typedef struct {
    void *st;
    SpeexBits bits;
} encoder_state;


JNIEXPORT jlong JNICALL Java_za_co_monadic_scopus_speex_Speex_00024_encoder_1create
    (JNIEnv *env, jobject clazz, jint modeID) {
    encoder_state* state = (encoder_state*)malloc(sizeof(encoder_state));
    switch (modeID) {
        case SPEEX_MODEID_NB:
            state->st = speex_encoder_init(&speex_nb_mode);
            break;
        case SPEEX_MODEID_WB:
            state->st = speex_encoder_init(&speex_wb_mode);
            break;
        case SPEEX_MODEID_UWB:
            state->st = speex_encoder_init(&speex_uwb_mode);
            break;
        default:
            free(state);
            return (jlong) -1;
    }
    speex_bits_init(&(state->bits));
    return (jlong) state;
}

JNIEXPORT jint JNICALL Java_za_co_monadic_scopus_speex_Speex_00024_encode_1short
    (JNIEnv *env, jobject clazz, jlong encoder, jshortArray input, jint len_in, jbyteArray coded, jint len_out) {

    encoder_state *state = (encoder_state*)encoder;
    jshort *in_ptr;
    jbyte *cod_ptr;
    jint ret;

    in_ptr = (*env)->GetPrimitiveArrayCritical(env, input, 0);
    if (in_ptr == 0) return -1;
    cod_ptr = (*env)->GetPrimitiveArrayCritical(env, coded, 0);
    if (cod_ptr == 0) {
	    (*env)->ReleasePrimitiveArrayCritical(env, input, in_ptr, 0);
	    return -1;
    }
    speex_bits_reset(&(state->bits));
    speex_encode_int(state->st,in_ptr, &(state->bits));
    ret = speex_bits_write(&(state->bits),(char *)cod_ptr,len_out);
    (*env)->ReleasePrimitiveArrayCritical(env, coded, cod_ptr, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, input, in_ptr, 0);
    return ret;
}

JNIEXPORT jint JNICALL Java_za_co_monadic_scopus_speex_Speex_00024_encode_1float
    (JNIEnv *env, jobject clazz, jlong encoder, jfloatArray input, jint len_in, jbyteArray coded, jint len_out) {
    encoder_state *state = (encoder_state*)encoder;
    jfloat *in_ptr;
    jbyte *cod_ptr;
    jint ret;

    in_ptr = (*env)->GetPrimitiveArrayCritical(env, input, 0);
    if (in_ptr == 0) return -1;
    cod_ptr = (*env)->GetPrimitiveArrayCritical(env, coded, 0);
    if (cod_ptr == 0) {
	    (*env)->ReleasePrimitiveArrayCritical(env, input, in_ptr, 0);
	    return -1;
    }
    speex_bits_reset(&(state->bits));
    speex_encode(state->st,in_ptr, &(state->bits));
    ret = speex_bits_write(&(state->bits),(char *)cod_ptr,len_out);
    (*env)->ReleasePrimitiveArrayCritical(env, coded, cod_ptr, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, input, in_ptr, 0);
    return ret;
}

JNIEXPORT void JNICALL Java_za_co_monadic_scopus_speex_Speex_00024_encoder_1destroy
    (JNIEnv *env, jobject clazz, jlong state_ptr) {
    encoder_state *state = (encoder_state*)(state_ptr);
    speex_encoder_destroy(state->st);
    speex_bits_destroy(&(state->bits));
    free(state);
}

JNIEXPORT jstring JNICALL Java_za_co_monadic_scopus_speex_Speex_00024_get_1version_1string
    (JNIEnv *env, jobject clazz) {
    const char* version;
    speex_lib_ctl(SPEEX_LIB_GET_VERSION_STRING, (void*)&version);
    return (*env)->NewStringUTF(env, version);
}


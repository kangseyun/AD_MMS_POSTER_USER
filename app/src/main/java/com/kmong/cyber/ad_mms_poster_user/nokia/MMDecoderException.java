/*
 * @(#)MMDecoderException.java	1.1
 *
 * Copyright (c) Nokia Corporation 2002
 *
 */

package com.kmong.cyber.ad_mms_poster_user.nokia;

/**
 * Thrown when an error occurs decoding a buffer representing a
 * Multimedia Message
 *
 */

public class MMDecoderException extends Exception {

  public MMDecoderException(String errormsg) {
    super(errormsg);
  }

  }



package com.alineumsoft.zenwk.security.constants;

public final class ServiceControllerConstants {
  public final static String SQL_MESSAGE_EMAIL_EXISTS =
      "duplicate key value violates unique constraint";
  public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
  public static final String HEADER_USER_AGENT = "User-Agent";
  public static final String TIME_FORMAT_SECONDS = "%.3fs";
  public static final String IP_UNKNOWN = "unknown";

  private ServiceControllerConstants() {

  }

}

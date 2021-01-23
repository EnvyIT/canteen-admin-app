package com.example.canteenchecker.infrastructure;

import android.app.Application;


public class CanteenAdminApplication extends Application {

  private String authenticationToken = null;


  public synchronized String getAuthenticationToken() {
    return authenticationToken;
  }

  public synchronized void setAuthenticationToken(String authenticationToken) {
    this.authenticationToken = authenticationToken;
  }

  public synchronized void clearAuthenticationToken() {
    this.authenticationToken = null;
  }

  public synchronized boolean isAuthenticated() {
    return  authenticationToken != null;
  }

}

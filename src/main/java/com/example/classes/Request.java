package com.example.classes;

import java.io.Serializable;

/**
*
* The class {@code Request} provides a simplified model of a request message.
*
**/
public class Request implements Serializable
{
  private static final long serialVersionUID = 1L;
  private int requestid;
  private Object data;
  private String authCode;

  //Constructor
  public Request()
  {
  }
  public int setId(final int requestidIn){
    try{
    this.requestid = requestidIn;
    }
    catch(Exception e){
      e.printStackTrace();
      return 0;
    }
    return 1;
  }
  public int setData(final Object dataIn){
    try{
    this.data = dataIn;
    }
    catch(Exception e){
      e.printStackTrace();
      return 0;
    }
    return 1;
  }
  public void setAuthCode(String authCode){
    this.authCode = authCode;
  }
  public String getAuthCode(){
    return this.authCode;
  }
  public int set(final int requestidIn, final Object dataIn, final String authCode){
    try{
      this.requestid = requestidIn;
      this.data = dataIn;
      this.authCode = authCode;
    }
    catch(Exception e){
      e.printStackTrace();
      return 0;
    }
    return 1;
  }
  public int getId(){
    return this.requestid;
  }
  public Object getData(){
    return this.data;
  }

}

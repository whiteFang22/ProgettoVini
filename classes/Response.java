package com.example.classes;
import java.io.Serializable;

/**
*
* The class {@code Response} provides a simplified model of a response message.
*
**/
public class Response implements Serializable
{
  private static final long serialVersionUID = 1L;
  private int responseid;
  private String authCode;
  private Object data;
  
  //Constructor
  public Response()
  {
  }
  
  public String getAuthCode() {
    return authCode;
  }

  public void setAuthCode(String authCode) {
    this.authCode = authCode;
  }

  public int setId(final int responseidIn){
    try{
    this.responseid = responseidIn;
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
  public int set(final int responseidIn, final Object dataIn, final String authCode){
    try{
      this.responseid = responseidIn;
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
    return this.responseid;
  }
  public Object getData(){
    return this.data;
  }

}

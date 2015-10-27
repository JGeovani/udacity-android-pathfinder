package com.udacity.pathfinder.android.udacitypathfinder.auth;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Hasher {

  private String data;

  public Md5Hasher(String input) {
   this.data=input;
  }

  public String getHash(){
    if(null == data) return null;
    String password = "MyPassword123";
    String salt = "Random$SaltValue#WithSpecialCharacters12@$@4&#%^$*LovingIT";
    String md5 = salt+data+password;

    try {

      //Create MessageDigest object for MD5
      MessageDigest digest = MessageDigest.getInstance("MD5");

      //Update input string in message digest
      digest.update(data.getBytes(), 0, data.length());

      //Converts message digest value in base 16 (hex)
      md5 = new BigInteger(1, digest.digest()).toString(16);

    } catch (NoSuchAlgorithmException e) {

      e.printStackTrace();
    }
    md5 = md5.substring(0,12);
    return md5;
  }
}

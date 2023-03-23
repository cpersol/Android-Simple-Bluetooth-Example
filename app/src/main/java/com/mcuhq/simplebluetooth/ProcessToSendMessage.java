package com.mcuhq.simplebluetooth;

import java.util.Base64;
import java.util.regex.Pattern;

public class ProcessToSendMessage {

 public static String hexToBase64(String hexString) {
  if (hexString != null && !hexString.isEmpty()) {
   byte[] hexBytes = new byte[hexString.length() / 2];
   for (int i = 0; i < hexBytes.length; i++) {
    int index = i * 2;
    int intValue = Integer.parseInt(hexString.substring(index, index + 2), 16);
    hexBytes[i] = (byte) intValue;
   }
   String base64String = android.util.Base64.encodeToString(hexBytes, android.util.Base64.DEFAULT);
   return base64String;
  }
  return null;
 }

 public static boolean isHexadecimal(String str) {
  Pattern pattern = Pattern.compile("^[0-9A-Fa-f]+$");
  return pattern.matcher(str).matches();
 }
}





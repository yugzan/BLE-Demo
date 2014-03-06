/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sonostar.bletool;

import java.util.HashMap;

import android.R.integer;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<String, String>();
    
    public static String  SONOSTAR_ALL_SETTING_R 	= "0000ffb0-0000-1000-8000-00805f9b34fb";
    public static String  SONOSTAR_CLEAR_FLASH_W 	= "0000ffb1-0000-1000-8000-00805f9b34fb";
    public static String  SONOSTAR_TIME_SETTING_W 	= "0000ffb2-0000-1000-8000-00805f9b34fb";
    public static String  SONOSTAR_DATE_SETTING_W 	= "0000ffb3-0000-1000-8000-00805f9b34fb";
    public static String  SONOSTAR_SENS_SETTING_W 	= "0000ffb4-0000-1000-8000-00805f9b34fb";
    public static String  SONOSTAR_NOTIFY_R 		= "0000ffb5-0000-1000-8000-00805f9b34fb";
    public static String  SONOSTAR_UNKNOWN_WR 		= "0000ffb6-0000-1000-8000-00805f9b34fb";
    public static String  SONOSTAR_UNKNOWN1_WR 		= "0000ffb7-0000-1000-8000-00805f9b34fb";
    
    public static String HEART_RATE_MEASUREMENT 	= "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    static {
        // Sample Services.
        //attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        //attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        //attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        //attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    	attributes.put(SONOSTAR_ALL_SETTING_R, 	"SONOSTAR_ALL_SETTING_R");
    	attributes.put(SONOSTAR_CLEAR_FLASH_W, 	"SONOSTAR_CLEAR_FLASH_W");
    	attributes.put(SONOSTAR_TIME_SETTING_W, "SONOSTAR_TIME_SETTING_W");
    	attributes.put(SONOSTAR_DATE_SETTING_W, "SONOSTAR_DATE_SETTING_W");
    	attributes.put(SONOSTAR_SENS_SETTING_W, "SONOSTAR_SENS_SETTING_W");
    	attributes.put(SONOSTAR_NOTIFY_R, 		"SONOSTAR_NOTIFY_R");
    	attributes.put(SONOSTAR_UNKNOWN_WR, 	"SONOSTAR_UNKNOWN_WR");
    	attributes.put(SONOSTAR_UNKNOWN1_WR, 	"SONOSTAR_UNKNOWN1_WR");
    	
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
    public static int lookup (String serviceuuid)
    {
    	int position = 0;
    	
    	if(serviceuuid.equals(SONOSTAR_ALL_SETTING_R))
    		position= 1;
    	
    	if(serviceuuid.equals(SONOSTAR_CLEAR_FLASH_W))
    		position=2;
    	
    	if(serviceuuid.equals(SONOSTAR_TIME_SETTING_W))
    		position=3;
    	
    	if(serviceuuid.equals(SONOSTAR_DATE_SETTING_W))
    		position=4;
    	
    	if(serviceuuid.equals(SONOSTAR_SENS_SETTING_W))
    		position=5;
    	
    	if(serviceuuid.equals(SONOSTAR_NOTIFY_R))
    		position=6;
    	
    	if(serviceuuid.equals(SONOSTAR_UNKNOWN_WR))
    		position=7;
    	
    	if(serviceuuid.equals(SONOSTAR_UNKNOWN1_WR))
    		position=8;
		return position;
    	
    	
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static String bytesToHex(byte[] bytes) {
    	
    	if (bytes != null && bytes.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(bytes.length);
            for(byte byteChar : bytes)
                stringBuilder.append(String.format("%02X ", byteChar));
            return new String(bytes) + "\n" + stringBuilder.toString();
        }
    	else {
    		
    		return "error" ;
		}
    }
}

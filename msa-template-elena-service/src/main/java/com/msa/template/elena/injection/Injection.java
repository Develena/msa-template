/*
 * Injection.java
 *
 *
 * 21. 3. 15. 오전 9:56
 *
 *
 * Copyright (c) 2021 NURIFLEX, Inc.
 * All rights reserved.
 *
 *
 * This software is the confidential and proprietary information of NURIFLEX, Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement you entered into with NURIFLEX, Inc.
 *
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */

package com.msa.template.elena.injection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class Injection {

  public static Object replaceInjection(Object params) throws Exception {

    final Pattern SpecialChars = Pattern.compile("['\"\\-#()@;=*/+]");
    final String regex = "(union|select|from|where|update|insert)";
    final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    String objectType = params.getClass().getName();
    String objectTypeLowerCase = params.getClass().getTypeName(); //.getSimpleName().toLowerCase()
    System.out.println(params.getClass().getTypeName());
    System.out.println(params.getClass().getSuperclass());
    if (objectTypeLowerCase.equals("hashmap")) {
      Map map = (Map) params;
      Object[] mKeys = map.keySet().toArray(new String[map.size()]);
      for (Object key : mKeys) {
        String subObjectTypeLowerCase = map.get(key).getClass().getSimpleName().toLowerCase();
        //if(subObjectTypeLowerCase.equals("hashmap") || subObjectTypeLowerCase.equals("arraylist"))
        System.out.println("변환중");
        System.out.println("key value" + key + " " + map.get(key));
        map.put(key, SpecialChars.matcher(map.get(key).toString()).replaceAll(""));
        map.put(key, pattern.matcher(map.get(key).toString()).replaceAll(""));
        System.out.println("key value" + key + " " + map.get(key));
      }
      return map;
    } else if (objectTypeLowerCase.equals("arraylist")) {
      ArrayList list = (ArrayList) params;

      String[] listArray = (String[]) list.toArray(new String[list.size()]);

      for (int i = 0; i < listArray.length; i++) {
        System.out.println("변환중");
        System.out.println("value " + listArray[i]);

        listArray[i] = SpecialChars.matcher(listArray[i]).replaceAll("");
        listArray[i] = pattern.matcher(listArray[i]).replaceAll("");

        System.out.println("value " + listArray[i]);
      }
      ArrayList<String> returnList = new ArrayList<>(Arrays.asList(listArray));

      return returnList;
    } else {
      Gson gson = new Gson();
      String model = gson.toJson(params);

      System.out.println(model);

      JSONObject jsonObj = null;
      try {
        JSONParser jsonparser = new JSONParser();
        Object obj = jsonparser.parse(model);
        jsonObj = (JSONObject) obj;
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
        throw new Exception(ex.getMessage());
      }

      Object returnClass = null;
      Object[] jKeys = jsonObj.keySet().toArray(new String[jsonObj.size()]);
      for (Object key : jKeys) {
        System.out.println("변환중");
        System.out.println("key value" + key + " " + jsonObj.get(key));
        jsonObj.put(key, SpecialChars.matcher(jsonObj.get(key).toString()).replaceAll(""));
        jsonObj.put(key, pattern.matcher(jsonObj.get(key).toString()).replaceAll(""));
        System.out.println("key value" + key + " " + jsonObj.get(key));
      }

      try {
        Class classType = Class.forName(objectType);

        ObjectMapper mapper = new ObjectMapper();
        returnClass = mapper.readValue(jsonObj.toJSONString(), classType);

      } catch (Exception ex) {
        System.out.println(ex.getMessage());
        throw new Exception(ex.getMessage());
      }

      System.out.println("변환완료");
      System.out.println(returnClass.toString());
      return returnClass;

    }

  }
}
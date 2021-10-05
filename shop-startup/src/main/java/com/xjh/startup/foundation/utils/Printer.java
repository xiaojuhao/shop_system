/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.utils;

import com.alibaba.fastjson.JSONArray;

/**
 * @author 36181
 */
public interface Printer {

    PrintResult print(JSONArray jSONArray, boolean isVoicce) throws Exception;

}

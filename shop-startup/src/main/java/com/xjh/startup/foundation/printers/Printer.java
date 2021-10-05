/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.startup.foundation.printers;

import java.util.concurrent.Future;

import com.alibaba.fastjson.JSONArray;

/**
 * @author 36181
 */
public interface Printer {

    Future<PrintResult> submitTask(JSONArray contentItems, boolean isVoicce);

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.service.printers;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author 36181
 */
public interface Printer {

    Future<PrintResult> submitTask(List<Object> contentItems, boolean isVoicce);

}

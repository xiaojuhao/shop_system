/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xjh.service.printers;

/**
 *
 * @author Administrator
 */
public class LocalDeskCode
{
    public static final String[] DESKNAME = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70","71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88","89","90","91","92","93","94","95","96","97","98","99","A01","A02","A03","A04","A05","A06","A07","A08","A09","A10","A11","A12","A13","A14","A15","A16","A17","B01","B02","B03","B04","B05","B06","B07","B08","B09","C01","C02","C03","C04","C05","C06","C07","C08","卡1","卡2","卡3","卡5","钟楼","天宁","武进","新北","经开","金坛","贵一","贵二","外卖1","外卖2","外卖3","外卖4","外卖5","外卖6","和风居","樱花亭","溪谷舍","富士阁","北海道","名古屋","京都苑","夕阳挂","晚云收","一川枫","美人醉","未在","三芳","绪方","采薇","1号","2号","3号","5号","6号","8号","9号","10号","11号","卡座1","卡座2","卡座3","VIP1","VIP2","府东一号","府东二号"};
    public static final String[] DESKCODE = {"HFhtL","bsdYm","tVQIq","aLNzw","mnzYw","yKIJH","vCsoj","rYwNT","WqNuB","qxeVG","FFgLg","FXQJg","vjVdD","nQcXm","nCGjs","IwOdz","DghsQ","fsjMb","ZZZHP","xRpWO","iUgNx","DjKRU","KoxEn","UzwuO","GalXp","aSBiA","ihyBG","QURTN","FEMof","HdSGk","MoKHE","Kuulp","hFifU","EGINl","pyZNg","qcpYX","rROUh","iLKPM","ngXDU","heTfl","rUyaQ","wJKLF","Ugrmv","Krbnw","fFRJg","kTGVc","fDsfc","SHyEi","FmsIG","vHZDB","WjVOt","evpyd","UqyYj","aesHS","aKACS","xAmSC","eAtwL","nhyBW","zWJrs","rQZxx","Toikn","UBUWV","PYRed","DvNkx","XLPEt","WEkhj","nPqrM","DahFO","LTUEL","QeUri","atDks","RqaFY","NlEtx","zGLSF","pTvDx","fdyfy","mEkEK","OWITf","RPvkv","vLvOC","vHMFR","iaxug","lYCKH","cOAwO","hXGJP","fJhaE","sbmCM","lulbR","glaRe","LlYes","Vpzqa","agdAA","FLrhC","ivGbG","bkRiS","awUQA","vCZhW","PJgJj","wxtCS","EnMOg","yNcHw","dM9vE","TTzbM","6z1Hn","OlpMl","lZqmB","mNMUt","wgW8I","CPIVn","kqIFG","SMmeZ","4IrVN","rCiuw","wZF04","Ervph","26kM7","uYX46","f54DI","PmevO","f6OwF","88daj","Tf4IA","bJ1TC","pY8fn","Z6ZL5","zgy77","bdJhC","8yvxX","rsstc","eYucM","Mc4bC","lzgAD","xWvua","UfQn8","FvQeu","e2HyZ","YRXim","fDi5G","tOXfY","mqFRo","vXjfp","nKigL","thOum","DsaXj","fHOOl","94Zjh","aUuxY","luM8p","Emx5N","hSqBf","SYeF7","NpVHr","5dx0P","FP5sb","SkxL3","Rh0s2","5eTxj","u7CpF","6MNxi","ewqy9","zdBb2","o2o9U","VaNGH","2sfEn","MQljE","UNxNJ","hT9Th","3nArB","E3LQD","pooQB","msTbS","X4ZzA","7HR4g","pw3ON","ht5GQ","WEg3w","bRTZn","I0HwV","6ikFW","fMvYo","2iOU6","NFWA7","6dRke"};
    
    public static String getDeskName(String deskKey)
    {
        for (int i = 0; i < DESKCODE.length; i++)
        {
            if (deskKey.equals(DESKCODE[i]))
            {
                return DESKNAME[i];
            }
        }
        return null;
    }
    
    public static String getDeskCode(String deskName)
    {
        for (int i = 0; i < DESKNAME.length; i++)
        {
            if (deskName.equals(DESKNAME[i]))
            {
                return DESKCODE[i];
            }
        }
        return null;
    }
    
}

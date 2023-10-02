package com.xjh.common.store;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.xjh.common.utils.Logger;

import java.io.File;
import java.util.List;

public class OssStore {
    String OSS_ACCCESS_KEY_ID;
    String OSS_ACCESS_KEY_SECRET;
    String OSS_ENDPOINT;
    static String bucketName = "xiaojuhao";

    public OssStore(String endpoint, String accessKeyId, String accessKeySecret){
        this.OSS_ENDPOINT = endpoint;
        this.OSS_ACCCESS_KEY_ID = accessKeyId;
        this.OSS_ACCESS_KEY_SECRET = accessKeySecret;
    }

    public void upload(File file, String objectName)  {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);

        try {
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, file);
            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
             ObjectMetadata metadata = new ObjectMetadata();
             metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
             metadata.setObjectAcl(CannedAccessControlList.Private);
             putObjectRequest.setMetadata(metadata);

            // 上传文件。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            System.out.println("上传OSS文件： "+JSONObject.toJSONString(result));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    public boolean exists(String objectName){
        OSS ossClient = new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
        boolean exists = ossClient.doesObjectExist(bucketName, objectName);
        ossClient.shutdown();
        return exists;
    }

    public boolean download(String objectName, File to){
        OSS ossClient = new OSSClientBuilder().build(OSS_ENDPOINT, OSS_ACCCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
        if(!exists(objectName)){
            Logger.info("OSS中不存在资源: " + objectName);
            return false;
        }
        if(!to.getParentFile().exists()){
            to.getParentFile().mkdirs();
        }
        GetObjectRequest getReq = new GetObjectRequest(bucketName, objectName);
        ossClient.getObject(getReq, to);
        ossClient.shutdown();
        System.out.println("下载OSS文件: " + objectName + " >> " + to.getAbsolutePath());
        return true;
    }

    public void listFiles(File f, List<File> files){
        if(f == null){
            return;
        }
        if(f.isFile()){
            files.add(f);
        }else {
            File[] fs = f.listFiles();
            if(fs == null || fs.length == 0){
                return;
            }
            for(File ff : fs){
                listFiles(ff, files);
            }
        }
    }

//    public static void main(String[] args) throws Exception {
//        OssStore ossStore = new OssStore();
//        List<File> files = new ArrayList<>();
//        File dir = new File("D:/shop_system_data/images/");
//        ossStore.listFiles(dir, files);
//
//        for(File f : files){
//            String objectName = f.getAbsolutePath().replaceAll("\\\\", "/")
//                    .replaceAll("D:/shop_system_data/","");
//            System.out.println(objectName);
//            ossStore.upload(f, objectName);
//        }
//    }
}
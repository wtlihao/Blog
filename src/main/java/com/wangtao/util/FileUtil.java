package com.wangtao.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * @author: zhangocean
 * @Date: 2018/7/21 11:29
 * Describe: 文件工具
 */
public class FileUtil {

    /**
     * 上传文件到腾讯云OSS
     * @param file 文件流
     * @return 返回文件URL
    */
    public String uploadFile(File file, String subCatalog){

        //初始化OSSClient
        COSClient cosClient = getCosClient();
        // 指定存储桶
        String bucketName = "myblog-1257039620";
        // 生成key
        String key = subCatalog+"/"+file.getName();
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,key,file);
        // 上传
        cosClient.putObject(putObjectRequest);
        String picUrl = "https://" + bucketName + "." + "cos.ap-chengdou.myqcloud.com"+ "/" + key;

        //删除临时生成的文件
        File deleteFile = new File(file.toURI());
        deleteFile.delete();

        return picUrl;

    }

    private COSClient getCosClient(){
        // 1 初始化用户身份信息（secretId, secretKey）。
        String secretId = "AKIDf9RYUy0p6B5oeJSCTdcOzKl6sTwJmO9k";
        String secretKey = "azIQSq3G3X8iCYjiHcqwuhJmFdu0udhy";
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的区域, COS 地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig 中包含了设置 region, https(默认 http), 超时, 代理等 set 方法, 使用可参见源码或者常见问题 Java SDK 部分。
        Region region = new Region("ap-chengdu");
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }

    /**
     * base64字符转换成file
     *  @param destPath 保存的文件路径
     * @param base64 图片字符串
     * @param fileName 保存的文件名
     * @return file
     */
    public File base64ToFile(String destPath,String base64, String fileName) {
        File file = null;
        //创建文件目录
        String filePath=destPath;
        File  dir=new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            file=new File(filePath+"/"+fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 将file转换成base64字符串
     * @param path
     * @return
     */
    public String fileToBase64(String path) {
        String base64 = null;
        InputStream in = null;
        try {
            File file = new File(path);
            in = new FileInputStream(file);
            byte[] bytes=new byte[(int)file.length()];
            in.read(bytes);
            base64 = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return base64;
    }

    /**
     * MultipartFile类型文件转File
     * @return File类型文件
     */
    public File multipartFileToFile(MultipartFile multipartFile, String filePath, String fileName){
        File f = null;
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        if("".equals(multipartFile) || multipartFile.getSize() <= 0){
            multipartFile = null;
        } else {
            try {
                InputStream ins = multipartFile.getInputStream();
                f = new File(filePath + fileName);
                OutputStream os = new FileOutputStream(f);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = ins.read(buffer, 0, 8192)) != -1){
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return f;
    }

}

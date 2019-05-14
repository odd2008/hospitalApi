package com.hospital.tools;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadHelper {

    public static Map<String, Object> uploadPicture(MultipartFile file, MessageProperties config, String telephone, String dirType) throws Exception {
        try {
            Map<String, Object> resultMap = new HashMap<>();
            String[] IMAGE_TYPE = config.getImageType().split(",");
            String path = null;
            boolean flag = false;
            for (String type : IMAGE_TYPE) {
                if (StringUtils.endsWithIgnoreCase(file.getOriginalFilename(), type)) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                // 获得文件类型
                String fileType = file.getContentType();
                // 获得文件后缀名称
                String imageName = fileType.substring(fileType.indexOf("/") + 1);
                // 原名称
                String oldFileName = file.getOriginalFilename();
                // 新名称
                String newFileName = uuid + "." + imageName;
                // 年月日文件夹
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                String basedir = sdf.format(new Date());
                // 进行压缩(大于4M)
                if (file.getSize() > config.getFileSize()) {
                    // 重新生成
                    String newUUID = UUID.randomUUID().toString().replaceAll("-", "");
                    String finalPath = "/" + telephone + "/" + dirType + "/" + basedir + "/" + newUUID + "." + imageName;

                    path = config.getUpPath() + finalPath;
                    // 如果目录不存在则创建目录
                    File oldFile = new File(path);
                    if (!oldFile.exists()) {
                        oldFile.mkdirs();
                    }
                    file.transferTo(oldFile);
                    // 压缩图片
                    Thumbnails.of(oldFile).scale(config.getScaleRatio()).toFile(path);
                    // 显示路径
                    resultMap.put("status", "success"); // success error
                    resultMap.put("errMsg", "");
                    resultMap.put("data", finalPath);
                } else {
                    String finalPath = "/" + telephone + "/" + dirType + "/" + basedir + "/" + uuid + "." + imageName;
                    path = config.getUpPath() + finalPath;
                    // 如果目录不存在则创建目录
                    File uploadFile = new File(path);
                    if (!uploadFile.exists()) {
                        uploadFile.mkdirs();
                    }
                    file.transferTo(uploadFile);
                    resultMap.put("status", "success"); // success error
                    resultMap.put("errMsg", "");
                    resultMap.put("data", finalPath);
                }
            } else {
                resultMap.put("status", "error"); // success error
                resultMap.put("errMsg", "图片格式不正确,支持png|jpg|jpeg");
                resultMap.put("data", "");
                return resultMap;
            }
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}

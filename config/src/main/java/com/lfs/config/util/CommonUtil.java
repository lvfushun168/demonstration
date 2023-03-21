package com.lfs.config.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 通用工具类
 */
public class CommonUtil {

    /**
     * MultipartFile转file工具类
     * @param multipartFile
     * @return
     */
    public static File transferToFile(MultipartFile multipartFile) {
//        选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");
            file=File.createTempFile(filename[0], filename[1] + ".");
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}

package site.pushy.fastdfs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.pushy.fastdfs.pojo.FastDFSFile;
import site.pushy.fastdfs.util.FastDFSUtil;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class FileController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return "文件为空";
        }
        String path = saveFile(file);
        return path;
    }

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        logger.info("【fileName】" + fileName + "【ext】" + ext);
        byte[] file_buff = null;
        try (InputStream inputStream = multipartFile.getInputStream();) {
            if (inputStream != null) {
                int len1 = inputStream.available();
                file_buff = new byte[len1];
                inputStream.read(file_buff);
            }
            FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
            file.setAuthor("Pushy");
            try {
                fileAbsolutePath = FastDFSUtil.upload(file);
            } catch (Exception e) {
                e.printStackTrace();
                return "上传错误";
            }

            if (fileAbsolutePath == null) {
                logger.error("fileAbsolutePath == null");
                return "上传错误";
            }
            return "ok";
        } catch (IOException e) {
            e.printStackTrace();
            return "读取inputStream错误";
        }

    }

}

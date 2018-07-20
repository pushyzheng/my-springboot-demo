package site.pushy.fastdfs.util;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.csource.common.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.pushy.fastdfs.pojo.FastDFSFile;

import java.io.IOException;

public class FastDFSUtil {

    private static Logger logger = LoggerFactory.getLogger(FastDFSUtil.class);

    private static TrackerClient trackerClient;
    private static TrackerServer trackerServer;
    private static StorageServer storageServer;
    private static StorageClient storageClient;
    StorageClient1 storageClient1;

    static {
        try {
            // 初始化配置文件
            ClientGlobal.initByProperties("fastdfs-client.properties");

            trackerClient = new TrackerClient();
            trackerServer = trackerClient.getConnection();
            storageServer = null;
            storageClient = new StorageClient(trackerServer, storageServer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    public static String[] upload(FastDFSFile file) {
        logger.info("File Name: " + file.getName() + "File Length:" + file.getContent().length);
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", file.getAuthor());

        long startTime = System.currentTimeMillis();
        String[] uploadResults = null;
        try {
            System.out.println(file.getContent());
            uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IO Exception when uploadind the file:" + file.getName(), e);
        } catch (MyException e) {
            e.printStackTrace();
            logger.error("Non IO Exception when uploadind the file:" + file.getName(), e);
        }

        System.out.println(uploadResults);
        String groupName = uploadResults[0];
        String remoteFileName = uploadResults[1];
        logger.info("upload file successfully!!!" + "group_name:" + groupName + ", remoteFileName:" + " " + remoteFileName);

        return uploadResults;
    }

}

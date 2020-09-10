package net.crow.moive.transfer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import net.crow.moive.transfer.utls.PropertyUtil;

import static net.crow.moive.transfer.constants.ApplicationConstants.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class VideoStreamService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ResponseEntity<byte[]> prepareContent(String fileName, String fileType, String range){
		
		long rangeStart = 0;
		long rangeEnd;
		byte[] data;
		Long fileSize;
		String fullFileName = fileName + "." + fileType;
		StringBuilder sb = new StringBuilder();
		sb.append("\n-----------------------------------------------------\n");
		sb.append("客户端传入的文件获取区间：").append(range).append("\n");
		sb.append("客户端传入的文件名：").append(fullFileName).append("\n");
		try {
			fileSize = getFileSize(fullFileName);
			sb.append("文件尺寸：").append(fileSize).append("\n");
			if (range == null || range.trim().length() == 0){
				sb.append("客户端未传递任何文件读取区间信息\n");
				sb.append("-----------------------------------------------------");
				logger.info(sb.toString());
				return ResponseEntity.status(HttpStatus.OK)
						.header(CONTENT_TYPE, VIDEO_CONTENT+fileType)
						.header(CONTENT_LENGTH, String.valueOf(fileSize))
						.body(
							// Read the object and convert it as bytes
							readByteRange(fullFileName, rangeStart, fileSize - 1)
						);
			}
			String[] ranges = range.split("-");
			rangeStart = Long.parseLong(ranges[0].substring(6));
			sb.append("服务端文件读取起始位置：").append(rangeStart).append("\n");
			if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
			if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
			sb.append("服务端文件读取终止位置：").append(rangeEnd).append("\n");
			sb.append("-----------------------------------------------------");
			logger.info(sb.toString());
			data = readByteRange(fullFileName, rangeStart, rangeEnd);
		} catch(Exception e){
			logger.error("Exception while reading the file {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		String contentLength = String.valueOf((rangeEnd-rangeStart+1));
		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(CONTENT_TYPE, VIDEO_CONTENT + fileType)
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, contentLength)
                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .body(data);
	}

	public byte[] readByteRange(String fullFileName, long start, long end) throws IOException {
		Path path = Paths.get(getFilePath(), fullFileName);
        try (InputStream inputStream = (Files.newInputStream(path));
             ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[BYTE_RANGE];
            int nRead;
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), (int) start, result, 0, result.length);
            return result;
        }
	}

	public Long getFileSize(String fullFileName) {
		Long result = Optional.ofNullable(fullFileName)
                .map(file -> Paths.get(getFilePath(), file))
                .map(this::sizeFromFile)
                .orElse(0L);
		return result;
	}
	
	private String getFilePath(){
		//打包前后获取到的路径不一致
		//URL url = this.getClass().getResource(VIDEO);
        //String filePath = new File(url.getFile()).getAbsolutePath();
		String dir = System.getProperty("user.dir");
		//dir = dir.substring(0,dir.lastIndexOf(File.separator));
		String filePath = null;
		try {
			filePath = dir + File.separator + PropertyUtil.instance()
					.getValueByDefaultFileKey(dir,CONFIG_COMMON_FILE, VIDEO_PATH_CONFIG);
		} catch (IOException e) {
			logger.error("Error while getting the file path", e);
			e.printStackTrace();
		}
        return filePath;
	}
	
	private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ioException) {
            logger.error("Error while getting the file size", ioException);
        }
        return 0L;
    }
	
//	public static void main(String[] args){
//		
//		String s = "D:\\eclipse_springboot\\crow-movie\\crow-moive-transfer\\target\\file:\\D:\\eclipse_springboot\\crow-movie\\crow-moive-transfer\\target\\crow-moive-transfer-1.0-snapshot.jar!\\BOOT-INF\\classes!\\video\\toystory.mp4";
//		int index = s.indexOf("file:");
//		System.out.println(s.substring(index));
//	}
}

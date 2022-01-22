package com.numble.numbledanggeun.file;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
@Component
public class FileStore {

    private String uploadFolder = System.getProperty("user.home");


    public String getFullPath(String folderPath, String storeFileName){

        return uploadFolder + File.separator + folderPath + File.separator + storeFileName;

    }


    public List<ResultFileStore> storeFiles(List<MultipartFile> multipartFiles) throws IOException {

        List<ResultFileStore> storeFileResult = new ArrayList<>();

        if (!multipartFiles.isEmpty() && multipartFiles.size() > 0) {
            for (MultipartFile multipartFile : multipartFiles) {

                storeFileResult.add(storeFile(multipartFile));
            }
        }

        return storeFileResult;
    }



    public ResultFileStore storeFile(MultipartFile multipartFile) throws IOException {

        if(multipartFile.isEmpty() || multipartFile == null){
            return null;
        }

        //파일 이름
        String originalFilename = multipartFile.getOriginalFilename();

        //파일 저장 이름
        String storeFileName = createStoreFileName(originalFilename);

        //폴더 생성
        String folderPath = makeFolder();

        //이미지 저장
        multipartFile.transferTo(new File(getFullPath(folderPath, storeFileName)));

        return new ResultFileStore(folderPath, storeFileName);
    }

    private String createStoreFileName(String originalFileName) {

        String uuid = UUID.randomUUID().toString();

        return uuid + "_" + originalFileName;


    }

    private String makeFolder() {

        String folderPath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyy/MM/dd"));

        File uploadPathFolder = new File(uploadFolder, folderPath);

        if(uploadPathFolder.exists() == false){
            uploadPathFolder.mkdirs();
        }

        return folderPath;

    }
}

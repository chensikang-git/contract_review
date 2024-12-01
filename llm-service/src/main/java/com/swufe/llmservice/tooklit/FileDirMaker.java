package com.swufe.llmservice.tooklit;

import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.swufe.llmservice.common.contant.llmConstant.*;


@Component
@RequiredArgsConstructor
public class FileDirMaker implements CommandLineRunner {


    private final ServletContext servletContext;

    private static final String IMG_DIR_PATH = "LawFile" + File.separator + "HeadImg";
    private static final String DOCX_DIR_PATH = "LawFile" + File.separator + "Upload";
    private static final String DOCX_EXECUTED_DIR_PATH = "LawFile" + File.separator + "Executed";

    @Override
    public void run(String... args) throws Exception {
        String currentDirPath = System.getProperty("user.dir");
        HEAD_IMG_DIR_PATH = currentDirPath + File.separator + IMG_DIR_PATH;
        DOCX_FILE_DIR_PATH = currentDirPath + File.separator + DOCX_DIR_PATH;
        EXECUTED_DOCX_FILE_DIR_PATH = currentDirPath + File.separator + DOCX_EXECUTED_DIR_PATH;

        mkdirOfFilePath(HEAD_IMG_DIR_PATH);
        mkdirOfFilePath(DOCX_FILE_DIR_PATH);
        mkdirOfFilePath(EXECUTED_DOCX_FILE_DIR_PATH);
    }

    private void mkdirOfFilePath(String absolutelyDirPath) {
        File realDir = new File(absolutelyDirPath);
        if (!realDir.exists()) {
            System.out.println(absolutelyDirPath);
            realDir.mkdirs();
        }
    }

}

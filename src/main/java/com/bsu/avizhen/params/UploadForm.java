package com.bsu.avizhen.params;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Александр on 13.04.2017.
 */
public class UploadForm {

    MultipartFile[] verifiableFiles;

    MultipartFile[] uniqueFiles;

    public MultipartFile[] getVerifiableFiles() {
        return verifiableFiles;
    }

    public void setVerifiableFiles(MultipartFile[] verifiableFiles) {
        this.verifiableFiles = verifiableFiles;
    }

    public MultipartFile[] getUniqueFiles() {
        return uniqueFiles;
    }

    public void setUniqueFiles(MultipartFile[] uniqueFiles) {
        this.uniqueFiles = uniqueFiles;
    }

    public boolean isFileUploaded() {
        return verifiableFiles.length > 1 && uniqueFiles.length > 1;
    }
}

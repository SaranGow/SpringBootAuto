package com.codezee.app.DataHolder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Scope("cucumber-glue")
public class DataHolder {

    private File folderPathForDownload;
    private String fileName;

    public File getFolderPathForDownload() {
        return folderPathForDownload;
    }

    public void setFolderPathForDownload(File folderPathForDownload) {
        this.folderPathForDownload = folderPathForDownload;
    }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


}

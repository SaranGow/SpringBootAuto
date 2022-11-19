package com.netzero.app.DataHolder;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Scope("cucumber-glue")
public class DataHolder {

    private File folderPathForDownload;

    public File getFolderPathForDownload() {
        return folderPathForDownload;
    }

    public void setFolderPathForDownload(File folderPathForDownload) {
        this.folderPathForDownload = folderPathForDownload;
    }


}

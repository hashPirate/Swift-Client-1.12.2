package me.pignol.swift.client.managers.config;

import me.pignol.swift.api.util.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager
{

    private static final FileManager INSTANCE = new FileManager();

    private FileManager() { /* This is a singleton. */ }

    private Path mainPath;
    private Path spammerPath;

    public static FileManager getInstance()
    {
        return INSTANCE;
    }

    public void init()
    {
        mainPath = FileUtil.getDirectory(Paths.get(""), "Swift");
        FileUtil.getDirectory(mainPath, "Modules");
        spammerPath = FileUtil.getDirectory(mainPath, "Spammers");
    }

    public Path getMainPath() {
        return mainPath;
    }

    public Path getSpammerPath()
    {
        return spammerPath;
    }

}

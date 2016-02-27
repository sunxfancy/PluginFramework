package com.plugin;

import com.plugin.searcher.FilePathSearcher;


import static com.plugin.framework.FrameWork.StartFrameWork;


public class Main {

    public static void main(String[] args) {

        FilePathSearcher filesearcher = new FilePathSearcher(".",true);
        StartFrameWork(filesearcher);
    }
}

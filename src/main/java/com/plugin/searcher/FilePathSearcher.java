package com.plugin.searcher;

import com.plugin.framework.Plugin;
import com.plugin.framework.PluginLoader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 文件路径插件搜索器
 * Created by sxf on 14-11-22.
 */
public class FilePathSearcher implements SearcherInterface {

    /**
     * 寻找一个文件路径下的所有jar包作为插件尝试加载
     *
     * @param path 文件路径
     */
    public FilePathSearcher(String path) {
        this.paths = new String[]{path};
    }

    /**
     * 寻找文件路径下的所有jar包作为插件尝试加载
     *
     * @param paths 文件路径
     */
    public FilePathSearcher(String[] paths) {
        this.paths = paths;
    }

    /**
     * 寻找一个文件路径下的所有jar包作为插件尝试加载
     *
     * @param path      文件路径
     * @param recursion 是否递归查找
     */
    public FilePathSearcher(String path, boolean recursion) {
        this.paths = new String[]{path};
        this.recursion = recursion;
    }

    /**
     * 寻找文件路径下的所有jar包作为插件尝试加载
     * @param paths     文件路径
     * @param recursion 是否递归查找
     */
    public FilePathSearcher(String[] paths, boolean recursion) {
        this.paths = paths;
        this.recursion = recursion;
    }

    String[] paths = null;
    boolean recursion = false;
    PluginLoader pLoader = null;

    @Override
    /**
     * 搜索插件的方法
     * @param map 用来存储结果的容器
     */
    public void SearchPlugin(Map<String, Plugin> map) {
        if (this.paths == null) return;
        pLoader = new PluginLoader(map);
        for (String path : paths) {
            File f = new File(path);
            findAllJAR(f);
        }
    }

    /**
     * 寻找路径下的全部jar包并加载
     *
     * @param dir 搜索路径
     */
    private void findAllJAR(File dir) {
        File[] fm = dir.listFiles(getFilefilter());
        if (fm == null) return;
        for (File file : fm) {
            if (isRecursion() && file.isDirectory()) {
                findAllJAR(file);
            }
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
                loadPlugin(file);
            }
        }
    }

    /**
     * 加载插件的方法,通过文件对象加载jar包
     * @param f 给定文件对象
     */
    protected void loadPlugin(File f) {
        try {
            InputStream inputStream = null, dependenceStream = null;
            Class<?> pClass = null;

            JarFile jarFile = new JarFile(f);
            URL url = new URL("file:" + f.getAbsolutePath());
            ClassLoader loader = new URLClassLoader(new URL[]{url});

            Enumeration<JarEntry> es = jarFile.entries();
            while (es.hasMoreElements()) {
                JarEntry jarEntry = es.nextElement();
                String name = jarEntry.getName();
                if (name != null && name.equals("plugin.properties")) {
                    inputStream = jarFile.getInputStream(jarEntry);
                }
                if (name != null && name.equals("dependence.properties")) {
                    dependenceStream = jarFile.getInputStream(jarEntry);
                }
            }
            Plugin p = pLoader.loadPlugin(inputStream, dependenceStream);
            pClass = loader.loadClass(p.getPlugin_class_name());
            assert pClass != null;
            p.MakeInstance(pClass);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * jar文件搜索过滤器
     */
    protected FileFilter getFilefilter(){
        return f;
    }

    FileFilter f = new FileFilter() {
        public boolean accept (File file){
            return file.isDirectory() || file.getName().endsWith(".jar");
        }
    };

    public String[] getPaths() {
        return paths;
    }

    public void setPaths(String[] path) {
        this.paths = path;
    }

    public boolean isRecursion() {
        return recursion;
    }

    public void setRecursion(boolean recursion) {
        this.recursion = recursion;
    }
}

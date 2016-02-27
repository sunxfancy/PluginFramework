package com.plugin.framework;

import com.plugin.searcher.FilePathSearcher;
import com.plugin.searcher.SearcherInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by sxf on 14-11-22.
 */
public class FrameWork {

    //========== 框架静态方法 ==========

    /**
     * 启动框架,核心方法
     */
    public static void StartFrameWork(SearcherInterface searcher){
        instance = new FrameWork();
        instance.Start(searcher);
    }

    /**
     * 启动框架,简便使用的核心方法
     */
    public static void StartFrameWork(String path){
        instance = new FrameWork();
        instance.Start(path);
    }

    /**
     * 获取当前框架的实例
     * @return 框架的实例对象
     */
    public static FrameWork getInstance(){
        return instance;
    }

    /**
     * 框架的实例
     */
    private static FrameWork instance;

    //=========== 类成员方法 ===========

    /**
     * 构造函数,创建plugin容器
     */
    public FrameWork(){
        PluginInstances = new HashMap<String, Plugin>();
    }


    /**
     * 启动框架,核心方法
     */
    public void Start(SearcherInterface searcher){
        FindPlugin(searcher);
        Initialize();
        Main();
    }

    /**
     * 启动框架,简便使用的核心方法
     */
    public void Start(String path){
        System.out.print("ok");
        SearcherInterface searcher = new FilePathSearcher(path,true);
        System.out.print("Find Begin");

        FindPlugin(searcher);
        System.out.print("Find Plugins");
        Initialize();
        System.out.print("Init Plugins");
        Main();
    }


    /**
     * 异步启动框架
     */
    public void StartAsyn(SearcherInterface searcher){
        Thread t = new Thread(() -> {
            this.Start(searcher);
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * 异步启动框架
     */
    public void StartAsyn(String path){
        Thread t = new Thread(() -> {
            this.Start(path);
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * 寻找插件,支持多种加载器
     * @return 异常码,正常为0
     */
    public int FindPlugin(SearcherInterface seacher) {
        try {
                seacher.SearchPlugin(getPluginInstances());
            return 0;
        }catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 初始化方法,初始化所有插件
     * @return 返回异常码,正常为0
     */
    public int Initialize() {
        try {
            for (Map.Entry<String,Plugin> plugin : getPluginInstances().entrySet()){
                List<Map.Entry<String, Integer>> lists = plugin.getValue().getPlugin_dependence();
                Plugin[] di = new Plugin[lists.size()];
                int p = 0;
                for (Map.Entry<String, Integer> dependences :lists)
                {
                    String name = dependences.getKey();
                    Plugin dependence = (Plugin)getPluginInstances().get(name);
                    di[p] = dependence; p++;
                }
                PluginInterface plugin_interface = plugin.getValue().getPlugin_interface();
                plugin_interface.setDI(di);
            }

            for (Map.Entry<String,Plugin> plugin : getPluginInstances().entrySet()){
                PluginInterface plugin_interface = plugin.getValue().getPlugin_interface();
                plugin_interface.Initialize();
            }
            return 0;
        }catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 启动各插件主线程,如果插件启动了独立选项,则为其设置独立的后台线程进行运转
     * @return 结束时返回异常码,正常为0
     */
    public int Main() {
        try{
            for (Map.Entry<String,Plugin> plugin : getPluginInstances().entrySet()){
                plugin.getValue().Start();
            }
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 所有插件的实例，用插件的名称作为索引
     * @return 所有实例的HashMap列表
     */
    public Map<String, Plugin> getPluginInstances() {
        return PluginInstances;
    }


    public Plugin FindPlugin(String name) {
        return PluginInstances.get(name);
    }


    /**
     * 所有插件的实例，用插件的名称作为索引
     */
    private Map<String, Plugin> PluginInstances;
}


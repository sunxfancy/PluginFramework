package com.plugin.framework;

import javafx.util.Pair;

import javax.swing.text.html.parser.Entity;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 工作线程启动类,负责启动各个插件的主函数
 * Created by sxf on 14-11-23.
 */
public class Plugin implements Runnable,PluginInterface {
    public Plugin() {

    }
    public Plugin(PluginInterface plugin) {
        this.plugin_interface = plugin;
    }

    /**
     * 利用反射的类对象创建接口实例
     * @param plugin_interface 类对象
     */
    public void MakeInstance(Class<?> plugin_interface){
        if (plugin_interface == null) throw new NullPointerException();
        PluginInterface p = null;
        try {
            p = (PluginInterface) plugin_interface.getConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (p == null) return;
        this.setPlugin_interface(p);
    }

    public void Start(){
        if (getPlugin_interface().isIndependent()) {
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }
    }

    /**
     * 获得插件的接口对象
     * @return 接口对象
     */
    public PluginInterface getPlugin_interface() {
        return plugin_interface;
    }

    /**
     * 设置插件的接口对象
     * @param plugin_interface 接口对象
     */
    public void setPlugin_interface(PluginInterface plugin_interface) {
        this.plugin_interface = plugin_interface;
    }

    /**
     * 设置插件的依赖项
     * @param name 依赖项的名字
     * @param version 依赖项的版本
     */
    public void setDependence(String name,Integer version){
        plugin_dependence.add(new AbstractMap.SimpleEntry<String, Integer>(name, version));
    }

    public void setDependence(String name,String version){
        plugin_dependence.add(new AbstractMap.SimpleEntry<String, Integer>(name, (int) ipToLong(version)) {
        });
    }

    /**
     * 测试插件的依赖
     * @param name 被测的名字
     * @return 最低要求的版本号,0为无依赖
     */
    public int getDependence(String name){
        for (Map.Entry<String,Integer> plugins : plugin_dependence) {
            if (plugins.getKey().equals(name)) return plugins.getValue();
        }
        return 0;
    }

    /**
     * 获得依赖项的列表
     * @return 依赖项Map列表
     */
    public List<Map.Entry<String, Integer>> getPlugin_dependence() {
        return plugin_dependence;
    }

    /**
     * 设置依赖项列表
     * @param plugin_dependence 依赖项列表
     */
    public void setPlugin_dependence(List<Map.Entry<String, Integer>> plugin_dependence) {
        this.plugin_dependence = plugin_dependence;
    }

    /**
     * 获得字符串表示的版本号 XXX.XXX.XXX.XXX 分别为0-255
     * @return 版本字符串
     */
    public String getPlugin_version_by_string() {
        return longToIP(plugin_version);
    }

    /**
     * 设置字符串表示的版本号 XXX.XXX.XXX.XXX 分别为0-255
     * @param plugin_version 版本字符串
     */
    public void setPlugin_version_by_string(String plugin_version) {
        this.plugin_version = ipToLong(plugin_version);
    }

    /**
     * 获取32位版本号
     * @return 版本号
     */
    public long getPlugin_version() {
        return plugin_version;
    }

    /**
     * 设置32位版本号
     * @param plugin_version 版本号
     */
    public void setPlugin_version(int plugin_version) {
        this.plugin_version = plugin_version;
    }

    /**
     * 获取插件名
     * @return 插件名
     */
    public String getPlugin_name() {
        return plugin_name;
    }

    /**
     * 设置插件名
     * @param plugin_name 插件名
     */
    public void setPlugin_name(String plugin_name) {
        this.plugin_name = plugin_name;
    }

    /**
     * 获取插件的类接口名
     * @return 接口类的名字
     */
    public String getPlugin_class_name() {
        return plugin_class_name;
    }

    /**
     * 设置接口类的名字
     * @param plugin_class_name 接口类名
     */
    public void setPlugin_class_name(String plugin_class_name) {
        this.plugin_class_name = plugin_class_name;
    }


    /**
     * 插件名
     */
    private String plugin_name;

    /**
     * 插件版本号
     */
    private long plugin_version;


    /**
     * 插件的接口类名
     */

    private String plugin_class_name;

    /**
     * 插件的依赖项
     */
    private List<Map.Entry<String,Integer>> plugin_dependence = new LinkedList<Map.Entry<String, Integer>>();

    /**
     * 插件的接口对象
     */
    private PluginInterface plugin_interface;

    /**
     * 插件自主运行的主方法,开启新线程运转
     */
    @Override
    public void run() {
        plugin_interface.Main();
    }

    public static long ipToLong(String strIp) {
        long[] ip = new long[4];
        // 先找到IP地址字符串中.的位置
        int position1 = strIp.indexOf(".");
        int position2 = strIp.indexOf(".", position1 + 1);
        int position3 = strIp.indexOf(".", position2 + 1);
        // 将每个.之间的字符串转换成整型
        ip[0] = Long.parseLong(strIp.substring(0, position1));
        ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(strIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    // 将十进制整数形式转换成127.0.0.1形式的ip地址
    public static String longToIP(long longIp) {
        StringBuilder sb = new StringBuilder("");
        // 直接右移24位
        sb.append(String.valueOf((longIp >>> 24)));
        sb.append(".");
        // 将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
        sb.append(".");
        // 将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
        sb.append(".");
        // 将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)));
        return sb.toString();
    }

    // ======== 便于使用的接口,调用方式和plugin_interface一样 ========

    @Override
    public void setDI(Plugin[] plugins) {
        plugin_interface.setDI(plugins);
    }

    @Override
    public String[] getInterfaces() {
        return plugin_interface.getInterfaces();
    }

    @Override
    public Object Factory(String class_name, Object... args) {
        return plugin_interface.Factory(class_name,args);
    }

    @Override
    public boolean isIndependent() {
        return plugin_interface.isIndependent();
    }

    @Override
    public int Initialize() {
        return plugin_interface.Initialize();
    }

    @Override
    public int Main() {
        return plugin_interface.Main();
    }
}
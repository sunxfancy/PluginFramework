package com.plugin.framework;

/**
 *
 * Created by sxf on 14-11-22.
 */
public interface PluginInterface {

    /**
     * 将所有你需要的依赖项注入进来
     * @param plugins
     */
    public void setDI(Plugin[] plugins);


    /**
     * 获取插件的接口对象，需要强制转换成插件对应的接口
     * @return 接口类对象的名字
     */
    public String[] getInterfaces();

    /**
     * 接口构造器,用于构造新的接口对象,也同事用于获取插件持有的实例化对象使用
     * @param class_name 实例化类名
     * @param args 参数表
     * @return 接口的实例化对象
     */
    public Object Factory(String class_name, Object... args);

    /**
     * 判断当前插件是否时拥有独立线程
     * @return 拥有独立线程的插件返回true，否则返回false
     */
    public boolean isIndependent();

    /**
     * 插件的初始化方法，会在整个插件系统初始化时被调用
     * @return 错误码，正常为0
     */
    public int Initialize();

    /**
     * 插件的主方法，拥有独立线程的插件才会被调用
     * @return 错误码，正常为0
     */
    public int Main();
}

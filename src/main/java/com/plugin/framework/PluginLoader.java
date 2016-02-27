package com.plugin.framework;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

/**
 * 标准插件规范加载器,重写此类会修改插件的加载规范
 * Created by sxf on 14-11-23.
 */
@SuppressWarnings("ALL")
public class PluginLoader {

    public PluginLoader(Map<String, Plugin> plugin_map) {
        this.plugin_map = plugin_map;
    }

    private Map<String, Plugin> plugin_map;

    /**
     * 插件的load方法
     * @param prop
     * @param plugin_interface
     */
    public Plugin loadPlugin(InputStream prop,InputStream dependence) {
        Plugin plugin = new Plugin();
        Properties properties = new Properties();
        String name = null,plugin_class = null;
        String version = null;
        try {
            properties.load(prop);
            name = properties.getProperty("name");
            version = properties.getProperty("version");
            plugin_class = properties.getProperty("plugin_class");
            properties.clear();
            properties.load(dependence);

            for (Map.Entry set : properties.entrySet()){
                plugin.setDependence((String)set.getKey(),(String)set.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin.setPlugin_name(name);
        plugin.setPlugin_version_by_string(version);
        plugin.setPlugin_class_name(plugin_class);
        plugin_map.put(name,plugin);
        return plugin;
    }
}

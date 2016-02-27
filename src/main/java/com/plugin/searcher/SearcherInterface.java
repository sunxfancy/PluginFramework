package com.plugin.searcher;

import com.plugin.framework.Plugin;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * 插件搜索器接口
 * Created by sxf on 14-11-22.
 */
public interface SearcherInterface {

    public void SearchPlugin(Map<String, Plugin> map) throws MalformedURLException;

}

package com.lfs.config.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface CacheMapUtil {

    Map<String,String> cacheMap = new ConcurrentHashMap<>();

    List<String> requestKey = new ArrayList<>();

    Map<String,String> cut = new ConcurrentHashMap<>();

}

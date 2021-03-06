package com.wcs.cache;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class CacheManagerService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(CacheManagerService.class.getName());


	@EJB(beanName = "FileCacheService")
	private CacheInterface cacheService;
	
	@PostConstruct
	private void init() {
		logger.info("CacheManagerService.init()");
	}

	public void clear() {
		cacheService.clear();
	}

	public Cache get(String key) {
		return cacheService.getCache(key);
	}

	public void set(String key, Cache obj) {
		cacheService.putCache(key, obj);
	}

	public void remove(String key) {
		cacheService.removeCache(key);
	}

	public void bulkRemove(String prefix) {
		cacheService.bulkRemove(prefix);
	}

}

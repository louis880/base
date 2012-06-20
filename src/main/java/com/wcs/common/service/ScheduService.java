package com.wcs.common.service;

import com.wcs.common.util.ConfigManager;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Map;

/**
 * <p>Project: BTC</p>
 * <p>Description: 同步任务调度</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:hujianguang@wcs-global.com">胡建光</a>
 */

@Startup
@Stateless
public class ScheduService implements Serializable {

    private static final long serialVersionUID = -4531023608569097125L;

    private static final String URL_PRE = "url_";

    @PersistenceContext
    public EntityManager em;

    @EJB
    public SyncJsonService syncService;

    @PostConstruct
    public void init() {
        this.SyncTask();
    }

    /**
     * <p>Description: 执行同步任务</p>
     */
    @Schedule(minute = "30", hour = "*")
    public void SyncTask() {

        //获取请求资源地址和表名
        Map<String, String> uriMap = ConfigManager.getConfigValueMapByFilter(URL_PRE);

        //设置请求参数，可选。可以直接在请求地址后缀加上。Map<表名，Map<key,value>>
//        Map<String, Map<String, String>> paramMap = new HashMap<String, Map<String, String>>();
//        Set<String> keySet = uriMap.keySet();
//        for (String key : keySet) {
//
//            Query query = em.createQuery(MAX_VERSION_SQL);
//            query.setParameter("tableName", key);
//            Object version = query.getSingleResult();
//
//            //将请求参数存入paramMap
//            Map<String, String> params = new HashMap<String, String>();
//            if (null != version) {
//                params.put(key, version.toString().trim());
//            }
//            paramMap.put(key, params);
//        }

        syncService.setUriMap(uriMap);
//        syncService.setParamMap(paramMap);
        syncService.process();

    }

}

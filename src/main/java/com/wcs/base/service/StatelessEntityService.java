package com.wcs.base.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * 无状态的实体操作类.
 * <p/>
 * 扩展功能包括分页查询,按属性过滤条件列表查询.
 * 可直接使用,也可以扩展EntityService
 *
 * @author chris
 */
@Stateless
public class StatelessEntityService extends EntityService {
	protected Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext(unitName = "pu")
    public EntityManager entityManager;

	public StatelessEntityService(){
	}

    @PostConstruct
    private void initEntityManager(){
        this.setEntityManager(entityManager);
    }
}

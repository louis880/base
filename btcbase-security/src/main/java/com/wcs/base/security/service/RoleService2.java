package com.wcs.base.security.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;

import com.google.common.collect.Lists;
import com.wcs.base.security.model.Permission;
import com.wcs.base.security.model.Resource;
import com.wcs.base.security.model.Role;
import com.wcs.base.service.StatelessEntityService;
import com.wcs.base.util.ResourcesNode;

/**
 * 
 * <p>Project: cmdpms</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2011 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:yourname@wcs-global.com">Your Name</a>
 */

@Stateless
public class RoleService2 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private StatelessEntityService entityService;
	@Inject
	private ResourceService resourceService;

	public RoleService2() {
		System.out.println("start roleservice....");
	}
	
	/**
     * 动态分页， XSQL 查询 （推荐使用）
     * @param queryMap
     * @return LazyDataModel<Person>
     */
	public LazyDataModel<Role> findModelByMap(Map<String, Object> queryMap) {
		String hql = "select r from Role r where 1=1 ";
		StringBuilder xsql =  new StringBuilder(hql);
	    xsql.append(" /~ and r.name like {name} ~/ ");
	    return entityService.findXsqlPage(xsql.toString(), queryMap);
	}

	/**
	 * Find all role
	 * @return
	 */
	public List<Role> getRoles() {
		String jpql = "SELECT r FROM Role r ORDER BY r.name ASC";
		List<Role> allRoles = entityService.findList(jpql);
		return allRoles;
	}

	/**
	 * Description:得到最顶层资源
	 * @return
	 */
	public List<Resource> getSysTopResource() {
		String sql = "SELECT r FROM Resource r WHERE r.parentId = 0";
		List<Resource> list = this.entityService.findList(sql);
		return list;
	}

	/**
	 * Description: 角色列表分页
	 * @param first
	 * @param pageSize
	 * @param sortField
	 * @param sortOrder
	 * @param filtermap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getResultByPage(int first, int pageSize, String sortField, boolean sortOrder, Map<String, String> filtermap) {
		try {
			String sql = "SELECT role FROM Role role WHERE role.state=1 ";
			Query query = this.entityService.createQuery(sql);
			query.setFirstResult(first);
			query.setMaxResults(pageSize);
			return (List<Role>) query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Description:得到角色总记录
	 * @return
	 */
	public int getRowCount() {
		try {
			return this.entityService.findAll(Role.class).size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Description: 根据角色名查询匹配的角色列表
	 * @param rolename
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public List<String> searchRole(String rolename) {
		List<String> rnamelist = Lists.newArrayList();
		try {
			Query query = entityService.createQuery("SELECT r FROM Role r WHERE r.roleName LIKE :roleNames ORDER BY roleName ASC");
			query.setParameter("roleNames", "%" + rolename + "%");
			List<Role> rlist = query.getResultList();
			for (Role r : rlist) {
				// rnamelist.add(r.getRoleName());
			}
			return rnamelist;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rnamelist;
	}

	/**
	 * Description: 根据角色查询角色对象
	 * @param name
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Role getRoleByName(String name) {
		try {
			String sql = "SELECT r FROM Role r WHERE r.roleName = ?1";
			Query query = this.entityService.createQuery(sql);
			query.setParameter(1, name);
			List st = query.getResultList();
			return (Role) st.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *  Description: 修改角色信息
	 * @param role
	 * @throws Exception
	 */
	public void updateRole(Role role) throws Exception {
		try {
			entityService.update(role);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Description: 删除角色
	 * @param role
	 * @throws Exception
	 */
	public void deleteRole(Role role) throws Exception {
		String sql1 = "DELETE FROM UserRole ur WHERE ur.roleid = ?1";
		String sql2 = "DELETE FROM Permission p WHERE p.roleid = ?1";
		String sql3 = "DELETE FROM Role r WHERE r.id = ?1 ";
		this.entityService.batchExecute(sql1, role.getId());
		this.entityService.batchExecute(sql2, role.getId());
		this.entityService.batchExecute(sql3, role.getId());
	}
	
	/**
	 * Description: 根据角色Id数组得到角色集合列表
	 * @param roleId
	 * @return
	 * @throws Exception
	 */
	public List<Role> getSelectRoleById(Long[] roleId) throws Exception {
		List<Role> list = Lists.newArrayList();
		if (roleId == null || roleId.length == 0) {
			return list;
		}
		int length = roleId.length;
		for (int i = 0; i < length; i++) {
			list.add(this.entityService.findUnique(Role.class, roleId[i]));
		}
		return list;
	}

	/**
	 * Description: 初始化父级资源
	 * @param root
	 * @param sysResource
	 * @param map
	 */
	public void initFatherNode(TreeNode root, List<Resource> sysResource, Map<String, Permission> map) {
		List<Resource> flist = this.findTopResource(sysResource);
		for (Resource father : flist) {
			ResourcesNode fnode = new ResourcesNode(father.getName(), root);
			if (map.get(father.getKeyName()) != null) {
				fnode.setSelected(true);
			}
			fnode.setId(father.getId());
			fnode.setUrl(father.getUrl());
			initChildNode(father.getId(), fnode, sysResource, map);
		}
	}

	/**
	 * Description: 初始化下级资源
	 * @param id
	 * @param father
	 * @param sysResource
	 * @param map
	 */
	@SuppressWarnings("unused")
	public void initChildNode(Long id, TreeNode father, List<Resource> sysResource, Map<String, Permission> map) {
		List<Resource> chillist = this.findChildResource(id, sysResource);
		boolean flag = false;
		for (Resource child : chillist) {
			List<Resource> sercondlist = this.findChildResource(child.getId(), sysResource);
			if (!sercondlist.isEmpty()) {
				ResourcesNode childnode = new ResourcesNode(child.getName(), father);
				if (map.get(child.getKeyName()) != null) {
					childnode.setSelected(true);
				}
				childnode.setId(child.getId());
				childnode.setUrl(child.getUrl());
				initChildNode(child.getId(), childnode, sysResource, map);
			} else {

				ResourcesNode fnode = new ResourcesNode(child.getName(), father);
				if (map.get(child.getKeyName()) != null) {
					fnode.setSelected(true);
				}
				// fnode.setSelected(flag);
				fnode.setId(child.getId());
				fnode.setUrl(child.getUrl());
			}

		}
	}

	/**
	 * Description: 根据id得到孩子节点
	 * @param id
	 * @param sysResource
	 * @return
	 */
	public List<Resource> findChildResource(Long id, List<Resource> sysResource) {
		List<Resource> childResource = new ArrayList<Resource>();
		for (Resource rs : sysResource) {
			if (id.equals(rs.getParentId())) {
				childResource.add(rs);
			}
		}
		return childResource;
	}

	/**
	 * Description: 得到顶级资源
	 * @param sysResource
	 * @return
	 */
	public List<Resource> findTopResource(List<Resource> sysResource) {
		List<Resource> topResource = new ArrayList<Resource>();
		for (Resource rs : sysResource) {
			if (rs.getParentId() == null || rs.getParentId() == 0) {
				topResource.add(rs);
			}
		}
		return topResource;
	}

	/**
	 * @param root
	 * @param sysResource
	 * @param role
	 * @throws Exception
	 */
	public void isSelectedResourceByRole(TreeNode root, List<Resource> sysResource, Role role) throws Exception {
		List<Permission> roleResource = this.resourceService.findResouceByRole(role);
		Map<String, Permission> map = new HashMap<String, Permission>();
		if (!roleResource.isEmpty()) {
			for (Permission p : roleResource) {
				map.put(p.getPermission(), p);
			}

		}
		this.initFatherNode(root, sysResource, map);
	}

}
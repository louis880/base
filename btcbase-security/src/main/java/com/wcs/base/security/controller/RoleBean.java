/**
 * RoleBean.java Created: 2011-7-8 上午11:04:11
 */
package com.wcs.base.security.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.wcs.base.conf.SystemConfiguration;
import com.wcs.base.controller.ConversationBaseBean;
import com.wcs.base.security.model.Resource;
import com.wcs.base.security.model.Role;
import com.wcs.base.security.service.ResourceService;
import com.wcs.base.security.service.RoleService;
import com.wcs.base.security.vo.ResourcesNode;
import com.wcs.base.service.StatelessEntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MessageUtils;

/**
 * <p>Project: btcbase</p> 
 * <p>Title: RoleBean.java</p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright .All rights reserved.</p> 
 * <p>Company: wcs.com</p> 
 * @author <a href="mailto:yujingu@wcs-gloabl.com">Yu JinGu</a>
 */

@SuppressWarnings("rawtypes")
@Named
@ConversationScoped
public class RoleBean extends ConversationBaseBean {
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(RoleBean.class);

	@Inject
	private StatelessEntityService entityService;
	@Inject
	private RoleService roleService;
	@Inject
	private ResourceService resourceService;

	private TreeNode root;// 资源树
	private TreeNode[] selectedNodes;// 节点数组
	private LazyDataModel<Role> lazyModel;// 数据模型
	private String roName;// 角色名字
	private Map<String, Object> queryMap = new HashMap<String, Object>();// 查询条件Map封装
	private Role currentRole = new Role(); // 当前角色对象

	private static final String LIST_PAGE = "/faces/permissions/role/list.xhtml";
	private static final String ROLE_RESOURCE_PAGE = "/faces/permissions/role/resource-role.xhtml";
	
	public RoleBean() {
		
	}

	@SuppressWarnings("unused")
	@PostConstruct
	private void initLazyModel() {
		this.search();
	}
	
	/**
	 * 角色查询方法
	 * @return
	 */
	public void search() {
		this.lazyModel = roleService.findModelByMap(queryMap);
	}
	
	/**
	 * 保存角色
	 */
	public void saveRole() {
		try {
			// this.currentRole.setAdmin(false);
			this.entityService.create(this.currentRole);
			MessageUtils.addSuccessMessage("rolemessgeId", "角色添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.addErrorMessage("rolemessgeId", "角色添加失败");
		}

	}

	/**
	 * 保存角色资源
	 * @return
	 */
	@SuppressWarnings("unused")
	public String saveRoleResource() {
		if (this.currentRole == null) {
			MessageUtils.addErrorMessage("rolemessgeId", "当前角色为空!");
			return JSFUtils.getViewId();
		}

		// 删除当前角色旧资源
		try {
			this.resourceService.deleteRoleResource(this.currentRole);
			this.resourceService.deleteRolePermission(this.currentRole);
		} catch (Exception e) {
			MessageUtils.addErrorMessage("rolemessgeId", "删除角色旧资源失败.");
			return JSFUtils.getViewId();
		}

		// 保存角色资源
		try {
			List<Resource> listresouce = this.resourceService.getSelectResource(selectedNodes);
			// for (Resource resource : listresouce) {
			// 保存角色资源对应关系
			/*
			 * RoleResource roleResource = new RoleResource();
			 * roleResource.setRole(this.currentRole);
			 * roleResource.setResource(resource);
			 * this.entityService.create(roleResource);
			 * 
			 * // 创建权限对象 Permission permission = new Permission();
			 * permission.setRole(this.currentRole); if (resource.getKeyName()
			 * != null) { String permissionString = "view:" +
			 * resource.getKeyName();
			 * permission.setPermission(permissionString); }
			 * permission.setPermissionName(resource.getName());
			 * this.entityService.create(permission);
			 */
			// }
		} catch (Exception e) {
			MessageUtils.addErrorMessage("rolemessgeId", "保存角色资源失败.");
			return JSFUtils.getViewId();
		}

		MessageUtils.addSuccessMessage("rolemessgeId", "角色资源分配成功");
		return LIST_PAGE;
	}

	/**
	 * 角色编辑初始化
	 */
	public void editInit() {

		/*
		 * roleVo.setRoleName(this.currentRole.getRoleName());
		 * roleVo.setState(this.currentRole.getState());
		 * roleVo.setDescription(this.currentRole.getDescription());
		 */
	}

	/**
	 * 角色编辑
	 */
	public void update() {
		try {
			/*
			 * this.currentRole.setRoleName(this.roleVo.getRoleName());
			 * this.currentRole.setDescription(this.roleVo.getDescription());
			 * this.currentRole.setState(this.roleVo.getState());
			 */
			this.entityService.update(currentRole);
			MessageUtils.addSuccessMessage("rolemessgeId", "角色更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.addErrorMessage("rolemessgeId", "角色更新失败");
		}

	}

	/**
	 * 节点选中监听
	 * @param eve
	 */
	public void onTreeNodeClicked(NodeSelectEvent eve) {
		System.out.println("onTreeNodeClicked >>>>> into");
	}

	/**
	 * 角色名输入匹配
	 * @param queryStr
	 * @return
	 */
	public List<String> complete(String queryStr) {
		if (queryStr != null && !"".equals(queryStr)) {
			return this.roleService.searchRole(roName);
		} else {
			return Lists.newArrayList("无配置项");
		}

	}

	/**
	 * 角色删除
	 */
	public void deleteRole() {
		try {
			logger.debug("当前角色  --->> " + this.currentRole.getId());
			this.roleService.deleteRole(currentRole);
			MessageUtils.addSuccessMessage("rolemessgeId", "角色删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			MessageUtils.addErrorMessage("rolemessgeId", "删除失败");
		}
	}

	/**
	 * 角色管理页面跳转
	 * @return
	 */
	public String roleList() {
		return LIST_PAGE;
	}

	/**
	 * 资源分配页面跳转
	 * @return
	 */
	public String resourceJump() {
		// this.roleVo.setRoleName(this.currentRole.getRoleName());
		root = new ResourcesNode("系统资源", null);
		// 若该角色已经分配过资源则查询已有的资源 并设置选中
		List<Resource> allResource = roleService.getAllResource();
		try {
			this.roleService.isSelectedResourceByRole(root, allResource, this.currentRole);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ROLE_RESOURCE_PAGE;
	}

	public String goBack() {

		return LIST_PAGE;
	}

	/**
	 * 资源分配页面跳转 直接通过角色名查询角色对象之后
	 * @return
	 */
	public String rescourceJump() {
		return "/faces/permissions/role/resourceRoleInput.xhtml";
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public LazyDataModel<Role> getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(LazyDataModel<Role> lazyModel) {
		this.lazyModel = lazyModel;
	}

	public String getRoName() {
		return roName;
	}

	public void setRoName(String roName) {
		this.roName = roName;
	}

	public Map<String, Object> getQueryMap() {
		return queryMap;
	}

	public void setQueryMap(Map<String, Object> queryMap) {
		this.queryMap = queryMap;
	}

	public Role getCurrentRole() {
		return currentRole;
	}

	public void setCurrentRole(Role currentRole) {
		this.currentRole = currentRole;
	}

	public String getRowsPerPageTemplate() {
		return SystemConfiguration.ROWS_PER_PAGE_TEMPLATE;
	}
}

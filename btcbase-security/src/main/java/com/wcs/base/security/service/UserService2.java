package com.wcs.base.security.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.persistence.Query;

import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.base.security.model.Resource;
import com.wcs.base.security.model.Role;
import com.wcs.base.security.model.User;
import com.wcs.base.security.model.UserRole3;
import com.wcs.base.service.StatelessEntityService;
import com.wcs.base.util.CollectionUtils;

/**
 * <p>Project: btcbase</p> 
 * <p>Title: UserService.java</p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright .All rights reserved.</p> 
 * <p>Company: wcs.com</p> 
 * @author <a href="mailto:yujingu@wcs-gloabl.com">Yu JinGu</a>
 */

@Stateless
public class UserService2 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private StatelessEntityService entityService;
	
    public UserService2() {}

   /**
    * 通过用户名查询唯一用户
    * @param userName
    * @return
    */
    public User findUniqueUser(String loginName) {
        String sql = "SELECT u FROM User u WHERE u.loginName ='" + loginName + "'";
        List<?> list = entityService.createQuery(sql).getResultList();
        User u = null;
        if (!list.isEmpty()) {
            u = (User) list.get(0);
        }
        return u == null ? null : u;
    }


   /**
    * 查询所有资源根据角色
    * @param roleList
    * @return
    * @throws Exception
    */
    public List<Resource> findAllResouceOfRoleList(List<Role> roleList) throws Exception {
        List<Resource> distinctResource = new ArrayList<Resource>();
        try {
            if (roleList.isEmpty()) { return distinctResource; }
            String jpql = "select res from RoleResource rr join rr.resource res join rr.role role where role in (?1)";
            List<Resource> resourceList = entityService.findList(jpql, roleList);
            for (Resource resource : resourceList) {
                if (!distinctResource.contains(resource)) {
                    /*if ("3".equals(resource.getLevel())) {
                        if (!distinctResource.contains(resourceService.loadTree(resource.getParentId()))) {
                            distinctResource.add(resourceService.loadTree(resource.getParentId()));
                        }
                    }*/
                    distinctResource.add(resource);
                }
            }
            return distinctResource;
        } catch (Exception e) {
            throw e;
        }
    }

   /**
    * 根据用户查询所拥有的角色
    * @param user
    * @return
    * @throws Exception
    */
    public List<Role> findAllRoleOfUser(User user) throws Exception {
        String jpql = "SELECT r FROM UserRole ur JOIN ur.user u JOIN ur.role r WHERE r.state=1 AND u.id=" + user.getId();
        return entityService.findList(jpql);
    }

   /**
    * 将资源集合转换成ID集合
    * @param resouceList
    * @return
    */
    public Long[] getResouceId(List<Resource> resouceList) {
        try {
            Long[] idArray = null;
            if (!CollectionUtils.isEmpty(resouceList)) {
                int size = resouceList.size();
                idArray = new Long[size];
                for (int i = 0; i < size; i++) {
                    idArray[i] = resouceList.get(i).getId();
                }
            }
            return idArray;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

  /**
   * 根据用户对象返回角色ID数组
   * @param user
   * @return
   */
    public Long[] getRoleIdByUser(User user) {
        try {
            /*List<UserRole> userRolelist = getRoleByUser(user);
            if (userRolelist != null) {
                int size = userRolelist.size();
                Long[] l = new Long[size];
                for (int i = 0; i < size; i++) {
                    Role role = userRolelist.get(i).getRole();
                    if (role.getState() == 1) {
                        l[i] = role.getId();
                    }
                }
                return l;
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户账户输入匹配
     * @param account
     * @return
     */
    @SuppressWarnings({ "unchecked", "unused" })
    public List<String> getUserAccountByInput(String account) {
        try {
            String sql = "SELECT u FROM User u WHERE u.defunctInd = false AND u.userName LIKE :account";
            Query query = this.entityService.createQuery(sql);
            query.setParameter("account", "%" + account + "%");
            List<User> ulist = query.getResultList();
            if (ulist.size() != 0) {
                List<String> rlist = Lists.newArrayList();
                for (User user : ulist) {
                   // rlist.add(user.getUserName());
                }
                return rlist;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

  /**
   * 根据账户和电子邮件查询User
   * @param account
   * @param emial
   * @return
   */
    public User getUserByEmail(String account, String emial) {
        try {
            String sql = "SELECT u FROM User u WHERE u.defunctInd = false ADN u.userName = :uname AND u.email = :Email";
            Query query = this.entityService.createQuery(sql);
            query.setParameter("uname", account);
            query.setParameter("Email", emial);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   /**
    * 得到所有菜单
    * @return
    */
    public List<Resource> findAllResources() {
        String sql = "SELECT r FROM Resource r WHERE r.ismenu = 1";
        Query query = entityService.createQuery(sql);
        @SuppressWarnings("unchecked")
        List<Resource> resourceList = query.getResultList();

        return resourceList;
    }

    /**
     * 查询用户列表
     * @param loginName
     * @return
     */
    @SuppressWarnings({ "unchecked", "unused" })
    public LazyDataModel<User> searchUserByName(String loginName) {
        String sql = "SELECT u FROM User u WHERE u.loginName LIKE :loginName";
        Query query = this.entityService.createQuery(sql);
        query.setParameter("loginName", "%" + loginName + "%");
        List<User> rsList = query.getResultList();
        
        // 转换成LazyModel
       // LazyDataModel<User> lazyMode = LazyModelUtil.getLazyUserDataModel(rsList);
        
        return null; // lazyMode;
    }
    
    /**
     * 查找所有用户列表
     * @return
     */
    @SuppressWarnings({ "unchecked", "unused" })
    public LazyDataModel<User> findAllUser() {
        String sql = "SELECT u FROM User u";
        Query query = this.entityService.createQuery(sql);
        List<User> rsList = query.getResultList();
        
        // 转换成LazyModel
        // LazyDataModel<User> lazyMode = LazyModelUtil.getLazyUserDataModel(rsList);
        
        return null; // lazyMode;
    }

    /**
     * 初始化角色列表
     * @return
     */
    @SuppressWarnings("unused")
	public List<SelectItem> initRoleList() {
        List<SelectItem> list = new ArrayList<SelectItem>();
        String sql = "select r from Role r where r.state =1";
        List<Role> roleList = this.entityService.findList(sql);
        for (Role role : roleList) {
            // list.add(new SelectItem(role.getId(), role.getRoleName()));
        }

        return list;
    }

    @SuppressWarnings({ "unused", "null" })
    public Long[] assignUserRole(User user) {
        User currentUser = this.findUniqueUser(user.getLoginName());
        Set<Role> roles = null; // currentUser.getRole();
        Object[] userRole = roles.toArray();
        Long[] uRole = new Long[userRole.length];
        for (int i = 0; i < userRole.length; i++) {
            Role role = (Role) userRole[i];
           /* if (role.getState() == 1) {
                uRole[i] = role.getId();
            }*/
        }

        return uRole;
    }

    /**
     * 修改用户
     * @param user
     */
    public void modUser(User user) {
       this.entityService.update(user);
    }

    /**
     * 删除当前选中用户
     * @param user
     */
    public Boolean delUser(User user) {
        // 删除用户对应中间表
        String sql = "DELETE FROM UserRole ur WHERE ur.user.id = ?1";
        Query query = entityService.createQuery(sql,  user.getId());
        int rs = query.executeUpdate();
        
        // 删除用户
        sql = "DELETE FROM User u WHERE u.id=?1";
        int rs1 = this.entityService.batchExecute(sql, user.getId());
        if (rs > 0 && rs1 > 0) {
            return true;  
        }
        
        return false;
    }
    
    /**
     * 根据用户得到当前用户角色
     * @param user
     * @return
     */
    public List<Role> findRolesByUser(User user) {
        String sql = "select r from UserRole ur join ur.user u join ur.role r where r.state=1 and u.id=" + user.getId();
        return entityService.findList(sql);
    }

    /**
     * 根据角色ID找到角色
     * @param object
     * @return
     */
    public Role findRoleById(Long roleId) {
        String sql = "SELECT r FROM Role r WHERE r.id = " + roleId;
        Query query = entityService.createQuery(sql);
        Role role = (Role) query.getSingleResult();
        
        return role;
    }

    /**
     * Find users by loginName from queryMap
     * @param queryMap
     * @return
     */
	public LazyDataModel<User> findUsers(Map<String, Object> queryMap) {
		String sql = "SELECT u FROM User u WHERE u.defunctInd = false";
		StringBuilder xsql =  new StringBuilder(sql);
	    xsql.append(" /~ and u.loginName like {loginName} ~/ ");
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
     * Delete user roles by userId
     * @param instance
     */
    public void delUserRole(User user) {
        Query q = entityService.createQuery("DELETE FROM UserRole ur where  ur.userid = :userId");
        q.setParameter("userId", user.getId());
        q.executeUpdate();
    }

    /**
     * Set current user roles
     * @param roleList
     */
    public void createUserRoles(User user, List<Role> roleList) {
        UserRole3 userRole = new UserRole3();
        for (Role role : roleList) {
            userRole.setUserid(user.getId());
            userRole.setRoleid(role.getId());
            entityService.create(userRole);
        }
    }
}
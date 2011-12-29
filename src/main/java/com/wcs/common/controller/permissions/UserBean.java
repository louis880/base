package com.wcs.common.controller.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import com.google.common.collect.Lists;
import com.wcs.base.conf.SystemConfiguration;
import com.wcs.base.controller.ViewBaseBean;
import com.wcs.base.service.StatelessEntityService;
import com.wcs.base.util.JSFUtils;
import com.wcs.base.util.MessageUtils;
import com.wcs.common.model.Role;
import com.wcs.common.model.User;
import com.wcs.common.model.UserRole;
import com.wcs.common.service.permissions.RoleService;
import com.wcs.common.service.permissions.UserService;

/**
 * <p>Project: btcbase</p> 
 * <p>Title: UserBean.java</p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright .All rights reserved.</p> 
 * <p>Company: wcs.com</p> 
 * @author <a href="mailto:yujingu@wcs-gloabl.com">Yu JinGu</a>
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class UserBean extends ViewBaseBean<User> {
    @EJB
    private UserService userService;
    @Inject
    private User user;
    @Inject
    private StatelessEntityService entityService;
    @Inject
    private RoleService roleService;
    
    private LazyDataModel<User> lazyModel;
    private User currentUser;
    private List<SelectItem> roleList;  // 角色下拉列表
    private Long[] selectedUserRole;
    
    private Boolean isDisplay;
   
    /** 角色Id */
    private Long roleId;

    /** 账户 */
    private String account;
    /** 电子邮件地址 */
    private String email;
    /** 角色选中数组 */
    private Long[] urole;
    /** 用于查询账号字段 */
    private String userAccount;
    /** 查询条件Map封装 */
    private Map<String, Object> queryMap = new HashMap<String, Object>();

    /** 页面路径 */
    private static final String LIST_PAGE = "/faces/permissions/user/list.xhtml";
    private static final String USER_ROLE_PAGE = "/faces/permissions/user/user-role.xhtml";

    public UserBean() {}
    
    @PostConstruct
    public void initUser() {
        this.lazyModel = userService.findAllUser();
    }

    /**
     * 查询用户
     * @return
     */
    public void searchUser() {
        String loginName = JSFUtils.getRequestParam("loginName");
        this.lazyModel = userService.searchUserByName(loginName);
    }
    
    /**
     * 添加用户
     */
    public void addUser() {
        try { 
            this.save();
            MessageUtils.addSuccessMessage("resMsg", "添加用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.addErrorMessage("resMsg", "添加用户失败，请检查！");
        }
        
        // 刷新
        refresh();
    }
    
    /**
     * 修改用户
     */
    public void modUser() {
        User user = getInstance();
        try { 
            this.userService.modUser(user);
            MessageUtils.addSuccessMessage("resMsg", "修改用户成功！");
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.addErrorMessage("resMsg", "修改用户失败，请检查！");
        }
        
        // 刷新
        refresh();
    }
    
    public void delUser() {
        User user = getInstance();
        try { 
            Boolean isSucceed = this.userService.delUser(user);
            if (isSucceed) {
                MessageUtils.addSuccessMessage("resMsg", "删除用户成功！");
            } else {
                MessageUtils.addErrorMessage("resMsg", "删除用户失败，请检查！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 刷新
     */
    private void refresh() {
        this.lazyModel = userService.findAllUser();
    }
    
    /**
     * 清除
     */
    public void clean() {
        setInstance(null);
        this.isDisplay = false;
    }
    
    /**
     * 选中用户记录
     * @param event
     */
    public void onRowSelect(SelectEvent event) {
        User user = (User)event.getObject();
        this.setInstance(user);
        this.isDisplay = true;
    }

    /**
     * 初始化角色列表
     */
    public void initRoleList() {
        if (this.roleList == null || this.roleList.size() > 0) {
            this.roleList = new ArrayList<SelectItem>();
        }

        this.roleList = this.userService.initRoleList();
    }

    /**
     * 当前用户角色
     * @param user
     */
    public String assignUserRole() {
        this.selectedUserRole = this.userService.getRoleIdByUser(this.currentUser);
        return USER_ROLE_PAGE;
    }

    /**
     * 保存或者更新当前用户角色
     * @return
     */
    public String saveUserRole() {
        try {
            List<Role> listrole = this.roleService.getSelectRoleById(this.getSelectedUserRole());
            if (listrole != null) {
                this.roleService.deleteUserRole(this.currentUser.getId());
                for (Role role : listrole) {
                    UserRole userrole = new UserRole();
                    userrole.setUser(currentUser);
                    userrole.setRole(role);
                    this.entityService.create(userrole);
                }
                MessageUtils.addSuccessMessage("usermessgeId", "用户角色分配成功");
            }

        } catch (Exception e) {
            MessageUtils.addErrorMessage("usermessgeId", "用户角色分配失败");
            e.printStackTrace();
            return JSFUtils.getViewId();
        }

        return LIST_PAGE;
    }

    /**
     * 
     * <p>
     * Description: 账户查询
     * </p>
     */
    public String search() {

        StringBuilder sql = new StringBuilder("from User u where 1=1");
        sql.append(" /~ and u.name like  {name}~/ ");
        this.lazyModel = this.entityService.findPageByFilter(sql.toString(), this.queryMap);

        return LIST_PAGE;
    }

    /**
     * 
     * <p>
     * Description: 管理界面跳转
     * </p>
     * 
     * @return
     */
    public String goBack() {
        return LIST_PAGE;
    }

    /**
     * 
     * <p>
     * Description: 输入匹配账号
     * </p>
     * 
     * @param queryStr
     * @return
     */
    public List<String> complete(String queryStr) {

        if (queryStr != null && !"".equals(queryStr)) {
            return this.userService.getUserAccountByInput(account);
        } else {
            return Lists.newArrayList();
        }

    }

    // -------------------------------------Set & Get---------------------------------------------------//

    /**
     * @return the roleId
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     *            the roleId to set
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    /**
     * @return the account
     */
    public String getAccount() {
        return account;
    }

    /**
     * @param account
     *            the account to set
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the urole
     */
    public Long[] getUrole() {
        return urole;
    }

    /**
     * @param urole
     *            the urole to set
     */
    public void setUrole(Long[] urole) {
        this.urole = urole;
    }

    /**
     * @return the userAccount
     */
    public String getUserAccount() {
        return userAccount;
    }

    /**
     * @param userAccount
     *            the userAccount to set
     */
    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return the queryMap
     */
    public Map<String, Object> getQueryMap() {
        return queryMap;
    }

    /**
     * @param queryMap
     *            the queryMap to set
     */
    public void setQueryMap(Map<String, Object> queryMap) {
        this.queryMap = queryMap;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public String getRowsPerPageTemplate() {
        return SystemConfiguration.ROWS_PER_PAGE_TEMPLATE;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<SelectItem> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SelectItem> roleList) {
        this.roleList = roleList;
    }

    public Long[] getSelectedUserRole() {
        return selectedUserRole;
    }

    public void setSelectedUserRole(Long[] selectedUserRole) {
        this.selectedUserRole = selectedUserRole;
    }

    public LazyDataModel<User> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<User> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public Boolean getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(Boolean isDisplay) {
        this.isDisplay = isDisplay;
    }
}

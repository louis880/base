package com.wcs.base.security.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.wcs.base.entity.IdEntity;


/**
 * <p>Project: btcbase-security</p> 
 * <p>Title: </p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright 2011-2020.All rights reserved.</p> 
 * <p>Company: wcs.com</p>
 * @author guanjianghuai
 */
@Entity
@Table(name="ROLE_RESOURCE")
public class RoleResource extends IdEntity {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="RESOURCE_ID")
	private Resource resource;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_ID")
	private Role role;
	
	@Column(length=200)
	private String uri;

	//--------------------- setter & getter -------------------//
	
	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
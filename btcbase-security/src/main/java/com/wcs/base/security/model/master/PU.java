package com.wcs.base.security.model.master;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the PU database table.
 * 
 */
@Entity
@Table(name="PU")
public class PU implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="DEFUNCT_IND", length=1)
	private String defunctInd;

	@Id
	@Column(length=50)  
	private String id;  // User->AD_Account

	@Column(length=20)   // Person->ID
	private String pernr;

    public PU() {
    }

	public String getDefunctInd() {
		return this.defunctInd;
	}

	public void setDefunctInd(String defunctInd) {
		this.defunctInd = defunctInd;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPernr() {
		return this.pernr;
	}

	public void setPernr(String pernr) {
		this.pernr = pernr;
	}

}
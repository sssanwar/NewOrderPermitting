package com.solarvillage;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class SalesRep implements java.io.Serializable {

	static final long serialVersionUID = 1L;

	@org.kie.api.definition.type.Label("Email")
	private java.lang.String email;
	@org.kie.api.definition.type.Label("Name")
	private java.lang.String name;

	public SalesRep() {
	}

	public java.lang.String getEmail() {
		return this.email;
	}

	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	public java.lang.String getName() {
		return this.name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public SalesRep(java.lang.String email, java.lang.String name) {
		this.email = email;
		this.name = name;
	}

}
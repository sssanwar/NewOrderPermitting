package com.solarvillage;

/**
 * This class was automatically generated by the data modeler tool.
 */

public class Customer implements java.io.Serializable {

	static final long serialVersionUID = 1L;

	@org.kie.api.definition.type.Label(value = "Name")
	private java.lang.String name;
	@org.kie.api.definition.type.Label(value = "Email")
	private java.lang.String email;

	public Customer() {
	}

	public java.lang.String getName() {
		return this.name;
	}

	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getEmail() {
		return this.email;
	}

	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	public Customer(java.lang.String name, java.lang.String email) {
		this.name = name;
		this.email = email;
	}

}
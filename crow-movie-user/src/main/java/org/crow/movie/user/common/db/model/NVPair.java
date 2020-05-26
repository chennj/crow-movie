package org.crow.movie.user.common.db.model;

public class NVPair {

	private String name;
	private String op;
	private Object value;
	
	public NVPair(String name, String op, Object value){
		this.name = name;
		this.op = op;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return name+" "+op+" "+value;
	}
	
	
}

package org.crow.movie.user.model;

import java.io.Serializable;

public class User_t implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 335522328220224564L;

	private Integer id;
    private String username;
    private Integer age;

    public User_t(Integer id, String username, Integer age) {
        this.id = id;
        this.username = username;
        this.age = age;
    }

    public User_t() {
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
    
	public String toString(){
		return "id:"+id+";age:"+age+";username:"+username;
	}
}

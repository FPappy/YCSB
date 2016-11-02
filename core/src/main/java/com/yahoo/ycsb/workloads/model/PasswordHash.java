package com.yahoo.ycsb.workloads.model;

public class PasswordHash
{
  
    private String hash;
    private String salt;
    private Integer algorithm;
    private String version;

  
    public String getId()
    {
        // this class does not have getId so just return NULL
        return null;
    }

	public Integer getAlgorithm() {
		return algorithm;
	}
	 
	public void setAlgorithm(Integer algorithm) {
		this.algorithm = algorithm;
	}
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	public String getVersion() {
		return version;
	}
	 
	public void setVersion(String version) {
		this.version = version;
	}
}
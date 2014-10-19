package db.jpa;

import java.io.Serializable;



public class CompositeDestructorKey implements Serializable{
    private static final long serialVersionUID = 5012404308795972775L;
    private String id;
    private String warName;
    
    public CompositeDestructorKey() {
    }
    
    public CompositeDestructorKey(String id, String warName) {
	this.id = id;
	this.warName = warName;
    }
    
    public String getId() {
	return id;
    }
    
    public String getWarName() {
	return warName;
    }
    
    public void setId(String id) {
	this.id = id;
    }
    
    public void setWarName(String warName) {
	this.warName = warName;
    }
    
    @Override
    public boolean equals(Object obj) {
	if(obj instanceof CompositeDestructorKey){
	    CompositeDestructorKey other = (CompositeDestructorKey)obj;
	    if(other.id.equals(id) && other.warName.equals(warName))
		return true;
	}
	return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

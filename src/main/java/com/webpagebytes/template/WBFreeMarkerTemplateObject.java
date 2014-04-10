package com.webpagebytes.template;

public class WBFreeMarkerTemplateObject {
	public enum TemplateType {
		TEMPLATE_PAGE,
		TEMPLATE_MODULE
	};
	private long lastModified = 0;
	private String name;
	private TemplateType type;
	public WBFreeMarkerTemplateObject(String name, TemplateType type, long lastModified)
	{
		setType(type);
		setLastModified(lastModified);
		setName(name);
	}
	
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TemplateType getType() {
		return type;
	}

	public void setType(TemplateType type) {
		this.type = type;
	}

	@Override public boolean equals(Object other) 
	 {
        boolean result = false;
        if (other instanceof WBFreeMarkerTemplateObject) {
            WBFreeMarkerTemplateObject that = (WBFreeMarkerTemplateObject) other;
            result = (this.lastModified == that.lastModified 
            		 && this.name.equals(that.name)
            		 && this.type.equals(that.type));
        }
        return result;
	 }
	 @Override public int hashCode() 
	 {
		 	int nameHash = name != null ? name.hashCode() : 0;
		 	int typeint = type == TemplateType.TEMPLATE_PAGE ? 1 : 2;
		 	return 41*(41 * (41 + (int)lastModified) + typeint) + nameHash;
	 }
	 
	 
}

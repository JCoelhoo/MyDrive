package pt.tecnico.mydrive.service.dto;

import org.joda.time.DateTime;

public class EntryDto implements Comparable<EntryDto> {
	private String name;
	private int entryID;
	private String permissions;
	private DateTime lastModified;
	private String owner;
	private String type;

    public EntryDto(String name, int entryID, String permissions, DateTime lastModified, String owner, String type) {
        this.name = name;
        this.entryID = entryID;
        this.permissions = permissions;
        this.lastModified = lastModified;
        this.owner = owner;
        this.type = type;
    }

    public final String getName() {
        return this.name;
    }

	public final int getEntryID() {
		return entryID;
	}
	
	public final String getPermissions() {
		return permissions;
	}

	public final DateTime getLastModified() {
		return lastModified;
	}
	
	public final String getOwner() {
		return owner;
	}
	
	public final String getType() {
		return type;
	}
	
	@Override
    public int compareTo(EntryDto other) {
		return getName().compareToIgnoreCase(other.getName());
    }
	
	@Override
	public String toString(){
		
		return getType() + " " +getPermissions() + " " +getEntryID() + " " +getOwner() + " " +getLastModified() 
				+ " " +getName();
		
	}
}

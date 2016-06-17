package pt.tecnico.mydrive.service.dto;

public class EnvVarDto implements Comparable<EnvVarDto> {
	private String name;
	private String value;

    public EnvVarDto(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public final String getName() {
        return this.name;
    }

	public final String getValue() {
		return this.value;
	}
	
	@Override
    public int compareTo(EnvVarDto other) {
		return getName().compareToIgnoreCase(other.getName());
    }
}

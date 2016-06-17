package pt.tecnico.mydrive.domain;

public class EnvironmentVariable extends EnvironmentVariable_Base {
    
    public EnvironmentVariable(String name, String value) {
        super();
        setName(name);
        setValue(value);
    }

    public void remove() {
        setLogin(null);
        deleteDomainObject();
    }
    
}

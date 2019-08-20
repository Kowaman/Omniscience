package net.lordofthecraft.omniscience.api.data.datatype;

public class ClassType implements DataType {

    private final Class<?> theClass;

    public ClassType(Class<?> theClass) {
        this.theClass = theClass;
    }

    public Class<?> getTheClass() {
        return theClass;
    }
}

package bbk.dng.utils;

/**
 * Date: 20-Aug-2008 12:50:34
 */
public class NameValue {
    public NameValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return name;
    }

    private String name;
    private String value;
}

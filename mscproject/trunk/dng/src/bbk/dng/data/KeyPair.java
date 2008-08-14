package bbk.dng.data;

/**
 * Date: 14-Aug-2008 14:44:19
 */
public class KeyPair {
    public String keyOne;
    public String keyTwo;

    public KeyPair(String keyOne, String keyTwo) {
        this.keyOne = keyOne;
        this.keyTwo = keyTwo;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyPair keyPair = (KeyPair) o;

        if (keyOne != null ? !keyOne.equals(keyPair.keyOne) : keyPair.keyOne != null) return false;
        if (keyTwo != null ? !keyTwo.equals(keyPair.keyTwo) : keyPair.keyTwo != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (keyOne != null ? keyOne.hashCode() : 0);
        result = 31 * result + (keyTwo != null ? keyTwo.hashCode() : 0);
        return result;
    }
}

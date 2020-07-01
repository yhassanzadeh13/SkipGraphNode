package skipnode;

import java.util.Objects;

// Basic skipnode.SkipNodeIdentity class
public class SkipNodeIdentity {
    private final String nameID;
    private final int numID;
    private final String address;
    public SkipNodeIdentity(String nameID, int numID, String address){
        this.nameID=nameID;
        this.numID=numID;
        this.address = address;
    }

    public String getNameID() {
        return nameID;
    }

    public int getNumID() {
        return numID;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkipNodeIdentity that = (SkipNodeIdentity) o;
        return getNumID() == that.getNumID() &&
                getNameID().equals(that.getNameID()) &&
                getAddress().equals(that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNameID(), getNumID(), getAddress());
    }
}

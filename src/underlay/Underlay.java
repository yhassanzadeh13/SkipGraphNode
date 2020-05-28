package underlay;

public abstract class Underlay {
    protected ConnectionAdapter connectionAdapter;
    public abstract RequestResponse sendMessage(String address, RequestType t, RequestParameters p);
}

package underlay;

public abstract class Underlay {
    private ConnectionAdapter connectionAdapter;

    public abstract RequestResponse sendMessage(String address, RequestType t, RequestParameters p);
}

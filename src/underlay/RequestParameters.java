package underlay;

/**
 * Contains the set of key value pairs of a request sent by the client.
 */
public interface RequestParameters {
    Object getRequestValue(String parameterName);
}

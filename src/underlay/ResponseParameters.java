package underlay;

/**
 * Contains the set of key value pairs of a response sent by the server.
 */
public interface ResponseParameters {
    Object getResponseValue(String parameterName);
}

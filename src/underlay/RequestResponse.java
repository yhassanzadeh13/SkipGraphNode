package underlay;

// Contains the set of parameters of a response received from a skip-graph node.
public interface RequestResponse {
    Object getResponseValue(String parameterName);
}

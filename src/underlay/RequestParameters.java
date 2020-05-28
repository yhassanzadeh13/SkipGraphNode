package underlay;

// Contains the set of parameters of a request sent to a skip-graph node.
public interface RequestParameters {
    Object getRequestValue(String parameterName);
}

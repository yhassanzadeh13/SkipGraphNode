package underlay;

import java.io.Serializable;

/**
 * Represents an empty acknowledgement response.
 */
public class AckResponse implements ResponseParameters {

    @Override
    public Object getResponseValue(String parameterName) {
        return null;
    }
}

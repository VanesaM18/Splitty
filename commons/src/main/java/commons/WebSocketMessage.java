package commons;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class WebSocketMessage {
    private String id;
    private Object data;
    private String method;
    private String endpoint;
    private List<Object> parameters;
    private String authHeader;

    /**
     * Gets parameters list
     * 
     * @return parameter list
     */
    public List<Object> getParameters() {
        return parameters;
    }

    /**
     * Sets parameters
     * 
     * @param parameters the parameters to be set
     */
    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get the endpoint
     * 
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Sets endpoint
     * 
     * @param endpoint the endpoint to be set
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Get the method
     * 
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets method
     * 
     * @param method the method to be set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Get the id
     * 
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id
     * 
     * @param id the id to be set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the data
     * 
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets data
     * 
     * @param data the data to be set
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * get authorization header
     * 
     * @return authorization header
     */
    public String getAuthHeader() {
        return authHeader;
    }

    /**
     * sets authorization header
     * 
     * @param authHeader authorization header
     */
    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    /**
     * turns this into a readable string
     *
     * @return string representation of the monetary value
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

}

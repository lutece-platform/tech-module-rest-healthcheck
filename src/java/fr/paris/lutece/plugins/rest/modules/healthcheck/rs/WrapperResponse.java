package fr.paris.lutece.plugins.rest.modules.healthcheck.rs;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponse.Status;

public class WrapperResponse {

    private final String name;

    private final Status status;

    private final Map<String, String> data = new HashMap<String, String>();

	
    public WrapperResponse(HealthCheckResponse response) {
       
       name = response.getName();

       status = response.getStatus();

       for (Map.Entry<String, Object> pair : response.getData().get().entrySet()) {
    	    data.put(pair.getKey(),pair.getValue().toString());
    	}
    	
    }


	public String getName() {
		return name;
	}


	public Status getStatus() {
		return status;
	}


	public Map<String, String> getData() {
		return data;
	}
    
    
	
}

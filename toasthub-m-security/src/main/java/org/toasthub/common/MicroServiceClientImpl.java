package org.toasthub.common;

import org.springframework.stereotype.Component;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.serviceCrawler.MicroServiceClient;


@Component("MicroServiceClient")
public class MicroServiceClientImpl implements MicroServiceClient {
	
	@Override
	public void process(RestRequest request, RestResponse response) {
		
	}

}

package ru.itmo.hotdogs.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import ru.itmo.hotdogs.exceptions.RestApiClientException;

public class CustomErrorDecoder implements ErrorDecoder {
	private static final Logger LOG =  LoggerFactory.getLogger(CustomErrorDecoder.class);
	private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

	@Override
	public Exception decode(String methodKey, Response response) {
		Response.Body responseBody = response.body();
		HttpStatus responseStatus = HttpStatus.valueOf(response.status());
		Exception defaultException = defaultDecoder.decode(methodKey, response);
		// Requirement 1: log error first and include response body
		try {
			LOG.error(
				"Got {} response from {}, response body: {}",
				response.status(),
				methodKey,
				IOUtils.toString(responseBody.asReader(Charset.defaultCharset()))
			);
		}
		catch (IOException e){
			LOG.error(
				"Got {} response from {}, response body could not be read",
				response.status(),
				methodKey
			);
		}
		if (responseStatus.is4xxClientError()) {
			// Requirement 4: return 500 on client error
			return new RestApiClientException(
				"Client error " + response.status() + " from calling posts api",
				defaultException
			);
		}
		return defaultException;
	}
}


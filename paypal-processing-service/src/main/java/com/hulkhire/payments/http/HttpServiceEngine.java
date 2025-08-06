package com.hulkhire.payments.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import com.hulkhire.payments.constants.ErrorCodeEnum;
import com.hulkhire.payments.exceptions.ProcessingServiceException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HttpServiceEngine {

	private RestClient restClient;

	public HttpServiceEngine(RestClient restClient) {
		this.restClient = restClient;
	}
	


	@CircuitBreaker(name = "payment-processing-service", fallbackMethod = "fallbackProcessPayment")
	public ResponseEntity<String> makeHttpCall(HttpRequest httpRequest) {
		log.info("ACTUAL method body executed");

		try {
			log.info("Inside HttpServiceEngine.makeHttpCall() | Request: {}", httpRequest);

			ResponseEntity<String> responseEntity = restClient.method(httpRequest.getHttpmethod())
					.uri(httpRequest.getUrl())
					.headers(httpHeaders -> httpHeaders
					.addAll(httpRequest.getHeaders()))
					.body(httpRequest.getRequestBody())
					.retrieve().toEntity(String.class);

			log.info("ResponseEntity from PayPal: {}", responseEntity);
			return responseEntity;

		} catch (HttpServerErrorException | HttpClientErrorException e) {
			HttpStatus status = (HttpStatus) e.getStatusCode();
			log.error("HTTP error occurred | StatusCode: {}", status);

			if (status.is5xxServerError()) {
				log.error("5xx server error occurred: {}", status);
				throw new ProcessingServiceException(ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getCode(),
						ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL.getMessage(), status);
			} else if (status.is4xxClientError()) {
				log.error("4xx client error occurred: {}", status);
				throw new ProcessingServiceException(ErrorCodeEnum.RESOURCE_NOT_FOUND.getCode(), // Make sure this
																									// exists in your
																									// ErrorCodeEnum
						ErrorCodeEnum.RESOURCE_NOT_FOUND.getMessage(), status);
			}

			throw new ProcessingServiceException(ErrorCodeEnum.GENERIC_ERROR.getCode(),
					ErrorCodeEnum.GENERIC_ERROR.getMessage(), status);
		} catch (Exception e) {
			log.error("Unexpected111111111111 exception during HTTP call");
			throw new ProcessingServiceException(ErrorCodeEnum.GENERIC_ERROR.getCode(),
					ErrorCodeEnum.GENERIC_ERROR.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

   public ResponseEntity<String> fallbackProcessPayment(HttpRequest httpRequest, Throwable t) {
		// Handle fallback logic here
		log.error("Fallback method called due to: {}", t.getMessage());
		throw new ProcessingServiceException(ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL_PROVIDER.getCode(),
			ErrorCodeEnum.UNABLE_TO_CONNECT_PAYPAL_PROVIDER.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		
	

	}
	


}
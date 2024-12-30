package com.service.account.exceptions;

import com.service.account.entity_exception.ErrorObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice 
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorObject> handleExpenseNotFoundException(ResourceNotFoundException ex, WebRequest request) {
		
		ErrorObject errorObject = new ErrorObject();
		
		errorObject.setStatusCode(HttpStatus.NOT_FOUND.value());
		
		errorObject.setMessage(ex.getMessage());
		
		errorObject.setTimestamp(new Date());
		
		return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorObject> handleMethodArgumentMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) {

		ErrorObject errorObject = new ErrorObject();

		errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());

		errorObject.setMessage(ex.getMessage());

		errorObject.setTimestamp(new Date());

		return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
	}
	

	
	@ExceptionHandler(CustomerAlreadyExistsException.class)
	public ResponseEntity<ErrorObject> handleItemExistsException(CustomerAlreadyExistsException ex, WebRequest request) {
		
		ErrorObject errorObject = new ErrorObject();
		
		errorObject.setStatusCode(HttpStatus.CONFLICT.value());
		
		errorObject.setMessage(ex.getMessage());
		
		errorObject.setTimestamp(new Date());
		
		return new ResponseEntity<>(errorObject, HttpStatus.CONFLICT);
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		// Map field errors with their messages
		Map<String, String> errors = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.collect(Collectors.toMap(
						fieldError -> fieldError.getField(),
						fieldError -> fieldError.getDefaultMessage(),
						(existing, replacement) -> existing // Handle duplicate keys
				));

		Map<String, Object> response = new HashMap<>();
		response.put("statusCode", HttpStatus.BAD_REQUEST.value());
		response.put("messages", errors); // Include validation messages
		response.put("timestamp", new Date());

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

		@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorObject> handleGeneralException(Exception ex, WebRequest request) {

		ErrorObject errorObject = new ErrorObject();

		errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

		errorObject.setMessage(ex.getMessage());

		errorObject.setTimestamp(new Date());

		return new ResponseEntity<ErrorObject>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}



















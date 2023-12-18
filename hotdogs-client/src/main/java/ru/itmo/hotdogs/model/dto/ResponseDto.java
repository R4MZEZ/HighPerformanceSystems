package ru.itmo.hotdogs.model.dto;

import java.io.Serializable;
import org.springframework.http.HttpStatus;


public record ResponseDto<T>(T body, Throwable error, HttpStatus code) implements Serializable {
}

package ru.itmo.userservice.model.dto;

import io.swagger.v3.oas.annotations.Hidden;
import java.io.Serializable;
import org.springframework.http.HttpStatus;


@Hidden
public record ResponseDto<T>(T body, Throwable error, HttpStatus code) implements Serializable {
}

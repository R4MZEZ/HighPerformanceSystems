package ru.itmo.hotdogs.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class NewDogInterestDto {
	Integer interestId;
	@Range(min=1, max=10)
	Integer level;
}

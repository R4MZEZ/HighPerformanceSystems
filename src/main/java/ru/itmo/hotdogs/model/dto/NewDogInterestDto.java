package ru.itmo.hotdogs.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewDogInterestDto {
	String interestName;
	@Range(min=1, max=10)
	Integer level;
}

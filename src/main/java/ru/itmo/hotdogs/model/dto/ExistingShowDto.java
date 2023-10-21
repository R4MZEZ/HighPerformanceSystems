package ru.itmo.hotdogs.model.dto;

import java.util.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistingShowDto {

	private Date date;
	private Long prize;
	private Set<String> allowed_breeds;
	private String winner;
}

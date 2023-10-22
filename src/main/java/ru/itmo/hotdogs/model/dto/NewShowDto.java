package ru.itmo.hotdogs.model.dto;

import java.sql.Date;
import java.util.Set;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewShowDto {

	@Future
	private Date date;
	@Min(0)
	private Long prize;
	private Set<Integer> allowed_breeds;
}

package ru.itmo.hotdogs.model.dto;

import java.sql.Timestamp;
import java.util.Set;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewShowDto {

	@Future
	private Timestamp date;
	@Min(0)
	private Long prize;
	@NotEmpty
	private Set<String> allowed_breeds;
}

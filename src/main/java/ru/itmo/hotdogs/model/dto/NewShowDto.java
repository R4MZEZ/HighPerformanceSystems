package ru.itmo.hotdogs.model.dto;

import java.sql.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewShowDto {

	private Date date;
	private Long prize;
	private Set<Integer> allowed_breeds;
}

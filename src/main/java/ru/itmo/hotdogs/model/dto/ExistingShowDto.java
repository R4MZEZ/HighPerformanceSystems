package ru.itmo.hotdogs.model.dto;

import java.sql.Timestamp;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExistingShowDto {

	private Timestamp date;
	private Long prize;
	private Set<String> allowed_breeds;
	private String winner;
}

package ru.itmo.hotdogs.model.dto;

import java.sql.Date;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.hotdogs.model.entity.BreedEntity;

@Data
@NoArgsConstructor
public class ShowDto {

	private Date date;
	private Long prize;

	private Set<Integer> allowed_breeds;

}
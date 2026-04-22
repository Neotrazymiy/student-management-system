package spring.dto;

import java.util.List;
import java.util.UUID;

import lombok.Value;

@Value
public class RoleIdsDto {

	private List<UUID> roleIds;
}

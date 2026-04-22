package spring.mapper;

import java.util.Optional;
import java.util.UUID;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import spring.dto.UserAddEditDto;
import spring.model.Role;
import spring.model.User;
import spring.repository.RoleRepository;

@Mapper(componentModel = "spring", uses = RoleAddEditMapper.class)
public abstract class UserAddEditMapper {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected RoleRepository roleRepository;

	@Mapping(target = "id", ignore = true)
	public abstract User toEntity(UserAddEditDto dto);

	@Mapping(target = "roleIds", source = "roles", qualifiedByName = "roleToId")
	public abstract UserAddEditDto toDto(User entity);

	@Named("updateUser")
	public void updateUser(UserAddEditDto dto, @MappingTarget User entity) {
		updateEntityFromDto(dto, entity);
	}

	@Mapping(target = "roles", ignore = true)
	@Mapping(target = "passwordHash", ignore = true)
	@Mapping(target = "id", ignore = true)
	public abstract void updateEntityFromDto(UserAddEditDto dto, @MappingTarget User entity);

	@AfterMapping
	protected void afterMapping(UserAddEditDto dto, @MappingTarget User user) {
		user.setEnabled(dto.getEnabled() != null && dto.getEnabled());

		Optional.ofNullable(dto.getPasswordHash()).filter(StringUtils::hasText).map(passwordEncoder::encode)
				.ifPresent(user::setPasswordHash);

	}

	@Named("roleToId")
	protected UUID map(Role role) {
		return role == null ? null : role.getId();
	}

}

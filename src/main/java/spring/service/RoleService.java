package spring.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.auxiliaryObjects.HelpsMethod;
import spring.dto.RoleAddEditDto;
import spring.dto.RoleReadDto;
import spring.exception.DeleteException;
import spring.mapper.RoleAddEditMapper;
import spring.mapper.RoleReadMapper;
import spring.model.Role;
import spring.model.User;
import spring.repository.RoleRepository;
import spring.repository.UserRepository;

@Service
@AllArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;
	private final RoleAddEditMapper roleAddEditMapper;
	private final RoleReadMapper roleReadMapper;
	private final UserRepository userRepository;
	private HelpsMethod helpsMethod;

	@Transactional
	public RoleReadDto addRole(RoleAddEditDto roleDto) {
		return Optional.of(roleDto).map(dto -> {
			Role role = roleAddEditMapper.toEntity(roleDto);
			return role;
		}).map(roleRepository::save).map(roleReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<RoleReadDto> getRoleById(UUID id) {
		return Optional.ofNullable(roleRepository.findById(id).map(roleReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<RoleReadDto> getRoleByName(String name) {
		return roleRepository.findByName(name).map(roleReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<RoleReadDto> getAllRoles() {
		return roleRepository.findAll().stream().map(roleReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<RoleReadDto> getAllPageRoles(Pageable pageable) {
		return roleRepository.findAll(pageable).map(roleReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<RoleReadDto> getRolesById(List<UUID> id) {
		return roleRepository.findAllById(id).stream().map(roleReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional
	public Optional<RoleReadDto> updateRole(UUID id, RoleAddEditDto updateRole) {
		return roleRepository.findById(id).map(entity -> {
			boolean repeatingName = entity.getName().equals(updateRole.getName());
			if (!repeatingName) {
				return entity;
			}
			roleAddEditMapper.updateEntityFromDto(updateRole, entity);
			return roleRepository.saveAndFlush(entity);
		}).map(roleReadMapper::toDto);
	}

	@Transactional
	public void deleteRoleById(UUID id) {
		Role role = roleRepository.findById(id).orElseThrow(() -> new DeleteException("Роль не найдена"));
		List<User> users = userRepository.findAllByRolesContains(role);
		users.stream().peek(user -> helpsMethod.correctRolesAfterDelete(user, role)).forEach(userRepository::save);
		roleRepository.delete(role);
	}

}

package spring.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import spring.dto.RoleReadDto;
import spring.dto.UserAddEditDto;
import spring.dto.UserReadDto;
import spring.mapper.RoleAddEditMapper;
import spring.mapper.UserAddEditMapper;
import spring.mapper.UserReadMapper;
import spring.model.Role;
import spring.model.RoleElement;
import spring.model.User;
import spring.repository.RoleRepository;
import spring.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserReadMapper userReadMapper;
	private final UserAddEditMapper userAddEditMapper;
	private final RoleAddEditMapper roleAddEditMapper;

	@Transactional
	public UserReadDto addUser(UserAddEditDto userDto) {
		return Optional.of(userDto).map(dto -> {
			User user = userAddEditMapper.toEntity(userDto);
			if (dto.getRoles() == null || dto.getRoles().isEmpty()) {
				user.setRoles(Arrays.asList(roleRepository.findByName(RoleElement.ROLE_QUEST.name())
						.orElseThrow(() -> new RuntimeException("Такой роли нет."))));
			}
			return user;
		}).map(userRepository::save).map(userReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<UserReadDto> getUserById(UUID id) {
		return Optional.ofNullable(userRepository.findById(id).map(userReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<UserReadDto> getUserByName(String userName) {
		return userRepository.findByUserName(userName).map(userReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public Optional<UserReadDto> getUserByEmail(String email) {
		return userRepository.findByEmail(email).map(userReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<UserReadDto> getAllUsers() {
		return userRepository.findAll().stream().map(userReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<UserReadDto> getAllPageUsers(Pageable pageable) {
		return userRepository.findAll(pageable).map(userReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public Page<UserReadDto> getUsersExcludingRoles(List<RoleReadDto> rolesToExclude, Pageable pageable) {
		List<Role> roles = rolesToExclude.stream()
				.map(id -> roleRepository.findById(id.getId()).orElseThrow(() -> new RuntimeException()))
				.collect(Collectors.toList());
		return userRepository.findAllByRolesNotIn(roles, pageable).map(userReadMapper::toDto);
	}

	@Transactional
	public Optional<UserReadDto> updateUser(UUID id, UserAddEditDto updateUserDto) {
		return userRepository.findById(id).map(entity -> {
			List<Role> newRoles;
			if (updateUserDto.getRoles() == null || updateUserDto.getRoles().isEmpty()) {
				newRoles = new ArrayList<>(
						Collections.singletonList(roleRepository.findByName(RoleElement.ROLE_QUEST.name())
								.orElseThrow(() -> new RuntimeException("Такой роли нет"))));
			} else {
				entity.getRoles().clear();
				newRoles = updateUserDto.getRoles().stream().map(roleDto -> {
					if (roleDto.getName() != null) {
						return roleRepository.findByName(roleDto.getName())
								.orElseThrow(() -> new RuntimeException("Роль не найдена: " + roleDto.getName()));
					}
					return roleAddEditMapper.toEntity(roleDto);
				}).collect(Collectors.toList());
			}
			entity.setRoles(newRoles);
			userAddEditMapper.updateEntityFromDto(updateUserDto, entity);
			return userRepository.saveAndFlush(entity);
		}).map(userReadMapper::toDto);
	}

	@Transactional
	public boolean deleteUserById(UUID id) {
		return userRepository.findById(id).map(entity -> {
			userRepository.delete(entity);
			return true;
		}).orElse(false);

	}
}

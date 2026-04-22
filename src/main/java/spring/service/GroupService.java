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
import spring.dto.GroupAddEditDto;
import spring.dto.GroupReadDto;
import spring.exception.DeleteException;
import spring.mapper.GroupAddEditMapper;
import spring.mapper.GroupReadMapper;
import spring.model.Group;
import spring.repository.DepartmentRepository;
import spring.repository.GroupRepository;

@Service
@AllArgsConstructor
public class GroupService {

	private final GroupRepository groupRepository;
	private final GroupAddEditMapper groupAddEditMapper;
	private final GroupReadMapper groupReadMapper;
	private final DepartmentRepository departmentRepository;

	@Transactional
	public GroupReadDto addGroup(GroupAddEditDto groupDto) {
		return Optional.of(groupDto).map(dto -> {
			Group group = groupAddEditMapper.toEntity(groupDto);
			departmentRepository.findById(groupDto.getDepartmentId()).ifPresent(group::setDepartment);
			return group;
		}).map(groupRepository::save).map(groupReadMapper::toDto).orElseThrow(() -> new RuntimeException());
	}

	@Transactional(readOnly = true)
	public Optional<GroupReadDto> getGroupById(UUID id) {
		return Optional.ofNullable(groupRepository.findById(id).map(groupReadMapper::toDto)
				.orElseThrow(() -> new RuntimeException("Такого id нет. " + id)));
	}

	@Transactional(readOnly = true)
	public Optional<GroupReadDto> getGroupByName(String name) {
		return groupRepository.findByName(name).map(groupReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<GroupReadDto> getAllGroups() {
		return groupRepository.findAll().stream().map(groupReadMapper::toDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<GroupReadDto> getAllPageGroups(Pageable pageable) {
		return groupRepository.findAll(pageable).map(groupReadMapper::toDto);
	}

	@Transactional(readOnly = true)
	public List<GroupReadDto> getGroupsByIds(List<UUID> groupIds) {
		return groupIds != null
				? groupRepository.findByIdIn(groupIds).stream().map(groupReadMapper::toDto).collect(Collectors.toList())
				: null;
	}

	@Transactional(readOnly = true)
	public Page<GroupReadDto> getPageGroupsByIds(List<UUID> groupIds, Pageable pageable) {
		if (groupIds == null || groupIds.isEmpty()) {
			return Page.empty(pageable);
		}
		return groupRepository.findByIdIn(groupIds, pageable).map(groupReadMapper::toDto);
	}

	@Transactional
	public Optional<GroupReadDto> updateGroup(UUID id, GroupAddEditDto updateGroup) {
		return groupRepository.findById(id).map(entity -> {
			boolean repeatingName = entity.getName().equals(updateGroup.getName());
			boolean recurringDepartment = entity.getDepartment().getId().equals(updateGroup.getDepartmentId());
			boolean changed = false;
			if (!repeatingName) {
				changed = true;
			}
			if (!recurringDepartment) {
				departmentRepository.findById(updateGroup.getDepartmentId()).ifPresent(entity::setDepartment);
				changed = true;
			}
			if (!changed) {
				return entity;
			}

			groupAddEditMapper.updateEntityFromDto(updateGroup, entity);
			return groupRepository.save(entity);
		}).map(groupReadMapper::toDto);
	}

	@Transactional
	public void deleteGroupById(UUID id) {
		Group group = groupRepository.findById(id)
				.orElseThrow(() -> new DeleteException("Group с id " + id + " не найден."));
		if (!group.getStudents().isEmpty()) {
			throw new DeleteException("Уберите связь сущностьи(ей) Student, ссылающихся на этот объекта Group.");
		}
		groupRepository.delete(group);
		groupRepository.flush();
	}

	@Transactional(readOnly = true)
	public List<GroupReadDto> getGroupsWithoutLesson(List<UUID> lessonIds) {
		List<GroupReadDto> dtos = groupRepository.findGroupsNotInLessons(lessonIds).stream().map(groupReadMapper::toDto)
				.collect(Collectors.toList());
		if (lessonIds == null || dtos == null || lessonIds.isEmpty() || dtos.isEmpty()) {
			return getAllGroups();
		}
		return dtos;
	}
}

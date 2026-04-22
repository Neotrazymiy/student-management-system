package spring.auxiliaryObjects;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import spring.model.Role;
import spring.model.RoleElement;
import spring.model.User;
import spring.repository.RoleRepository;

@AllArgsConstructor
@Component
public class HelpsMethod {

	private final RoleRepository roleRepository;

	public void set_ROLE_QUEST_IfRoleNull(User user) {
		List<Role> roles = user.getRoles();
		if (roles == null || roles.isEmpty()) {
			user.getRoles().add(roleRepository.findByName(RoleElement.ROLE_QUEST.name())
					.orElseThrow(() -> new RuntimeException("ROLE_QUEST не найдена")));
		}
	}

	public void correctRolesAfterDelete(User user, Role removedRole) {
		user.getRoles().removeIf(role -> role.equals(removedRole));
		if (user.getRoles().isEmpty()) {
			Role questRole = roleRepository.findByName(RoleElement.ROLE_QUEST.name())
					.orElseThrow(() -> new RuntimeException("ROLE_QUEST не найдена"));
			user.getRoles().add(questRole);
		}
	}

}

package spring.auxiliaryObjects;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.model.RoleElement;
import spring.model.User;
import spring.repository.RoleRepository;
import spring.repository.UserRepository;

@Component
@AllArgsConstructor
public class OAuthUserServiceHelper {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	@Transactional
	public User createUserFromGoogle(String email, String givenName, String familyName) {
		Optional<User> findUser = userRepository.findByEmail(email);
		if (findUser.isPresent()) {
			return findUser.get();
		}
		User user = new User();
		user.setUserName(givenName + "_" + familyName);
		user.setFirstName(givenName);
		user.setLastName(familyName);
		user.setEmail(email);
		user.setPasswordHash(UUID.randomUUID().toString());
		user.setRoles(Arrays.asList(roleRepository.findByName(RoleElement.ROLE_QUEST.name())
				.orElseThrow(() -> new RuntimeException("Role not found"))));
		return userRepository.save(user);
	}
}

package spring.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import spring.config.CustomUserDetails;
import spring.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CastomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		return userRepository.findByUserName(userName).map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("Такого пользователя нет." + userName));
	}

	@Transactional
	public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email).map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("Такого пользователя нет." + email));
	}
}

package spring.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import spring.repository.UserRepository;

@Service
@AllArgsConstructor
public class LoginAttemptService {

	private static final int MAX_ATTEMPT = 15;
	private static final Duration LOCK_TIME = Duration.ofMinutes(10);

	private final UserRepository userRepository;

	@Transactional
	public void onSuccess(String userName) {
		userRepository.findByUserName(userName).ifPresent(u -> {
			u.setFailedAttempts(0);
			u.setBlockingTime(null);
		});
	}

	@Transactional
	public void onFailure(String userName) {
		userRepository.findByUserName(userName).ifPresent(u -> {
			int attempts = u.getFailedAttempts() + 1;
			u.setFailedAttempts(attempts);

			if (attempts >= MAX_ATTEMPT) {
				u.setBlockingTime(LocalDateTime.now().plus(LOCK_TIME));
			}
		});
	}

	@Transactional
	public long getDelayMillis(String userName) {
		return userRepository.findByUserName(userName).map(user -> {
			int attempts = user.getFailedAttempts();
			long delay = attempts * 500L;
			return Math.min(delay, 3000L);
		}).orElse(500L);
	}

}

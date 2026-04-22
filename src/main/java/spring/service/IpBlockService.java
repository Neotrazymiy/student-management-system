package spring.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class IpBlockService {

	private static final int MAX_ATTEMPTS = 50;
	private static final int BLOCK_MINUTES = 5;

	private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
	private final Map<String, LocalDateTime> blockedUtill = new ConcurrentHashMap<>();

	public void onFilter(String ip) {
		if (ip == null) {
			return;
		}
		int count = attempts.getOrDefault(ip, 0) + 1;
		attempts.put(ip, count);

		if (count >= MAX_ATTEMPTS) {
			blockedUtill.put(ip, LocalDateTime.now().plusMinutes(BLOCK_MINUTES));
			attempts.remove(ip);
		}
	}

	public void onSuccess(String ip) {
		if (ip == null) {
			return;
		}
		attempts.remove(ip);
	}

	public boolean isBlocked(String ip) {
		if (ip == null) {
			return false;
		}
		LocalDateTime until = blockedUtill.get(ip);
		if (until == null) {
			return false;
		}

		if (until.isAfter(LocalDateTime.now())) {
			return true;
		}
		blockedUtill.remove(ip);
		return false;
	}

}

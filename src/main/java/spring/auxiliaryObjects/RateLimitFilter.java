package spring.auxiliaryObjects;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import spring.service.IpBlockService;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

	private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
	private final IpBlockService ipBlockService;

	public RateLimitFilter(IpBlockService ipBlockService) {
		this.ipBlockService = ipBlockService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String ip = clientIp(request);
		if (ipBlockService.isBlocked(ip)) {
			response.setStatus(403);
			response.setContentType("text/plain; charset=UTF-8");
			response.getWriter().write("IP temporarily blocked");
			return;
		}

		String path = request.getRequestURI();
		String method = request.getMethod();

		if ("POST".equals(method) && "/perform_login".equals(path)) {
			String key = "LOGIN_IP: " + clientIp(request);
			Bucket bucket = buckets.computeIfAbsent(key, k -> loginBucket());
			try {
				if (!tryConsumeOr429(bucket, response)) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if ("POST".equals(method) && (path.startsWith("/admin") || path.startsWith("/methodist"))) {
			String key = "LESSON: " + userOrIpKey(request);
			Bucket bucket = buckets.computeIfAbsent(key, k -> lessonBucket());
			try {
				if (!tryConsumeOr429(bucket, response)) {
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		filterChain.doFilter(request, response);
	}

	private Bucket loginBucket() {
		@SuppressWarnings("deprecation")
		Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
		return Bucket.builder().addLimit(limit).build();
	}

	private Bucket lessonBucket() {
		@SuppressWarnings("deprecation")
		Bandwidth limit = Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
		return Bucket.builder().addLimit(limit).build();
	}

	private boolean tryConsumeOr429(Bucket bucket, HttpServletResponse response) throws Exception {
		if (bucket.tryConsume(1)) {
			return true;
		}
		long waitSeconds = Math.max(1, bucket.estimateAbilityToConsume(1).getNanosToWaitForRefill() / 1_000_000_000L);
		response.setStatus(429);
		response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(waitSeconds));
		response.setContentType("text/plain; charset=UTF-8");
		response.getWriter().write("Too many request");
		return false;
	}

	private String userOrIpKey(HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
			return "USER: " + auth.getName();
		}
		return "IP: " + clientIp(request);
	}

	private String clientIp(HttpServletRequest request) {
		String xff = request.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isEmpty()) {
			return xff.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

}

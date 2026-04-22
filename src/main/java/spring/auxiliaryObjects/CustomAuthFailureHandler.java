package spring.auxiliaryObjects;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import spring.service.IpBlockService;
import spring.service.LoginAttemptService;

@Component
@AllArgsConstructor
public class CustomAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final LoginAttemptService loginAttemptService;
	private final IpBlockService ipBlockService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String userName = request.getParameter("username");
		String ip = getClientIp(request);
		if (userName != null) {
			loginAttemptService.onFailure(userName);
		}
		ipBlockService.onFilter(ip);
		long delay = loginAttemptService.getDelayMillis(userName);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		if (exception instanceof LockedException) {
			setDefaultFailureUrl("/login?locked=true");
		} else {
			setDefaultFailureUrl("/login?error=true");
		}
		super.onAuthenticationFailure(request, response, exception);
	}

	private String getClientIp(HttpServletRequest request) {
		String xff = request.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isEmpty()) {
			return xff.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

}

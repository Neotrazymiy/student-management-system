package spring.auxiliaryObjects;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import spring.service.IpBlockService;
import spring.service.LoginAttemptService;

@Component
@AllArgsConstructor
public class CustomAuthSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final LoginAttemptService loginAttemptService;
	private final IpBlockService ipBlockService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		loginAttemptService.onSuccess(authentication.getName());
		ipBlockService.onSuccess(getClientIp(request));
		setAlwaysUseDefaultTargetUrl(true);
		setDefaultTargetUrl("/home");
		super.onAuthenticationSuccess(request, response, authentication);
	}

	private String getClientIp(HttpServletRequest request) {
		String xff = request.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isEmpty()) {
			return xff.split(",")[0].trim();
		}
		return request.getRemoteAddr();
	}

}

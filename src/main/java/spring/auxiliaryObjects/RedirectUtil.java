package spring.auxiliaryObjects;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class RedirectUtil {

	public static void keepPageSize(RedirectAttributes attributes, RequestPageSizeData data) {
		attributes.addAttribute("page", data.getPage());
		attributes.addAttribute("size", data.getSize());
	}

}

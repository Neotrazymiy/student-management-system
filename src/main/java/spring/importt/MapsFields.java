package spring.importt;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MapsFields {

	public Map<String, Object> getUniversityField(ImportRecord record) {
		Map<String, Object> fields = new HashMap<>();
		fields.put("name", record.get("name"));
		return fields;
	}

}

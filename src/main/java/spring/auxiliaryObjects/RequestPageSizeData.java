package spring.auxiliaryObjects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestPageSizeData {

	private Integer page = 0;
	private Integer size = 10;

	public RequestPageSizeData(int page, int size) {
		this.page = Math.max(page, 0);
		this.size = size <= 0 ? 10 : size;
	}

}

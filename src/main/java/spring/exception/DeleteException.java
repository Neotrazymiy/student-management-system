package spring.exception;

public class DeleteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DeleteException(String massage) {
		super(massage);
	}

}

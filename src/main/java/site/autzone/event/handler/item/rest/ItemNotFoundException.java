package site.autzone.event.handler.item.rest;

public class ItemNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ItemNotFoundException() {
		super();
	}
	
	public ItemNotFoundException(String message) {
		super(message);
	}
}

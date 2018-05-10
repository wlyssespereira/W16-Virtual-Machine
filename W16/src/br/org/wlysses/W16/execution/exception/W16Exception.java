package br.org.wlysses.W16.execution.exception;

public class W16Exception {
	private TipeException exception;

	public W16Exception(TipeException exception) {
		this.exception= exception;
		printException();
	}

	public TipeException getException() {
		return exception;
	}
	
	public void printException(){
		System.err.println("Exception runtime: " + exception.toString());
	}
}

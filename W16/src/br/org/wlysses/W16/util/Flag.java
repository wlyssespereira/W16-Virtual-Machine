package br.org.wlysses.W16.util;

public enum Flag {

	_1(false),
	_0(true);

	private boolean b;

	Flag(boolean b){
		this.b = b;
	}
	public boolean booleanValue(){
		return b;
	}
}
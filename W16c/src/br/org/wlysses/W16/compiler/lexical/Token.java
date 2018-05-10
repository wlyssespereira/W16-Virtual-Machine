package br.org.wlysses.W16.compiler.lexical;

public class Token {

	private TipoToken tipoToken;
	private String valor;

	public Token(TipoToken tipoToken, String valor){
		this.setTipoToken(tipoToken);
		this.setValor(valor);
	}

	public Token(Token token){
		setTipoToken(token.getTipoToken());
		setValor(token.getValor());
	}

	public TipoToken getTipoToken() {
		return tipoToken;
	}

	public void setTipoToken(TipoToken tipoToken) {
		this.tipoToken = tipoToken;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj.getClass() != Token.class)
			return false;
		else
			return (((Token)obj).getTipoToken() == tipoToken) && (((Token)obj).getValor().equalsIgnoreCase(valor));
	}

	@Override
	public String toString() {
		return valor + "\t" + tipoToken;
	}
}
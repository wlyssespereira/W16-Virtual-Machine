package br.org.wlysses.W16.compiler.syntax;

import java.util.ArrayList;
import java.util.List;

import br.org.wlysses.W16.compiler.lexical.Token;

public class Noh {

	private Token token;
	private List<Noh> nohs = new ArrayList<Noh>();
	private TipoNoh tipoNoh;

	public Noh(Token token){
		this.token = token;
		this.tipoNoh = TipoNoh.TOKEN;
	}
	public Noh(TipoNoh tipoNoh){
		this.tipoNoh = tipoNoh;
	}
	public void add(Token token){
		this.token = token;
	}	
	public Token getToken() {
		return token;
	}
	public void addFilho(Noh filho){
		nohs.add(filho);
	}
	public Noh getFilho(int index) {
		return nohs.get(index);
	}
	public List<Noh> getFilhos() {
		return nohs;
	}
	public TipoNoh getTipoNoh() {
		return tipoNoh;
	}
	public void setTipoNoh(TipoNoh tipoNoh) {
		this.tipoNoh = tipoNoh;
	}

	@Override
	public String toString() {
		return mostraArvore();
	}

	private String mostraArvore() {
		String espacamento = "";
		espacamento = mostraNo(this, espacamento);
		return espacamento;
	}

	private String mostraNo(Noh noh, String espacamento) {
		String string = mostraTipoNo(noh, espacamento);
		if (noh != null && noh.getFilhos() != null) {
			for (Noh node : noh.getFilhos()) {
				string += mostraNo(node, espacamento + "    ");
			}
		}
		return string;
	}

	private String mostraTipoNo(Noh no, String espacamento) {
		if (no.getTipoNoh() == TipoNoh.TOKEN) {
			return (espacamento + no.getToken().getValor()) + "\n";
		} else {
			return (espacamento + no.getTipoNoh().toString())+ "\n";
		}
	}

}

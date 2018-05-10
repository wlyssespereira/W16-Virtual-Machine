package br.org.wlysses.W16.compiler.lexical;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AnalisadorLexico {

	private List<Token> lstTokens;
	private List<String> erros = new ArrayList<String>();

	/**
	 * Efetua a analise e cria a Lista de Tokens.
	 * @param lstStrings
	 * @return boolean
	 */
	public boolean analisar(List<String> lstStrings) {
		lstTokens = null;
		List<Token> auxTokens = new ArrayList<Token>();

		for (Iterator<String> iterator = lstStrings.iterator(); iterator.hasNext() && erros.isEmpty();) {
			String aux = (String) iterator.next();
			Token auxToken = getToken(aux);

			if(auxToken.getTipoToken() == TipoToken.INDEFINIDO)
				erros.add("Erro: palavra '" + aux + "' nao definida.");

			if(auxToken.getTipoToken() != TipoToken.COMENTARIO)
				auxTokens.add(auxToken);
		}

		lstTokens = auxTokens;

		return erros.isEmpty();
	}

	/**
	 * Retorna um token para o valor que é passado.
	 * @param valor
	 * @return {@link Token}
	 */
	private Token getToken(String valor) {

		TipoToken tipoToken = getTipo(valor);
		Token token = new Token(tipoToken, valor);

		return token;
	}

	/**
	 * Retorna o tipo do token.
	 * @param valor
	 * @return TipoToken
	 */
	private TipoToken getTipo(String valor) {

		//Palavras reservadas
		if(valor.equalsIgnoreCase(".var"))		return TipoToken.PALAVRA_RESERVADA;
		if(valor.equalsIgnoreCase(".program"))	return TipoToken.PALAVRA_RESERVADA;
		if(valor.equalsIgnoreCase(".end"))		return TipoToken.PALAVRA_RESERVADA;


		//Variaveis
		if(
				(valor.length() >= 2) &&
				(valor.charAt(0) == '$') &&
				!Character.isDigit(valor.charAt(1))
				)								return TipoToken.VARIAVEL;


		//Rotulos
		if(
				(valor.length() >= 2) &&
				(valor.charAt(0) == ':') &&
				!Character.isDigit(valor.charAt(1))
				)								return TipoToken.ROTULO;


		//Operadores
		if(valor.equalsIgnoreCase("MOV"))		return TipoToken.OPERADOR;

		if(valor.equalsIgnoreCase("ADD"))		return TipoToken.OPERADOR;
		if(valor.equalsIgnoreCase("SUB"))		return TipoToken.OPERADOR;
		if(valor.equalsIgnoreCase("MUL"))		return TipoToken.OPERADOR;
		if(valor.equalsIgnoreCase("DIV"))		return TipoToken.OPERADOR;

		if(valor.equalsIgnoreCase("CMP"))		return TipoToken.OPERADOR;


		//MBPSP (mov bp sp)
		if(valor.equalsIgnoreCase("MBPSP"))		return TipoToken.MBPSP;


		//Loads e Stories
		if(valor.equalsIgnoreCase("ld0"))		return TipoToken.LOAD;
		if(valor.equalsIgnoreCase("ld1"))		return TipoToken.LOAD;
		if(valor.equalsIgnoreCase("ld2"))		return TipoToken.LOAD;
		if(valor.equalsIgnoreCase("ld3"))		return TipoToken.LOAD;
		if(valor.equalsIgnoreCase("ld4"))		return TipoToken.LOAD;
		if(valor.equalsIgnoreCase("ld5"))		return TipoToken.LOAD;
		if(valor.equalsIgnoreCase("ld6"))		return TipoToken.LOAD;
		if(valor.equalsIgnoreCase("ld7"))		return TipoToken.LOAD;

		if(valor.equalsIgnoreCase("st0"))		return TipoToken.STORE;
		if(valor.equalsIgnoreCase("st1"))		return TipoToken.STORE;
		if(valor.equalsIgnoreCase("st2"))		return TipoToken.STORE;
		if(valor.equalsIgnoreCase("st3"))		return TipoToken.STORE;
		if(valor.equalsIgnoreCase("st4"))		return TipoToken.STORE;
		if(valor.equalsIgnoreCase("st5"))		return TipoToken.STORE;
		if(valor.equalsIgnoreCase("st6"))		return TipoToken.STORE;
		if(valor.equalsIgnoreCase("st7"))		return TipoToken.STORE;


		//Load de BP
		if(valor.equalsIgnoreCase("ldbp0"))		return TipoToken.LDBPX;		
		if(valor.equalsIgnoreCase("ldbp1"))		return TipoToken.LDBPX;		
		if(valor.equalsIgnoreCase("ldbp2"))		return TipoToken.LDBPX;		
		if(valor.equalsIgnoreCase("ldbp3"))		return TipoToken.LDBPX;		
		if(valor.equalsIgnoreCase("ldbp4"))		return TipoToken.LDBPX;		
		if(valor.equalsIgnoreCase("ldbp5"))		return TipoToken.LDBPX;		
		if(valor.equalsIgnoreCase("ldbp6"))		return TipoToken.LDBPX;		
		if(valor.equalsIgnoreCase("ldbp7"))		return TipoToken.LDBPX;		


		//Valores numericos
		if(isNumber(valor))						return TipoToken.VALOR_NUMERAL;


		//Valor hexadecimal
		if(isHexNumber(valor))					return TipoToken.VALOR_HEXADECIMAL;


		//Push e Pop
		if(valor.equalsIgnoreCase("push"))		return TipoToken.PUSH;
		if(valor.equalsIgnoreCase("pop"))		return TipoToken.POP;


		//Saltos
		if(valor.equalsIgnoreCase("JMP"))		return TipoToken.SALTOS;
		if(valor.equalsIgnoreCase("JE"))		return TipoToken.SALTOS;
		if(valor.equalsIgnoreCase("JNE"))		return TipoToken.SALTOS;
		if(valor.equalsIgnoreCase("JG"))		return TipoToken.SALTOS;
		if(valor.equalsIgnoreCase("JGE"))		return TipoToken.SALTOS;
		if(valor.equalsIgnoreCase("JL"))		return TipoToken.SALTOS;
		if(valor.equalsIgnoreCase("JLE"))		return TipoToken.SALTOS;
		if(valor.equalsIgnoreCase("JNZ"))		return TipoToken.SALTOS;
		if(valor.equalsIgnoreCase("JZ"))		return TipoToken.SALTOS;


		//Call
		if(valor.equalsIgnoreCase("call"))		return TipoToken.SALTOS;
		//Ret
		if(valor.equalsIgnoreCase("ret"))		return TipoToken.RET;


		//Registradores
		if(valor.equalsIgnoreCase("R0"))		return TipoToken.REGISTRADOR_GERAL;
		if(valor.equalsIgnoreCase("R1"))		return TipoToken.REGISTRADOR_GERAL;
		if(valor.equalsIgnoreCase("R2"))		return TipoToken.REGISTRADOR_GERAL;
		if(valor.equalsIgnoreCase("R3"))		return TipoToken.REGISTRADOR_GERAL;
		if(valor.equalsIgnoreCase("R4"))		return TipoToken.REGISTRADOR_GERAL;
		if(valor.equalsIgnoreCase("R5"))		return TipoToken.REGISTRADOR_GERAL;
		if(valor.equalsIgnoreCase("R6"))		return TipoToken.REGISTRADOR_GERAL;
		if(valor.equalsIgnoreCase("R7"))		return TipoToken.REGISTRADOR_GERAL;
		//------------------------------------------------------------------------;
		if(valor.equalsIgnoreCase("SP"))		return TipoToken.REGISTRADOR_SP;
		//------------------------------------------------------------------------;
		if(valor.equalsIgnoreCase("BP"))		return TipoToken.REGISTRADOR_BP;


		//Tipos das variaveis
		if(valor.equalsIgnoreCase("UB"))		return TipoToken.VARIAVEL_TIPO;
		if(valor.equalsIgnoreCase("DB"))		return TipoToken.VARIAVEL_TIPO;


		//Tipo string
		if(isString(valor))						return TipoToken.STRING;


		//Comentario
		if(isComentario(valor))					return TipoToken.COMENTARIO;


		//Default
		return TipoToken.INDEFINIDO;
	}

	/**
	 * Verifica se é um comentario.
	 * @param valor
	 * @return boolean
	 */
	private boolean isComentario(String valor) {
		if(valor.length() >= 2)
			return (valor.charAt(0) == '/') && (valor.charAt(1) == '/');
		return false;
	}

	/**
	 * Verifica se é uma palavra literal
	 * @param valor
	 * @return boolean
	 */
	private boolean isString(String valor) {
		if(valor.length() > 2)
			return ((valor.charAt(0) == '\'') && (valor.charAt(valor.length()-1) == '\''));
		return false;
	}

	/**
	 * Verifica se é um numero hexadecimal
	 * @param valor
	 * @return boolean
	 */
	private boolean isHexNumber(String valor){
		valor = valor.toLowerCase();

		boolean isHex = valor.length() > 2;

		isHex = isHex && ((valor.charAt(0) == '0') && (valor.charAt(1) == 'x'));

		int size = valor.length();
		for(int i = 2; (i < size) && isHex; i ++)
			isHex = isHexChar(valor.charAt(i));
		return isHex;

	}

	/**
	 * Verifica se é uma letra que compoe um hexadecimal
	 * @param c
	 * @return boolean
	 */
	private boolean isHexChar(char c){		
		if(Character.isDigit(c))
			return true;
		else{
			if(c == 'a') return true;
			if(c == 'b') return true;
			if(c == 'c') return true;
			if(c == 'd') return true;
			if(c == 'e') return true;
			if(c == 'f') return true;
		}
		return false;			
	}

	/**
	 * Verifica se é um numero inteiro sem letras na composição
	 * @param valor
	 * @return boolean
	 */
	private boolean isNumber(String valor){
		@SuppressWarnings("unused")
		int n = 0;
		try {
			n = Integer.parseInt(valor);
			return true;
		} catch (NumberFormatException numberFormatException) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Metodo que retorna a lista de tokens, após a analise lexica
	 * @return {@link List}
	 */
	public List<Token> getLstTokens() {
		return lstTokens;
	}

	/**
	 * Metodo que retorna a lista de erros, após a analise lexica
	 * @return {@link List}
	 */
	public List<String> getErros() {
		return erros;
	}

	/**
	 * Retorna a tabela de tokens da analise lexica.
	 */
	@Override
	public String toString() {
		String string = "";
		if(lstTokens != null)
			for (Iterator<Token> iterator = lstTokens.iterator(); iterator.hasNext();) {
				Token token = (Token) iterator.next();
				string += token.toString() + "\n";
			}
		else
			return "vazio";
		return string;
	}

}
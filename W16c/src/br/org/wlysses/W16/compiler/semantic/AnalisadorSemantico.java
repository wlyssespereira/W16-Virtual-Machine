package br.org.wlysses.W16.compiler.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.org.wlysses.W16.compiler.lexical.TipoToken;
import br.org.wlysses.W16.compiler.lexical.Token;
import br.org.wlysses.W16.compiler.syntax.Noh;
import br.org.wlysses.W16.compiler.syntax.TipoNoh;

public class AnalisadorSemantico {

	private Noh raiz = null;
	private Map<String, Short> rotulos = new HashMap<String, Short>();
	private List<String> erros = new ArrayList<String>();
	private List<Token> variaveis = new ArrayList<Token>();
	private int lineCode = 0;

	public AnalisadorSemantico(Noh raiz) {
		this.raiz = raiz;
	}

	public boolean analisar(){
		INICIO(raiz);
		return erros.size() == 0;
	}

	public List<String> getErros(){
		return erros;
	}

	public Map<String, Short> getRotulos() {
		return rotulos;
	}

	//	<INICIO>        ::=     <BLOCO_VAR> <BLOCO_PROGRAM> '.end'
	private Object INICIO(Noh noh) {
		BLOCO_VAR(noh.getFilho(0));
		BLOCO_PROGRAM(noh.getFilho(1));
		return null;
	}

	//	<BLOCO_VAR>     ::=     '.var' <DECLARACOES>
	private Object BLOCO_VAR(Noh noh) {
		noh.getFilho(0);
		DECLARACOES(noh.getFilho(1));
		return null;
	}

	//	<DECLARACOES>   ::=     <DECLARACAO> <DECLARACOES> | 
	private Object DECLARACOES(Noh noh) {
		if(noh.getFilhos().size() != 0){
			DECLARACAO(noh.getFilho(0));
			if(noh.getFilhos().size() > 1)
				DECLARACOES(noh.getFilho(1));
		}
		return null;
	}

	//	<DECLARACAO>    ::=     <VAR>
	private Object DECLARACAO(Noh noh) {
		Token var;
		var = ((Noh)VAR(noh.getFilho(0))).getToken();

		if(!variaveis.contains(var))
			variaveis.add(var);
		else
			erros.add("Erro: Declaracao da variavel "+ var.getValor() +" repetida.");

		return null;
	}

	//	<VAR>           ::=     '$' Identifier
	private Object VAR(Noh noh) {
		return noh.getFilho(0);
	}

	//	<BLOCO_PROGRAM> ::=     '.program' <INSTRUCOES>
	private Object BLOCO_PROGRAM(Noh noh) {
		noh.getFilho(0);
		INSTRUCOES(noh.getFilho(1));
		return null;
	}

	//	<INSTRUCOES>    ::=     <INSTRUCAO> <INSTRUCOES> | 
	private Object INSTRUCOES(Noh noh) {
		if(noh.getFilhos().size() != 0){
			INSTRUCAO(noh.getFilho(0));
			if(noh.getFilhos().size() > 1)
				INSTRUCOES(noh.getFilho(1));
		}
		return null;
	}

	//	<INSTRUCAO>     ::=     Rotulo | <OPERACAO>
	private Object INSTRUCAO(Noh noh) {
		if(noh.getFilho(0).getTipoNoh() == TipoNoh.TOKEN){
			if(noh.getFilho(0).getToken().getTipoToken() == TipoToken.ROTULO)
				if(!rotulos.containsKey(noh.getFilho(0).getToken().getValor()))
					rotulos.put((noh.getFilho(0).getToken().getValor()), (short) lineCode);
				else
					erros.add("Erro INSTRUCAO: Declaracao do rotulo "+ noh.getFilho(0).getToken().getValor() +" repetido.");

		}else
			OPERACAO(noh.getFilho(0));

		lineCode  += 3;

		return null;
	}

	//	<OPERACAO>              ::=     <OP_UNARIA> | <OP_BINARIA> <REGISTRADOR_GERAL> <PARAMETRO> | 'ret' | <LDBPX> IntegerLiteral | 'mbpsp'
	private Object OPERACAO(Noh noh) {
		if(noh.getFilho(0).getTipoNoh() == TipoNoh.OP_UNARIA)
			OP_UNARIA(noh.getFilho(0));
		else if(noh.getFilho(0).getTipoNoh() == TipoNoh.OP_BINARIA){
			OP_BINARIA(noh.getFilho(0));
			Token t1, t2;
			t1 = (Token) REGISTRADOR_GERAL(noh.getFilho(1));
			t2 = (Token) PARAMETRO(noh.getFilho(2));
			if(t2 != null)
				if(t1.equals(t2))
					erros.add("Erro OPERACAO: Operacao com registrador "+ t1.getValor() +". Eh invalido dois registradores iguais em operacoes.");
		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.TOKEN){
			//Nada a fazer.
		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.LDBPX){
			LDBPX(noh.getFilho(0));
			noh.getFilho(1);
		}else{
			erros.add("Erro: Esperado uma operacao valida.");
		}

		return null;
	}

	//	<LDBPX>                  ::=     'ldp0' | 'ldp1' | 'ldp2' | 'ldp3' | 'ldp4' | 'ldp5' | 'ldp6' | 'ldp7'
	private Object LDBPX(Noh noh){		
		return null;
	}

	//	<OP_UNARIA>             ::=     <LOAD> <POS_MEM> | <STORE> <POS_MEM> | <PULO> Rotulo | <PUSH> | <POP>
	private Object OP_UNARIA(Noh noh) {
		if(noh.getFilho(0).getTipoNoh() == TipoNoh.LOAD){
			LOAD(noh.getFilho(0));
			POS_MEM(noh.getFilho(1));
		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.STORE){
			STORE(noh.getFilho(0));
			POS_MEM(noh.getFilho(1));
		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.PULO){
			PULO(noh.getFilho(0));
			noh.getFilho(1);
		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.PUSH){
			PUSH(noh.getFilho(0));
		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.POP){
			POP(noh.getFilho(0));
		}
		return null;
	}

	//	<LOAD>          ::=     'ld0' | 'ld1' | 'ld2' | 'ld3' | 'ld4' | 'ld5' | 'ld6' | 'ld7'
	private Object LOAD(Noh noh) {
		return null;
	}

	//	<STORE>         ::=     'st0' | 'st1' | 'st2' | 'st3' | 'st4' | 'st5' | 'st6' | 'st7'
	private Object STORE(Noh noh) {
		return null;
	}

	//	<POS_MEM>       ::=     IntegerLiteral | HexLiteral | '$' Identifier
	private Object POS_MEM(Noh noh) {
		Integer integer = 0;
		if(noh.getFilho(0).getToken().getTipoToken() == TipoToken.VALOR_HEXADECIMAL)
			integer = Integer.parseInt(noh.getFilho(0).getToken().getValor().substring(2),16);
		else if(noh.getFilho(0).getToken().getTipoToken() == TipoToken.VALOR_NUMERAL)
			integer = Integer.parseInt(noh.getFilho(0).getToken().getValor());

		if(integer < 0)
			erros.add("Erro: Posicao da memoria deve ser positivo.");

		return null;
	}

	//	<PULO>          ::=     'jmp' | 'je' | 'jne' | 'jg' | 'jge' | 'jl' | 'jle' | 'jnz' | 'jz' | 'call'
	private Object PULO(Noh noh) {
		return null;
	}

	//	<PUSH>                  ::=     'push' <PARAMETRO> | 'push' 'bp'
	private Object PUSH(Noh noh) {
		return null;
	}

	//	<POP>                   ::=     'pop' <REGISTRADOR_GERAL> | 'pop' 'bp'
	private Object POP(Noh noh) {
		return null;
	}

	//	<PARAMETRO>             ::=     <REGISTRADOR_GERAL> | IntegerLiteral
	private Object PARAMETRO(Noh noh) {
		if(noh.getFilho(0).getTipoNoh() == TipoNoh.REGISTRADOR_GERAL)
			return REGISTRADOR_GERAL(noh.getFilho(0));
		else{
			Integer n = new Integer(noh.getFilho(0).getToken().getValor());
			if(n < -32768 || n > 32767)
				erros.add("Erro PARAMETRO: Valor deve ser entre -32768 e 32767.");
		}
		return null;
	}

	//	<REGISTRADOR_GERAL>   ::=     'r0' | 'r1' | 'r2' | 'r3' | 'r4' | 'r5' | 'r6' | 'r7'
	private Object REGISTRADOR_GERAL(Noh noh) {
		return noh.getFilho(0).getToken();
	}

	//	<OP_BINARIA>    ::=     'mov' | 'add' | 'sub' | 'mul' | 'div' | 'cmp'
	private Object OP_BINARIA(Noh noh) {
		return null;
	}

}

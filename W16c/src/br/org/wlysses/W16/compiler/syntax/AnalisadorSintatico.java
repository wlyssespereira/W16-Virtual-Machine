package br.org.wlysses.W16.compiler.syntax;

import java.util.ArrayList;
import java.util.List;

import br.org.wlysses.W16.compiler.lexical.TipoToken;
import br.org.wlysses.W16.compiler.lexical.Token;

public class AnalisadorSintatico {

	private List<Token> tokens;
	private Noh raiz;
	private Token token;
	private Integer pToken;
	private List<String> erros = new ArrayList<String>();

	private String temp;

	public AnalisadorSintatico(List<Token> list) {
		this.tokens = list;
	}

	public Noh getRaiz() {
		return raiz;
	}

	public List<String> getErros() {
		return erros;
	}

	private void nextToken() {
		token = tokens.get(pToken);
		pToken++;
	}

	public boolean analisar() {
		pToken = 0;
		nextToken();

		raiz = INICIO();

		return (erros.size() == 0);
	}


	//	<INICIO>        ::=     <BLOCO_VAR> <BLOCO_PROGRAM> '.end'
	private Noh INICIO() {
		Noh noh = new Noh(TipoNoh.INICIO);

		noh.addFilho(BLOCO_VAR());
		noh.addFilho(BLOCO_PROGRAM());

		if(token.getValor().equalsIgnoreCase(".end"))
			noh.addFilho(new Noh(token));
		else
			erros.add("Erro INICIO: esperado '.end'.");

		return noh;
	}

	//	<BLOCO_VAR>     ::=     '.var' <DECLARACOES>
	private Noh BLOCO_VAR() {
		Noh noh = new Noh(TipoNoh.BLOCO_VAR);
		if (token.getValor().equalsIgnoreCase(".var")){
			noh.addFilho(new Noh(token));
			nextToken();
		}else
			erros.add("Erro BLOCO_VAR: esperado '.var'.");

		noh.addFilho(DECLARACOES());
		return noh;
	}

	//	<DECLARACOES>   ::=     <DECLARACAO> <DECLARACOES> | 
	private Noh DECLARACOES() {
		Noh noh = new Noh(TipoNoh.DECLARACOES);

		if(token.getTipoToken() == TipoToken.VARIAVEL)
			noh.addFilho(DECLARACAO());

		if(token.getTipoToken() == TipoToken.VARIAVEL)
			noh.addFilho(DECLARACOES());

		return noh;
	}

	//	<DECLARACAO>    ::=     <VAR>
	private Noh DECLARACAO() {
		Noh noh = new Noh(TipoNoh.DECLARACAO);

		noh.addFilho(VAR());

		return noh;
	}

	//	<VAR>           ::=     '$' Identifier
	private Noh VAR() {
		Noh noh = new Noh(TipoNoh.VAR);
		if (token.getTipoToken() == TipoToken.VARIAVEL){
			noh.addFilho(new Noh(token));
			nextToken();
		}else
			erros.add("Erro VAR: esperado um tipo VARIAVEL.");
		return noh;
	}

	//	<BLOCO_PROGRAM> ::=     '.program' <INSTRUCOES>
	private Noh BLOCO_PROGRAM() {
		Noh noh = new Noh(TipoNoh.BLOCO_PROGRAM);

		if (token.getValor().equalsIgnoreCase(".program")){
			noh.addFilho(new Noh(token));
			nextToken();
		}else
			erros.add("Erro BLOCO_PROGRAM: esperado '.program'.");

		noh.addFilho(INSTRUCOES());

		return noh;
	}

	//	<INSTRUCOES>    ::=     <INSTRUCAO> <INSTRUCOES> | 
	private Noh INSTRUCOES() {
		Noh noh = new Noh(TipoNoh.INSTRUCOES);

		if(
				token.getTipoToken() == TipoToken.ROTULO	||
				token.getTipoToken() == TipoToken.LOAD		||
				token.getTipoToken() == TipoToken.STORE		||
				token.getTipoToken() == TipoToken.PUSH		||
				token.getTipoToken() == TipoToken.POP		||
				token.getTipoToken() == TipoToken.SALTOS	||
				token.getTipoToken() == TipoToken.OPERADOR	||
				token.getTipoToken() == TipoToken.RET		||
				token.getTipoToken() == TipoToken.MBPSP		||
				token.getTipoToken() == TipoToken.LDBPX
				)
			noh.addFilho(INSTRUCAO());

		if(
				token.getTipoToken() == TipoToken.ROTULO	||
				token.getTipoToken() == TipoToken.LOAD		||
				token.getTipoToken() == TipoToken.STORE		||
				token.getTipoToken() == TipoToken.PUSH		||
				token.getTipoToken() == TipoToken.POP		||
				token.getTipoToken() == TipoToken.SALTOS	||
				token.getTipoToken() == TipoToken.OPERADOR	||
				token.getTipoToken() == TipoToken.RET		||
				token.getTipoToken() == TipoToken.MBPSP		||
				token.getTipoToken() == TipoToken.LDBPX
				)
			noh.addFilho(INSTRUCOES());

		return noh;
	}

	//	<INSTRUCAO>     ::=     Rotulo | <OPERACAO>
	private Noh INSTRUCAO() {
		Noh noh = new Noh(TipoNoh.INSTRUCAO);

		if (token.getTipoToken() == TipoToken.ROTULO){
			noh.addFilho(new Noh(token));
			nextToken();
		}else if(token.getTipoToken() == TipoToken.LOAD		||
				token.getTipoToken() == TipoToken.STORE		||
				token.getTipoToken() == TipoToken.PUSH		||
				token.getTipoToken() == TipoToken.POP		||
				token.getTipoToken() == TipoToken.SALTOS	||
				token.getTipoToken() == TipoToken.OPERADOR	||
				token.getTipoToken() == TipoToken.RET		||
				token.getTipoToken() == TipoToken.MBPSP		||
				token.getTipoToken() == TipoToken.LDBPX
				)
			noh.addFilho(OPERACAO());
		else
			erros.add("Erro INSTRUCAO: esperado uma Rotulo ou uma Operacao.");

		return noh;
	}

	//	<OPERACAO>              ::=     <OP_UNARIA> | <OP_BINARIA> <REGISTRADOR_GERAL> <PARAMETRO> | 'ret' | <LDBPX> IntegerLiteral | 'mbpsp'
	private Noh OPERACAO() {
		Noh noh = new Noh(TipoNoh.OPERACAO);

		if(token.getTipoToken() == TipoToken.RET){
			noh.addFilho(new Noh(token));
			nextToken();
		}else if(token.getTipoToken() == TipoToken.LOAD || token.getTipoToken() == TipoToken.STORE || token.getTipoToken() == TipoToken.PUSH || token.getTipoToken() == TipoToken.POP || token.getTipoToken() == TipoToken.SALTOS){
			noh.addFilho(OP_UNARIA());
		}else if(token.getTipoToken() == TipoToken.LDBPX){
			noh.addFilho(LDBPX());
			if(token.getTipoToken() == TipoToken.VALOR_NUMERAL){
				noh.addFilho(new Noh(token));
				nextToken();
			}else
				erros.add("Erro OPERACAO: Esperado um " + TipoToken.VALOR_NUMERAL);

		}else if(token.getTipoToken() == TipoToken.MBPSP){
			noh.addFilho(new Noh(token));
			nextToken();
		}else{
			noh.addFilho(OP_BINARIA());
			noh.addFilho(REGISTRADOR_GERAL());
			noh.addFilho(PARAMETRO());
		}

		return noh;
	}

	//	<LDBPX>                  ::=     'ldp0' | 'ldp1' | 'ldp2' | 'ldp3' | 'ldp4' | 'ldp5' | 'ldp6' | 'ldp7'
	private Noh LDBPX(){
		Noh noh = new Noh(TipoNoh.LDBPX);

		noh.addFilho(new Noh(token));
		nextToken();

		return noh;
	}

	//	<REGISTRADOR_GERAL>   ::=     'r0' | 'r1' | 'r2' | 'r3' | 'r4' | 'r5' | 'r6' | 'r7'
	private Noh REGISTRADOR_GERAL() {
		Noh noh = new Noh(TipoNoh.REGISTRADOR_GERAL);

		if (token.getTipoToken() == TipoToken.REGISTRADOR_GERAL){
			noh.addFilho(new Noh(token));
			nextToken();
		}else
			erros.add("Erro REGISTRADOR: esperado um tipo REGISTRADOR.");

		return noh;
	}

	//	<OP_UNARIA>     ::=     <LOAD> <POS_MEM> | <STORE> <POS_MEM> | <PULO> Rotulo | <PUSH> | <POP>
	private Noh OP_UNARIA() {
		Noh noh = new Noh(TipoNoh.OP_UNARIA);

		if(token.getTipoToken() == TipoToken.LOAD){
			noh.addFilho(LOAD());
			noh.addFilho(POS_MEM());
		}else if (token.getTipoToken() == TipoToken.STORE) {
			noh.addFilho(STORE());
			noh.addFilho(POS_MEM());
		}else if (token.getTipoToken() == TipoToken.SALTOS) {
			noh.addFilho(PULO());
			if (token.getTipoToken() == TipoToken.ROTULO){
				noh.addFilho(new Noh(token));
				nextToken();
			}else
				erros.add("Erro OP_UNARIA: Esperado um tipo ROTULO.");
		}else if(token.getTipoToken() == TipoToken.PUSH){
			noh.addFilho(PUSH());
		}else if(token.getTipoToken() == TipoToken.POP){
			noh.addFilho(POP());
		}else{
			erros.add("Erro OP_UNARIA: Esperado um tipo LOAD, STORE, SALTO, PUSH ou POP.");
		}

		return noh;
	}

	//	<LOAD>          ::=     'ld0' | 'ld1' | 'ld2' | 'ld3' | 'ld4' | 'ld5' | 'ld6' | 'ld7'
	private Noh LOAD() {
		Noh noh = new Noh(TipoNoh.LOAD);

		if 		(token.getValor().equalsIgnoreCase("ld0")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("ld1")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("ld2")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("ld3")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("ld4")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("ld5")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("ld6")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("ld7")){ noh.addFilho(new Noh(token)); nextToken();}
		else erros.add("Erro LOAD: esperado um tipo LOAD.");

		return noh;
	}

	//	<STORE>         ::=     'st0' | 'st1' | 'st2' | 'st3' | 'st4' | 'st5' | 'st6' | 'st7'
	private Noh STORE() {
		Noh noh = new Noh(TipoNoh.STORE);

		if 		(token.getValor().equalsIgnoreCase("st0")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("st1")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("st2")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("st3")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("st4")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("st5")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("st6")){ noh.addFilho(new Noh(token)); nextToken();}
		else if (token.getValor().equalsIgnoreCase("st7")){ noh.addFilho(new Noh(token)); nextToken();}
		else erros.add("Erro STORE: esperado um tipo STORE.");

		return noh;
	}

	//	<POS_MEM>       ::=     IntegerLiteral | HexLiteral | '$' Identifier
	private Noh POS_MEM() {
		Noh noh = new Noh(TipoNoh.POS_MEM);
		if (token.getTipoToken() == TipoToken.VALOR_NUMERAL || token.getTipoToken() == TipoToken.VALOR_HEXADECIMAL || token.getTipoToken() == TipoToken.VARIAVEL){
			noh.addFilho(new Noh(token));
			nextToken();
		}else
			erros.add("Erro POS_MEM: esperado um tipo VALOR_NUMERAL, VALOR_HEXADECIMAL ou uma VARIAVEL.");
		return noh;
	}

	//	<PULO>          ::=     'jmp' | 'je' | 'jne' | 'jg' | 'jge' | 'jl' | 'jle' | 'jnz' | 'jz' | 'call'
	private Noh PULO() {
		Noh noh = new Noh(TipoNoh.PULO);

		if		(token.getValor().equalsIgnoreCase("jmp")){	noh.addFilho(new Noh(token)); nextToken(); }
		else if	(token.getValor().equalsIgnoreCase("je")){	noh.addFilho(new Noh(token)); nextToken(); }
		else if	(token.getValor().equalsIgnoreCase("jne")){	noh.addFilho(new Noh(token)); nextToken(); }
		else if	(token.getValor().equalsIgnoreCase("jg")){	noh.addFilho(new Noh(token)); nextToken(); }
		else if	(token.getValor().equalsIgnoreCase("jge")){	noh.addFilho(new Noh(token)); nextToken(); }
		else if	(token.getValor().equalsIgnoreCase("jl")){	noh.addFilho(new Noh(token)); nextToken(); }
		else if	(token.getValor().equalsIgnoreCase("jle")){	noh.addFilho(new Noh(token)); nextToken(); }
		else if	(token.getValor().equalsIgnoreCase("jnz")){	noh.addFilho(new Noh(token)); nextToken(); }
		else if	(token.getValor().equalsIgnoreCase("jz")){	noh.addFilho(new Noh(token)); nextToken(); }

		else if	(token.getValor().equalsIgnoreCase("call")){noh.addFilho(new Noh(token)); nextToken(); }

		else erros.add("Erro PULO: esperado um tipo SALTOS.");

		return noh;
	}

	//	<PUSH>                  ::=     'push' <PARAMETRO> | 'push' 'bp'
	private Noh PUSH() {
		Noh noh = new Noh(TipoNoh.PUSH);

		if (token.getTipoToken() == TipoToken.PUSH){
			noh.addFilho(new Noh(token));
			nextToken();

			if(token.getTipoToken() == TipoToken.REGISTRADOR_BP){
				noh.addFilho(new Noh(token));
				nextToken();
			}else
				noh.addFilho(PARAMETRO());

		}else
			erros.add("Erro PUSH: esperado um tipo PUSH.");

		return noh;
	}

	//	<POP>                   ::=     'pop' <REGISTRADOR_GERAL> | 'pop' 'bp'
	private Noh POP() {
		Noh noh = new Noh(TipoNoh.POP);

		if (token.getTipoToken() == TipoToken.POP){
			noh.addFilho(new Noh(token));
			nextToken();
			temp = "POP";

			if(token.getTipoToken() == TipoToken.REGISTRADOR_BP){
				noh.addFilho(new Noh(token));
				nextToken();
			}else
				noh.addFilho(PARAMETRO());
			
			if(temp != null && temp.equalsIgnoreCase("POP"))
				temp = null;
			
		}else
			erros.add("Erro POP: esperado um tipo POP.");

		return noh;
	}

	//	<PARAMETRO>             ::=     <REGISTRADOR_GERAL> | IntegerLiteral
	private Noh PARAMETRO() {
		Noh noh = new Noh(TipoNoh.PARAMETRO);

		if(token.getTipoToken() == TipoToken.REGISTRADOR_GERAL){
			noh.addFilho(REGISTRADOR_GERAL());
			temp = null;
		}else if(token.getTipoToken() == TipoToken.VALOR_NUMERAL){

			if(temp != null && temp.equalsIgnoreCase("POP")){
				erros.add("Erro PARAMETRO: nao permitido numero apos POP.");
				return noh;
			}	

			noh.addFilho(new Noh(token));
			nextToken();
			temp = null;
		}else
			erros.add("Erro PARAMETRO: esperado um tipo REGISTRADOR_GERAL ou VALOR_NUMERAL.");

		return noh;
	}

	//	<OP_BINARIA>    ::=     'mov' | 'add' | 'sub' | 'mul' | 'div' | 'cmp'
	private Noh OP_BINARIA() {
		Noh noh = new Noh(TipoNoh.OP_BINARIA);

		if (token.getTipoToken() == TipoToken.OPERADOR){
			noh.addFilho(new Noh(token));
			nextToken();
		}else
			erros.add("Erro OP_BINARIA: esperado um tipo OPERADOR.");
		return noh;
	}


	public void mostraArvore() {
		String espacamento = "";
		System.out.println("");
		mostraNo(raiz, espacamento);
	}

	private void mostraNo(Noh noh, String espacamento) {
		mostraTipoNo(noh, espacamento);
		if (noh != null && noh.getFilhos() != null) {
			for (Noh node : noh.getFilhos()) {
				mostraNo(node, espacamento + "    ");
			}
		}
	}

	private void mostraTipoNo(Noh no, String espacamento) {
		if (no.getTipoNoh() == TipoNoh.TOKEN) {
			System.out.println(espacamento + no.getToken().getValor());
		} else {
			System.out.println(espacamento + no.getTipoNoh().toString());
		}
	}

}


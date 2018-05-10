package br.org.wlysses.W16.compiler.lexical;

public enum TipoToken {

	LOAD,				//Loads
	STORE,				//Stories
	PUSH,				//PUSH
	POP,				//POP
	SALTOS,				//Saltos
	
	LDBPX,				//Load de BP
	MBPSP,				//Move valor de SP para BP

	OPERADOR,			//MOV, ADD, SUB, etc...

	VALOR_NUMERAL,		//Valores inteiros
	VALOR_HEXADECIMAL,	//Valores da regiao de memoria. Ex: "0x88"
	STRING,				//Valor de uma palavra

	PALAVRA_RESERVADA,	// .var, .program, .end
	VARIAVEL,			//Começam com "$"
	VARIAVEL_TIPO,		//Tipos de variaveis
	ROTULO,				//Rotulos começam com dois pontos (:)

	REGISTRADOR_GERAL,	//Registradores de uso geral (r0 ... r7)
	REGISTRADOR_SP,		//Registrador de ponteiro de pilha
	REGISTRADOR_BP,		//Registrador de base de pilha

	RET,				//Retorno

	COMENTARIO,			//Comentario simples

	INDEFINIDO;			//Sem definição.
}
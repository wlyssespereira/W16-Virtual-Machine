package br.org.wlysses.W16.compiler.generator;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.org.wlysses.W16.compiler.lexical.TipoToken;
import br.org.wlysses.W16.compiler.lexical.Token;
import br.org.wlysses.W16.compiler.syntax.Noh;
import br.org.wlysses.W16.compiler.syntax.TipoNoh;

public class CodeGenerator{

	private Noh raiz = null;
	private DataOutputStream dos = null;
	private Map<String, Short> rotulos = new HashMap<String, Short>();
	private Map<String, String> variaveis = new HashMap<String, String>();
	private List<String> erros = new ArrayList<String>();

	private short posVar = 0;

	private short CS = (short) 0x00_00; //0
	private short DS = (short) 0x02_00; //512
	private short SS = (short) 0x03_00; //768

	public CodeGenerator(Noh raiz, String out, Map<String, Short> rotulos){
		this.raiz = raiz;
		this.rotulos = rotulos;
		try {
			dos = new DataOutputStream(new FileOutputStream(out));
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}


	public boolean generate() {

		try{
			writeByte((byte) 'W');
			writeByte((byte) '1');
			writeByte((byte) '6');

			writeShort((short) CS);	//CS
			writeShort((short) DS);	//DS
			writeShort((short) SS);	//SS

			INICIO(raiz);

			//End
			writeByte((byte) 0b1111_1110);
			writeByte((byte) 0b0000_0000);
			writeByte((byte) 0b0000_0000);


			writeByte((byte) 0xFF);



			writeByte((byte) 0xFF);

			close();

			return erros.isEmpty();

		}catch(Exception e){
			return false;
		}

	}

	public List<String> getErros() {
		return erros;
	}

	private void writeShort(short s){
		short remove_signal = 0b0000_0000__1111_1111;
		byte s1 = (byte) ((s >> 8) & remove_signal);
		byte s2 = (byte) (s & remove_signal);
		writeByte((byte) s1);
		writeByte((byte) s2);
	}


	private void writeByte(byte b){
		try {
			dos.writeByte(b);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void close(){
		try {
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	//	<INICIO>        ::=     <BLOCO_VAR> <BLOCO_PROGRAM> '.end'
	private Object INICIO(Noh noh){

		BLOCO_VAR(noh.getFilho(0));
		BLOCO_PROGRAM(noh.getFilho(1));
		noh.getFilho(2);

		return null;
	}

	//	<BLOCO_VAR>     ::=     '.var' <DECLARACOES> 
	private Object BLOCO_VAR(Noh noh){

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

		String nomeVar;
		nomeVar = (String) VAR(noh.getFilho(0));

		if(!variaveis.containsKey(nomeVar)){
			variaveis.put(nomeVar, Short.toString(posVar));
			posVar += 2;
		}

		return null;
	}

	//	<VAR>           ::=     '$' Identifier
	private Object VAR(Noh noh) {
		return noh.getFilho(0).getToken().getValor();
	}

	//	<BLOCO_PROGRAM> ::=     '.program' <INSTRUCOES>
	private Object BLOCO_PROGRAM(Noh noh) {
		noh.getFilho(0);
		INSTRUCOES(noh.getFilho(1));
		return null;
	}

	//	<INSTRUCOES>    ::=     <INSTRUCAO> <INSTRUCOES> | 
	private Object INSTRUCOES(Noh noh) {
		if(!noh.getFilhos().isEmpty()){
			INSTRUCAO(noh.getFilho(0));
			if(noh.getFilhos().size() > 1)
				INSTRUCOES(noh.getFilho(1));
		}
		return null;
	}

	//	<INSTRUCAO>     ::=     Rotulo | <OPERACAO>
	private Object INSTRUCAO(Noh noh) {
		if(noh.getFilho(0).getTipoNoh() == TipoNoh.TOKEN){
			writeByte((byte) 0b1111_1101);
			writeShort((short) 0);
		}else{
			OPERACAO(noh.getFilho(0));
		}
		return null;
	}

	//	<OPERACAO>              ::=     <OP_UNARIA> | <OP_BINARIA> <REGISTRADOR_GERAL> <PARAMETRO> | 'ret' | <LDBPX> IntegerLiteral | 'mbpsp'
	private Object OPERACAO(Noh noh) {

		if(noh.getFilho(0).getTipoNoh() == TipoNoh.TOKEN){
			if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ret")){
				writeByte((byte) 0b0010_1010);
				writeShort((short) 0);
			}else if (noh.getFilho(0).getToken().getTipoToken() == TipoToken.MBPSP) {
				writeByte((byte) 0b1_0_111_111);
				writeShort((short) 0);
			}
		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.LDBPX){
			LDBPX(noh.getFilho(0));
			writeShort(Short.parseShort(noh.getFilho(1).getToken().getValor()));
		}else if(noh.getFilhos().size() == 1)
			OP_UNARIA(noh.getFilho(0));
		else{
			String string="";
			byte b = 0;
			short s = 0;
			if(noh.getFilho(0).getFilho(0).getToken().getValor().equalsIgnoreCase("add")){
				writeByte((byte) 0b0001_0000);
				string += "00";
				string += REGISTRADOR_GERAL(noh.getFilho(1));
				string += REGISTRADOR_GERAL(noh.getFilho(2).getFilho(0));
				b = (byte) Integer.parseInt(string, 2);
				writeByte(b);
				writeByte((byte) 0);
			}else if(noh.getFilho(0).getFilho(0).getToken().getValor().equalsIgnoreCase("sub")){
				writeByte((byte) 0b0001_0000);
				string += "01";
				string += REGISTRADOR_GERAL(noh.getFilho(1));
				string += REGISTRADOR_GERAL(noh.getFilho(2).getFilho(0));
				b = (byte) Integer.parseInt(string, 2);
				writeByte(b);
				writeByte((byte) 0);
			}else if(noh.getFilho(0).getFilho(0).getToken().getValor().equalsIgnoreCase("mul")){
				writeByte((byte) 0b0001_0000);
				string += "10";
				string += REGISTRADOR_GERAL(noh.getFilho(1));
				string += REGISTRADOR_GERAL(noh.getFilho(2).getFilho(0));
				b = (byte) Integer.parseInt(string, 2);
				writeByte(b);
				writeByte((byte) 0);
			}else if(noh.getFilho(0).getFilho(0).getToken().getValor().equalsIgnoreCase("div")){
				writeByte((byte) 0b0001_0000);
				string += "11";
				string += REGISTRADOR_GERAL(noh.getFilho(1));
				string += REGISTRADOR_GERAL(noh.getFilho(2).getFilho(0));
				b = (byte) Integer.parseInt(string, 2);
				writeByte(b);
				writeByte((byte) 0);
			}else if(noh.getFilho(0).getFilho(0).getToken().getValor().equalsIgnoreCase("cmp")){
				writeByte((byte) 0b0011_0000);
				string += "00";
				string += REGISTRADOR_GERAL(noh.getFilho(1));
				string += REGISTRADOR_GERAL(noh.getFilho(2).getFilho(0));
				b = (byte) Integer.parseInt(string, 2);
				writeByte(b);
				writeByte((byte) 0);
			}else if(noh.getFilho(0).getFilho(0).getToken().getValor().equalsIgnoreCase("mov")){
				if(noh.getFilho(2).getFilho(0).getTipoNoh() == TipoNoh.REGISTRADOR_GERAL){

					string += "10";

					string += REGISTRADOR_GERAL(noh.getFilho(1));
					string += REGISTRADOR_GERAL(noh.getFilho(2).getFilho(0));

					b = (byte) Integer.parseInt(string, 2);
					writeByte(b);
					writeShort((short) 0);
				}else{
					string += "11000";
					string += REGISTRADOR_GERAL(noh.getFilho(1));
					b = (byte) Integer.parseInt(string, 2);
					writeByte(b);
					string = "";
					string += PARAMETRO(noh.getFilho(2));
					s = (short) Integer.parseInt(string.substring(string.length()-16), 2);
					writeShort(s);
				}
			}
		}
		return null;
	}

	//	<LDBPX>                  ::=     'ldp0' | 'ldp1' | 'ldp2' | 'ldp3' | 'ldp4' | 'ldp5' | 'ldp6' | 'ldp7'
	private Object LDBPX(Noh noh){

		Token token = noh.getFilho(0).getToken();

		if(token.getValor().equalsIgnoreCase("LDBP0")) {writeByte((byte)0b0110_0_000); return null;}
		if(token.getValor().equalsIgnoreCase("LDBP1")) {writeByte((byte)0b0110_0_001); return null;}
		if(token.getValor().equalsIgnoreCase("LDBP2")) {writeByte((byte)0b0110_0_010); return null;}
		if(token.getValor().equalsIgnoreCase("LDBP3")) {writeByte((byte)0b0110_0_011); return null;}
		if(token.getValor().equalsIgnoreCase("LDBP4")) {writeByte((byte)0b0110_0_100); return null;}
		if(token.getValor().equalsIgnoreCase("LDBP5")) {writeByte((byte)0b0110_0_101); return null;}
		if(token.getValor().equalsIgnoreCase("LDBP6")) {writeByte((byte)0b0110_0_110); return null;}
		if(token.getValor().equalsIgnoreCase("LDBP7")) {writeByte((byte)0b0110_0_111); return null;}

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
			//Buscando o valor do rotulo
			Short rotulo = getRotulo(noh.getFilho(1));

			if(rotulo == null)
				erros.add("Erro: Rotulo " + noh.getFilho(1).getToken().getValor() + " nao declarado.");

			writeShort(rotulo);

		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.PUSH){
			PUSH(noh.getFilho(0));
		}else if(noh.getFilho(0).getTipoNoh() == TipoNoh.POP){
			POP(noh.getFilho(0));
		}
		return null;
	}

	//Retorna o valor de um rotulo da lista.
	private Short getRotulo(Noh noh) {
		return rotulos.get(noh.getToken().getValor());
	}

	//	<LOAD>          ::=     'ld0' | 'ld1' | 'ld2' | 'ld3' | 'ld4' | 'ld5' | 'ld6' | 'ld7'
	private Object LOAD(Noh noh) {
		String bytt = "00000";

		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ld0")) bytt += "000";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ld1")) bytt += "001";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ld2")) bytt += "010";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ld3")) bytt += "011";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ld4")) bytt += "100";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ld5")) bytt += "101";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ld6")) bytt += "110";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("ld7")) bytt += "111";

		byte byte1 = (byte) Integer.parseInt(bytt, 2);

		writeByte(byte1);
		return null;
	}

	//	<STORE>         ::=     'st0' | 'st1' | 'st2' | 'st3' | 'st4' | 'st5' | 'st6' | 'st7'
	private Object STORE(Noh noh) {
		String bytt = "00001";

		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("st0")) bytt += "000";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("st1")) bytt += "001";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("st2")) bytt += "010";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("st3")) bytt += "011";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("st4")) bytt += "100";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("st5")) bytt += "101";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("st6")) bytt += "110";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("st7")) bytt += "111";

		byte byte1 = (byte) Integer.parseInt(bytt, 2);

		writeByte(byte1);
		return null;
	}

	//	<POS_MEM>       ::=     IntegerLiteral | HexLiteral | '$' Identifier
	private Object POS_MEM(Noh noh){
		Short short1 = null;
		if(noh.getFilho(0).getToken().getTipoToken() == TipoToken.VALOR_NUMERAL)
			short1 = new Short(noh.getFilho(0).getToken().getValor());
		else if(noh.getFilho(0).getToken().getTipoToken() == TipoToken.VALOR_HEXADECIMAL){
			short1 = (short) Integer.parseInt(noh.getFilho(0).getToken().getValor().substring(2), 16);
		}else{
			short1 = Short.parseShort(variaveis.get(noh.getFilho(0).getToken().getValor()));
		}

		writeShort(short1.shortValue());
		return null;
	}

	//	<PULO>          ::=     'jmp' | 'je' | 'jne' | 'jg' | 'jge' | 'jl' | 'jle' | 'jnz' | 'jz' | 'call'
	private Object PULO(Noh noh){
		byte byte1 = 0;
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("jmp"))	byte1 = (byte) Integer.parseInt("00100000", 2);
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("je"))		byte1 = (byte) Integer.parseInt("00100001", 2);
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("jne"))	byte1 = (byte) Integer.parseInt("00100010", 2);
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("jg"))		byte1 = (byte) Integer.parseInt("00100011", 2);
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("jge"))	byte1 = (byte) Integer.parseInt("00100100", 2);
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("jl"))		byte1 = (byte) Integer.parseInt("00100101", 2);
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("jle"))	byte1 = (byte) Integer.parseInt("00100110", 2);
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("jnz"))	byte1 = (byte) Integer.parseInt("00100111", 2);
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("jz"))		byte1 = (byte) Integer.parseInt("00101000", 2);

		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("call"))	byte1 = (byte) Integer.parseInt("00101001", 2);

		writeByte(byte1);

		return null;
	}

	//	<PUSH>                  ::=     'push' <PARAMETRO> | 'push' 'bp'
	private Object PUSH(Noh noh) {
		String value = "0100";

		if(noh.getFilho(1).getTipoNoh() == TipoNoh.TOKEN && noh.getFilho(1).getToken().getTipoToken() == TipoToken.REGISTRADOR_BP)
			value += "1"+"001"+"00000000"+"00000000";
		else
			value += (String) PARAMETRO(noh.getFilho(1));

		writeTreeBytes(value);

		return null;
	}

	//	<POP>                   ::=     'pop' <REGISTRADOR_GERAL> | 'pop' 'bp'
	private Object POP(Noh noh) {
		String value = "0101";

		if((noh.getFilho(1).getTipoNoh() != TipoNoh.TOKEN) && (noh.getFilho(1).getFilho(0).getTipoNoh() == TipoNoh.REGISTRADOR_GERAL))	
			value += "0" + REGISTRADOR_GERAL(noh.getFilho(1).getFilho(0)) + "00000000" + "00000000";
		else
			value += "1" + "000" + "00000000" + "00000000";

		writeTreeBytes(value);

		return null;
	}

	//	<PARAMETRO>             ::=     <REGISTRADOR_GERAL> | IntegerLiteral
	private Object PARAMETRO(Noh noh) {
		String temp = "";
		if(noh.getFilho(0).getTipoNoh() == TipoNoh.REGISTRADOR_GERAL)
			return "0" + REGISTRADOR_GERAL(noh.getFilho(0)) + "00000000" + "00000000";

		temp = ("0000000000000000" + Integer.toBinaryString(Integer.parseInt(noh.getFilho(0).getToken().getValor())));
		temp = temp.substring(temp.length()-16);
		return "1000" + temp;
	}

	/**
	 * Escreve tres bytes no buffer
	 * @param {@link String}
	 */
	private void writeTreeBytes(String value) {
		byte b1, b2, b3;
		int i1, i2, i3;

		i1 = Integer.parseInt(value.substring(0, 8), 2);
		i2 = Integer.parseInt(value.substring(9, 15), 2);
		i3 = Integer.parseInt(value.substring(16, 24), 2);


		b1 = Byte.parseByte(i1+"");
		b2 = Byte.parseByte(i2+"");
		b3 = Byte.parseByte(i3+"");

		writeByte(b1);
		writeByte(b2);
		writeByte(b3);
	}

	//	<REGISTRADOR_GERAL>   ::=     'r0' | 'r1' | 'r2' | 'r3' | 'r4' | 'r5' | 'r6' | 'r7'
	private Object REGISTRADOR_GERAL(Noh noh){

		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("r0")) return "000";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("r1")) return "001";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("r2")) return "010";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("r3")) return "011";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("r4")) return "100";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("r5")) return "101";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("r6")) return "110";
		if(noh.getFilho(0).getToken().getValor().equalsIgnoreCase("r7")) return "111";

		return null;
	}

}

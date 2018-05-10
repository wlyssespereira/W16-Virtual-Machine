package br.org.wlysses.W16.compiler.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import br.org.wlysses.W16.compiler.generator.CodeGenerator;
import br.org.wlysses.W16.compiler.lexical.AnalisadorLexico;
import br.org.wlysses.W16.compiler.semantic.AnalisadorSemantico;
import br.org.wlysses.W16.compiler.syntax.AnalisadorSintatico;


public class Main {

	/**
	 * Método main.
	 * @param args
	 */
	public static void main(String[] args) {

		if(args.length < 1){
			System.err.println("Erro: Sem codigo.");
			return;
		}

		double inicio = System.currentTimeMillis();

		String code = getCode(args[0]);
		List<String> lstStrings = Arrays.asList(code.split(" "));

		boolean sucesso = false;

		AnalisadorLexico al = new AnalisadorLexico();
		sucesso = al.analisar(lstStrings);

		if(sucesso){
			//System.out.println("Analise lexica sucesso.");
		}else{
			System.err.println("Erro analise lexica.");
			mostraErros(al.getErros());
			return;
		}

		AnalisadorSintatico asyn = new AnalisadorSintatico(al.getLstTokens());
		sucesso = asyn.analisar();

		if(sucesso){
			//System.out.println("Analise sintatica sucesso.");
		}else{
			System.err.println("Erro analise sintatica.");
			mostraErros(asyn.getErros());
			return;
		}
		

		AnalisadorSemantico asem = new AnalisadorSemantico(asyn.getRaiz());
		sucesso = asem.analisar();

		if(sucesso){
			//System.out.println("Analise semantica sucesso.");
		}else{
			System.err.println("Erro analise semantica.");
			mostraErros(asem.getErros());
			return;
		}

		String out;
		if(args.length < 2)
			out = getNameNoExt(args[0]) + ".wbin";
		else
			out = args[1];

		CodeGenerator cg = new CodeGenerator(asyn.getRaiz(), out, asem.getRotulos());
		sucesso = cg.generate();

		if(sucesso){
			//System.out.println("Codigo gerado com sucesso.");
			System.out.println("Feito.");
		}else{
			System.err.println("Erro na geracao do codigo.");
			mostraErros(cg.getErros());
			return;
		}

		double fim = System.currentTimeMillis();
		double tempo = (fim - inicio) / 1000;

		System.out.println("Tempo: " + tempo + " segundos.");

	}

	private static String getNameNoExt(String arg) {
		String[] array = arg.split("\\.");
		String returnString="";
		for (int i = 0; i < array.length-1; i++) {
			returnString += array[i];
		}
		return returnString;
	}

	private static void mostraErros(List<String> erros){
		for (Iterator<String> iterator = erros.iterator(); iterator.hasNext();) {
			String erro = (String) iterator.next();
			System.err.println(erro);
		}
	}

	private static String getCode(String arg){
		String str = "";
		Scanner scanner = null;

		try {
			scanner = new Scanner(new File(arg));

			while (scanner.hasNext())
				str += scanner.next() + " ";

		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} finally{
			try {
				scanner.close();
			} catch (Exception e) {}
		}

		return str;
	}

}
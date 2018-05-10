package br.org.wlysses.W16.main;

import br.org.wlysses.W16.execution.Execution;
import br.org.wlysses.W16.machine.Processor;
import br.org.wlysses.W16.source.Source;
import br.org.wlysses.W16.util.MemoryUtil;

public class Main {

	/**
	 * Main function.
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("No file argument");
			return;
		}

		run(args[0]);

	}


	public static void run(String arg){

		boolean bool = false;

		Processor p0 = Source.insertCodeInRam(arg);

		//		@SuppressWarnings("unused")
		//		ProgramInfo programInfo;

		long inicio = System.currentTimeMillis();
		
		if(p0 != null){
			//			programInfo = new ProgramInfo(p0);
			do{
				bool = Execution.executeInstruction(p0);
			}while(bool);
		}else
			return;

		long fim = System.currentTimeMillis();
		
		System.out.println("Tempo de execucao: "+ (fim - inicio) +"ms\n");

		System.out.println(p0);
		System.out.println();
		System.out.println(MemoryUtil.getMemoryInfo(p0));


	}

}

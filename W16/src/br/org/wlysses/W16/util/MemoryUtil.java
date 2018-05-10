package br.org.wlysses.W16.util;

import br.org.wlysses.W16.machine.Memory;
import br.org.wlysses.W16.machine.Processor;


/**
 * Class used to print values of RAM.
 * @author Wlysses Pereira
 */
public class MemoryUtil {

	/**
	 * Returns the representation of memory and its CS, DS and SS.
	 * @param processor
	 * @return {@link String}
	 */
	public static String getMemoryInfo(Processor processor) {

		short CS = processor.CS;
		short DS = processor.DS;
		short SS = processor.SS;

		String retorno = "";

		int remove_signal = 0b000000000000000000000000_1111_1111;

		int i;
		retorno += "CS: ("+ CS +")\n";
		for(i = CS; Memory.ram[i] != -2; i+=3){
			retorno += i-CS + " = " + Integer.toHexString(Memory.ram[i] & remove_signal).toUpperCase() + " ";
			retorno += Integer.toHexString(Memory.ram[i+1] & remove_signal).toUpperCase() + " ";
			retorno += Integer.toHexString(Memory.ram[i+2] & remove_signal).toUpperCase() + "\n";
		}
		retorno += i-CS + " = " + Integer.toHexString(Memory.ram[i] & remove_signal).toUpperCase() + " ";
		retorno += Integer.toHexString(Memory.ram[i+1] & remove_signal).toUpperCase() + " ";
		retorno += Integer.toHexString(Memory.ram[i+2] & remove_signal).toUpperCase() + "\n";

		retorno += "\n";

		retorno += "DS: ("+ DS +")\n";
		for(i = DS; i < DS+20; i+=2){
			retorno += i-DS + " = " + shortValue(Memory.ram[i], Memory.ram[i+1]) + "\n";
		}

		retorno += "\n";

		retorno += "SS: ("+ SS +")\n";
		for(i = SS; i <= SS + processor.SP; i+=2){
			retorno += i-SS + " = " + shortValue(Memory.ram[i], Memory.ram[i+1]);

			if((i-SS) == processor.BP)
				retorno += " <= BP(" + processor.BP + ")";
			if((i-SS) == processor.SP)
				retorno += " <= SP(" + processor.SP + ")";

			retorno += "\n";
		}

		return retorno;

	}

	private static short shortValue(byte b1, byte b2){
		short strip_signal = 0b00000000_11111111;

		short short1 = (short) (b1 & strip_signal);
		short1 = (short) (short1 << 8);
		short short2 = (short) (b2 & strip_signal);

		short short3 = (short) (short1 | short2);

		return short3;
	}

}

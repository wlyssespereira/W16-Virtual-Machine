package br.org.wlysses.W16.execution;

import br.org.wlysses.W16.execution.exception.TipeException;
import br.org.wlysses.W16.execution.exception.W16Exception;
import br.org.wlysses.W16.machine.Memory;
import br.org.wlysses.W16.machine.Processor;
import br.org.wlysses.W16.util.Flag;

/**
 * Class used for performing operations.
 * @author Wlysses Pereira
 */
public class Execution {

	/**
	 * Executes the instruction memory,
	 * is passed as a parameter the processor that will execute the statement.
	 * Returns a boolean telling whether the instruction was executed.
	 * @param processor
	 * @return {@link boolean}
	 */
	public static boolean executeInstruction(Processor processor) {

		if (Memory.ram[processor.IP] >= 0x00 && Memory.ram[processor.IP] < 0x10) {
			//Loads e Stores
			return executeLoadStore(processor);
		}else if(Memory.ram[processor.IP] == 0x10){
			//Operações aritméticas.
			return executeAddSubMulDiv(processor);
		}else if(Memory.ram[processor.IP] >= 0x20 && Memory.ram[processor.IP] <= 0x28) {
			//Pulos condicionais e incondicionais.
			return jumps(processor);
		}else if(Memory.ram[processor.IP] == 0x29){
			//Chamada de função.
			return call(processor);
		}else if(Memory.ram[processor.IP] == 0x2A){
			//Saida de função.
			return ret(processor);
		}else if(Memory.ram[processor.IP] == 0x30) {
			//Comparações.
			return compares(processor);
		}else if(Memory.ram[processor.IP] >= 0x40 && Memory.ram[processor.IP] < 0x60) {
			//push e pop.
			return pushPop(processor);
		}else if(Memory.ram[processor.IP] >= 0b0110_0_000 && Memory.ram[processor.IP] <= 0b0110_0_111){
			//ldpx (Load de BP)
			return executeLoadBP(processor);
		}else if(Memory.ram[processor.IP] >= (byte)0b1_0_000_001 && Memory.ram[processor.IP] <= (byte)0b1_1_111_000) {
			//MOVs
			return executeMov(processor);
		}else if(Memory.ram[processor.IP] == (byte)0b1111_1101) {
			//Empty instruction
			processor.IP += 3;
			return true;
		}else if(Memory.ram[processor.IP] == (byte)0b1111_1110) {
			return false;
		}

		//Default
		return false;
	}


	private static boolean ret(Processor processor){

		//Verificando estouro de pilha.
		if(processor.SP == 0){
			new W16Exception(TipeException.OVERFLOW_SS);
			return false;
		}

		processor.SP -= 2;
		processor.IP = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);


		return true;
	}

	private static boolean call(Processor processor) {

		//Inserindo o ponteiro de codigo atual na pilha.
		Memory.ram[processor.SP + processor.SS]	= 0;
		Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.IP + 3);
		processor.SP += 2;

		//Inserindo o novo ponteiro de codigo.
		processor.IP = (short) ((processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2]))));

		return true;
	}

	private static boolean executeMov(Processor processor) {
		switch (Memory.ram[processor.IP]) {
		case (byte) 0b1_0_000_001:
			processor.r0 = processor.r1;
		break;
		case (byte) 0b1_0_000_010:
			processor.r0 = processor.r2;
		break;
		case (byte) 0b1_0_000_011:
			processor.r0 = processor.r3;
		break;
		case (byte) 0b1_0_000_100:
			processor.r0 = processor.r4;
		break;
		case (byte) 0b1_0_000_101:
			processor.r0 = processor.r5;
		break;
		case (byte) 0b1_0_000_110:
			processor.r0 = processor.r6;
		break;
		case (byte) 0b1_0_000_111:
			processor.r0 = processor.r7;
		break;
		case (byte) 0b1_0_001_000:
			processor.r1 = processor.r0;
		break;
		case (byte) 0b1_0_001_010:
			processor.r1 = processor.r2;
		break;
		case (byte) 0b1_0_001_011:
			processor.r1 = processor.r3;
		break;
		case (byte) 0b1_0_001_100:
			processor.r1 = processor.r4;
		break;
		case (byte) 0b1_0_001_101:
			processor.r1 = processor.r5;
		break;
		case (byte) 0b1_0_001_110:
			processor.r1 = processor.r6;
		break;
		case (byte) 0b1_0_001_111:
			processor.r1 = processor.r7;
		break;
		case (byte) 0b1_0_010_000:
			processor.r2 = processor.r0;
		break;
		case (byte) 0b1_0_010_001:
			processor.r2 = processor.r1;
		break;
		case (byte) 0b1_0_010_011:
			processor.r2 = processor.r3;
		break;
		case (byte) 0b1_0_010_100:
			processor.r2 = processor.r4;
		break;
		case (byte) 0b1_0_010_101:
			processor.r2 = processor.r5;
		break;
		case (byte) 0b1_0_010_110:
			processor.r2 = processor.r6;
		break;
		case (byte) 0b1_0_010_111:
			processor.r2 = processor.r7;
		break;
		case (byte) 0b1_0_011_000:
			processor.r3 = processor.r0;
		break;
		case (byte) 0b1_0_011_001:
			processor.r3 = processor.r1;
		break;
		case (byte) 0b1_0_011_010:
			processor.r3 = processor.r2;
		break;
		case (byte) 0b1_0_011_100:
			processor.r3 = processor.r4;
		break;
		case (byte) 0b1_0_011_101:
			processor.r3 = processor.r5;
		break;
		case (byte) 0b1_0_011_110:
			processor.r3 = processor.r6;
		break;
		case (byte) 0b1_0_011_111:
			processor.r3 = processor.r7;
		break;
		case (byte) 0b1_0_100_000:
			processor.r4 = processor.r0;
		break;
		case (byte) 0b1_0_100_001:
			processor.r4 = processor.r1;
		break;
		case (byte) 0b1_0_100_010:
			processor.r4 = processor.r2;
		break;
		case (byte) 0b1_0_100_011:
			processor.r4 = processor.r3;
		break;
		case (byte) 0b1_0_100_101:
			processor.r4 = processor.r5;
		break;
		case (byte) 0b1_0_100_110:
			processor.r4 = processor.r6;
		break;
		case (byte) 0b1_0_100_111:
			processor.r4 = processor.r7;
		break;
		case (byte) 0b1_0_101_000:
			processor.r5 = processor.r0;
		break;
		case (byte) 0b1_0_101_001:
			processor.r5 = processor.r1;
		break;
		case (byte) 0b1_0_101_010:
			processor.r5 = processor.r2;
		break;
		case (byte) 0b1_0_101_011:
			processor.r5 = processor.r3;
		break;
		case (byte) 0b1_0_101_100:
			processor.r5 = processor.r4;
		break;
		case (byte) 0b1_0_101_110:
			processor.r5 = processor.r6;
		break;
		case (byte) 0b1_0_101_111:
			processor.r5 = processor.r7;
		break;
		case (byte) 0b1_0_110_000:
			processor.r6 = processor.r0;
		break;
		case (byte) 0b1_0_110_001:
			processor.r6 = processor.r1;
		break;
		case (byte) 0b1_0_110_010:
			processor.r6 = processor.r2;
		break;
		case (byte) 0b1_0_110_011:
			processor.r6 = processor.r3;
		break;
		case (byte) 0b1_0_110_100:
			processor.r6 = processor.r4;
		break;
		case (byte) 0b1_0_110_101:
			processor.r6 = processor.r5;
		break;
		case (byte) 0b1_0_110_111:
			processor.r6 = processor.r7;
		break;
		case (byte) 0b1_0_111_000:
			processor.r7 = processor.r0;
		break;
		case (byte) 0b1_0_111_001:
			processor.r7 = processor.r1;
		break;
		case (byte) 0b1_0_111_010:
			processor.r7 = processor.r2;
		break;
		case (byte) 0b1_0_111_011:
			processor.r7 = processor.r3;
		break;
		case (byte) 0b1_0_111_100:
			processor.r7 = processor.r4;
		break;
		case (byte) 0b1_0_111_101:
			processor.r7 = processor.r5;
		break;
		case (byte) 0b1_0_111_110:
			processor.r7 = processor.r6;
		break;

		//MOV BP, SP
		case (byte) 0b1_0_111_111:
			processor.BP = (short) (processor.SP - 2);
		break;

		////////////////////////////////
		case (byte) 0b1_1_000_000:
			processor.r0 = shortValue(Memory.ram[processor.IP + 1], Memory.ram[processor.IP + 2]);
		break;
		case (byte) 0b1_1_000_001:
			processor.r1 = shortValue(Memory.ram[processor.IP + 1], Memory.ram[processor.IP + 2]);
		break;
		case (byte) 0b1_1_000_010:
			processor.r2 = shortValue(Memory.ram[processor.IP + 1], Memory.ram[processor.IP + 2]);
		break;
		case (byte) 0b1_1_000_011:
			processor.r3 = shortValue(Memory.ram[processor.IP + 1], Memory.ram[processor.IP + 2]);
		break;
		case (byte) 0b1_1_000_100:
			processor.r4 = shortValue(Memory.ram[processor.IP + 1], Memory.ram[processor.IP + 2]);
		break;
		case (byte) 0b1_1_000_101:
			processor.r5 = shortValue(Memory.ram[processor.IP + 1], Memory.ram[processor.IP + 2]);
		break;
		case (byte) 0b1_1_000_110:
			processor.r6 = shortValue(Memory.ram[processor.IP + 1], Memory.ram[processor.IP + 2]);
		break;
		case (byte) 0b1_1_000_111:
			processor.r7 = shortValue(Memory.ram[processor.IP + 1], Memory.ram[processor.IP + 2]);
		break;


		default:
			return false;
		}

		processor.IP += 3;
		return true;

	}

	private static boolean pushPop(Processor processor) {

		short strip_signal = (short) 0b0000_0000_1111_1111;

		switch (Memory.ram[processor.IP]) {
		//Push
		case (byte) 0b010_0_0000:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.r0 >> 8) & strip_signal);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.r0 & strip_signal);
			processor.SP += 2;
		}
		break;
		case (byte) 0b010_0_0001:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.r1 >> 8) & strip_signal);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.r1 & strip_signal);
			processor.SP += 2;
		}
		break;
		case (byte) 0b010_0_0010:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.r2 >> 8) & strip_signal);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.r2 & strip_signal);
			processor.SP += 2;
		}
		break;
		case (byte) 0b010_0_0011:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.r3 >> 8) & strip_signal);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.r3 & strip_signal);
			processor.SP += 2;
		}
		break;
		case (byte) 0b010_0_0100:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.r4 >> 8) & strip_signal);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.r4 & strip_signal);
			processor.SP += 2;
		}
		break;
		case (byte) 0b010_0_0101:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.r5 >> 8) & strip_signal);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.r5 & strip_signal);
			processor.SP += 2;
		}
		break;
		case (byte) 0b010_0_0110:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.r6 >> 8) & strip_signal);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.r6 & strip_signal);
			processor.SP += 2;
		}
		break;
		case (byte) 0b010_0_0111:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.r7 >> 8) & strip_signal);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.r7 & strip_signal);
			processor.SP += 2;
		}
		break;
		//Valor numerico.
		case (byte) 0b010_0_1000:{
			Memory.ram[processor.SP + processor.SS]	= (byte) Memory.ram[processor.IP + 1];
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) Memory.ram[processor.IP + 2];
			processor.SP += 2;
		}
		break;


		//PUSH BP
		case (byte) 0b0100_1_001:{
			Memory.ram[processor.SP + processor.SS]	= (byte) ((processor.BP & strip_signal) >> 8);
			Memory.ram[processor.SP + 1 + processor.SS]	= (byte) (processor.BP & strip_signal);
			processor.SP += 2;
		}
		break;	


		//Pop
		case (byte) 0b010_1_0000:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
//			processor.r0 = (short) ((Memory.ram[processor.SP + processor.SS] << 8) | Memory.ram[processor.SP + 1 + processor.SS]);
			processor.r0 = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;
		case (byte) 0b010_1_0001:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
			processor.r1 = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;
		case (byte) 0b010_1_0010:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
			processor.r2 = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;
		case (byte) 0b010_1_0011:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
			processor.r3 = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;
		case (byte) 0b010_1_0100:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
			processor.r4 = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;
		case (byte) 0b010_1_0101:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
			processor.r5 = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;
		case (byte) 0b010_1_0110:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
			processor.r6 = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;
		case (byte) 0b010_1_0111:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
			processor.r7 = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;

		//POP BP
		case (byte) 0b0101_1_000:{
			if(processor.SP == 0){
				new W16Exception(TipeException.OVERFLOW_SS);
				return false;
			}
			processor.SP -= 2;
			processor.BP = shortValue(Memory.ram[processor.SP + processor.SS], Memory.ram[processor.SP + 1 + processor.SS]);
		}
		break;

		default:
			return false;
		}

		processor.IP += 3;
		return true;
	}

	private static boolean executeLoadBP(Processor processor) {
		short temp = (short) (processor.SS + processor.BP + shortValue(Memory.ram[processor.IP+1], Memory.ram[processor.IP+2]));
		// Loads do BP para registradores de uso geral.
		switch (Memory.ram[processor.IP]) {
		case (byte) 0b0110_0_000:
			processor.r0 = shortValue(Memory.ram[temp], Memory.ram[temp+1]);
		break;
		case (byte) 0b0110_0_001:
			processor.r1 = shortValue(Memory.ram[temp], Memory.ram[temp+1]);
		break;
		case (byte) 0b0110_0_010:
			processor.r2 = shortValue(Memory.ram[temp], Memory.ram[temp+1]);
		break;
		case (byte) 0b0110_0_011:
			processor.r3 = shortValue(Memory.ram[temp], Memory.ram[temp+1]);
		break;
		case (byte) 0b0110_0_100:
			processor.r4 = shortValue(Memory.ram[temp], Memory.ram[temp+1]);
		break;
		case (byte) 0b0110_0_101:
			processor.r5 = shortValue(Memory.ram[temp], Memory.ram[temp+1]);
		break;
		case (byte) 0b0110_0_110:
			processor.r6 = shortValue(Memory.ram[temp], Memory.ram[temp+1]);
		break;
		case (byte) 0b0110_0_111:
			processor.r7 = shortValue(Memory.ram[temp], Memory.ram[temp+1]);
		break;

		default:
			return false;
		}

		processor.IP += 3;
		return true;
	}

	private static boolean compares(Processor processor) {

		switch (Memory.ram[processor.IP+1]) {
		case 0b00_000_001:{
			if(processor.r0 == processor.r1){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r0 > processor.r1){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_000_010:{
			if(processor.r0 == processor.r2){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r0 > processor.r2){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_000_011:{
			if(processor.r0 == processor.r3){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r0 > processor.r3){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_000_100:{
			if(processor.r0 == processor.r4){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r0 > processor.r4){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_000_101:{
			if(processor.r0 == processor.r5){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r0 > processor.r5){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_000_110:{
			if(processor.r0 == processor.r6){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r0 > processor.r6){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_000_111:{
			if(processor.r0 == processor.r7){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r0 > processor.r7){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_001_000:{
			if(processor.r1 == processor.r0){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r1 > processor.r0){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_001_010:{
			if(processor.r1 == processor.r2){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r1 > processor.r2){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_001_011:{
			if(processor.r1 == processor.r3){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r1 > processor.r3){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_001_100:{
			if(processor.r1 == processor.r4){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r1 > processor.r4){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_001_101:{
			if(processor.r1 == processor.r5){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r1 > processor.r5){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_001_110:{
			if(processor.r1 == processor.r6){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r1 > processor.r6){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_001_111:{
			if(processor.r1 == processor.r7){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r1 > processor.r7){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_010_000:{
			if(processor.r2 == processor.r0){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r2 > processor.r0){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_010_001:{
			if(processor.r2 == processor.r1){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r2 > processor.r1){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_010_011:{
			if(processor.r2 == processor.r3){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r2 > processor.r3){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_010_100:{
			if(processor.r2 == processor.r4){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r2 > processor.r4){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_010_101:{
			if(processor.r2 == processor.r5){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r2 > processor.r5){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_010_110:{
			if(processor.r2 == processor.r6){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r2 > processor.r6){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_010_111:{
			if(processor.r2 == processor.r7){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r2 > processor.r7){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_011_000:{
			if(processor.r3 == processor.r0){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r3 > processor.r0){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_011_001:{
			if(processor.r3 == processor.r1){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r3 > processor.r1){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_011_010:{
			if(processor.r3 == processor.r2){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r3 > processor.r2){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_011_100:{
			if(processor.r3 == processor.r4){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r3 > processor.r4){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_011_101:{
			if(processor.r3 == processor.r5){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r3 > processor.r5){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_011_110:{
			if(processor.r3 == processor.r6){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r3 > processor.r6){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_011_111:{
			if(processor.r3 == processor.r7){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r3 > processor.r7){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_100_000:{
			if(processor.r4 == processor.r0){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r4 > processor.r0){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_100_001:{
			if(processor.r4 == processor.r1){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r4 > processor.r1){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_100_010:{
			if(processor.r4 == processor.r2){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r4 > processor.r2){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_100_011:{
			if(processor.r4 == processor.r3){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r4 > processor.r3){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_100_101:{
			if(processor.r4 == processor.r5){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r4 > processor.r5){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_100_110:{
			if(processor.r4 == processor.r6){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r4 > processor.r6){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_100_111:{
			if(processor.r4 == processor.r7){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r4 > processor.r7){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_101_000:{
			if(processor.r5 == processor.r0){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r5 > processor.r0){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_101_001:{
			if(processor.r5 == processor.r1){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r5 > processor.r1){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_101_010:{
			if(processor.r5 == processor.r2){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r5 > processor.r2){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_101_011:{
			if(processor.r5 == processor.r3){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r5 > processor.r3){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_101_100:{
			if(processor.r5 == processor.r4){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r5 > processor.r4){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_101_110:{
			if(processor.r5 == processor.r6){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r5 > processor.r6){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_101_111:{
			if(processor.r5 == processor.r7){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r5 > processor.r7){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_110_000:{
			if(processor.r6 == processor.r0){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r6 > processor.r0){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_110_001:{
			if(processor.r6 == processor.r1){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r6 > processor.r1){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_110_010:{
			if(processor.r6 == processor.r2){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r6 > processor.r2){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_110_011:{
			if(processor.r6 == processor.r3){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r6 > processor.r3){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_110_100:{
			if(processor.r6 == processor.r4){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r6 > processor.r4){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_110_101:{
			if(processor.r6 == processor.r5){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r6 > processor.r5){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_110_111:{
			if(processor.r6 == processor.r7){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r6 > processor.r7){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_111_000:{
			if(processor.r7 == processor.r0){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r7 > processor.r0){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_111_001:{
			if(processor.r7 == processor.r1){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r7 > processor.r1){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_111_010:{
			if(processor.r7 == processor.r2){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r7 > processor.r2){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_111_011:{
			if(processor.r7 == processor.r3){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r7 > processor.r3){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_111_100:{
			if(processor.r7 == processor.r4){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r7 > processor.r4){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_111_101:{
			if(processor.r7 == processor.r5){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r7 > processor.r5){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		case 0b00_111_110:{
			if(processor.r7 == processor.r6){
				processor.FLAG_Z = Flag._1.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else if(processor.r7 > processor.r6){
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._0.booleanValue();
			}else{
				processor.FLAG_Z = Flag._0.booleanValue();
				processor.FLAG_S = Flag._1.booleanValue();
			}
		}break;
		default:
			return false;
		}

		processor.IP += 3;
		return true;
	}

	private static boolean jumps(Processor processor) {

		switch (Memory.ram[processor.IP]) {
		case 0b0010_0000:{
			processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;
		//je (==) 0010_0001 vvvv_vvvv vvvv_vvvv
		case 0b0010_0001:{
			if((processor.FLAG_Z == Flag._1.booleanValue()) && (processor.FLAG_S == Flag._0.booleanValue()))
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;
		//jne (!=) 0010_0010 vvvv_vvvv vvvv_vvvv
		case 0b0010_0010:{
			if(processor.FLAG_Z == Flag._0.booleanValue())
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;
		//jg (>) 0010_0011 vvvv_vvvv vvvv_vvvv
		case 0b0010_0011:{
			if((processor.FLAG_Z == Flag._0.booleanValue()) && (processor.FLAG_S == Flag._0.booleanValue()))
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;
		//jge (>=) 0010_0100 vvvv_vvvv vvvv_vvvv
		case 0b0010_0100:{
			//(==)
			if((processor.FLAG_Z == Flag._1.booleanValue()) && (processor.FLAG_S == Flag._0.booleanValue()))
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
			//(>)
			else if((processor.FLAG_Z == Flag._0.booleanValue()) && (processor.FLAG_S == Flag._0.booleanValue()))
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;
		//jl (<) 0010_0101 vvvv_vvvv vvvv_vvvv
		case 0b0010_0101:{
			if((processor.FLAG_Z == Flag._0.booleanValue()) && (processor.FLAG_S == Flag._1.booleanValue()))
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;
		//jle (<=) 0010_0110 vvvv_vvvv vvvv_vvvv
		case 0b0010_0110:{
			//(==)
			if((processor.FLAG_Z == Flag._1.booleanValue()) && (processor.FLAG_S == Flag._0.booleanValue()))
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
			//(<)
			else if((processor.FLAG_Z == Flag._0.booleanValue()) && (processor.FLAG_S == Flag._1.booleanValue()))
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;
		//jnz (!0) 0010_0111 vvvv_vvvv vvvv_vvvv
		case 0b0010_0111:{
			if(!processor.FLAG_Z)
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;
		//jz (0) 0010_1000 vvvv_vvvv vvvv_vvvv
		case 0b0010_1000:{
			if(processor.FLAG_Z)
				processor.IP = (short) (processor.CS + ((short) (((short) Memory.ram[processor.IP+1] << 8) | Memory.ram[processor.IP+2])));
		}
		break;

		default:
			return false;
		}

		processor.IP += 3;
		return true;
	}

	private static boolean executeAddSubMulDiv(Processor processor) {

		switch (Memory.ram[processor.IP+1]) {
		//ADD
		case 0b00_000_001:{
			processor.TEMP = processor.r0 += processor.r1;
		}break;
		case 0b00_000_010:{
			processor.TEMP = processor.r0 += processor.r2;
		}break;
		case 0b00_000_011:{
			processor.TEMP = processor.r0 += processor.r3;
		}break;
		case 0b00_000_100:{
			processor.TEMP = processor.r0 += processor.r4;
		}break;
		case 0b00_000_101:{
			processor.TEMP = processor.r0 += processor.r5;
		}break;
		case 0b00_000_110:{
			processor.TEMP = processor.r0 += processor.r6;
		}break;
		case 0b00_000_111:{
			processor.TEMP = processor.r0 += processor.r7;
		}break;
		case 0b00_001_000:{
			processor.TEMP = processor.r1 += processor.r0;
		}break;
		case 0b00_001_010:{
			processor.TEMP = processor.r1 += processor.r2;
		}break;
		case 0b00_001_011:{
			processor.TEMP = processor.r1 += processor.r3;
		}break;
		case 0b00_001_100:{
			processor.TEMP = processor.r1 += processor.r4;
		}break;
		case 0b00_001_101:{
			processor.TEMP = processor.r1 += processor.r5;
		}break;
		case 0b00_001_110:{
			processor.TEMP = processor.r1 += processor.r6;
		}break;
		case 0b00_001_111:{
			processor.TEMP = processor.r1 += processor.r7;
		}break;
		case 0b00_010_000:{
			processor.TEMP = processor.r2 += processor.r0;
		}break;
		case 0b00_010_001:{
			processor.TEMP = processor.r2 += processor.r1;
		}break;
		case 0b00_010_011:{
			processor.TEMP = processor.r2 += processor.r3;
		}break;
		case 0b00_010_100:{
			processor.TEMP = processor.r2 += processor.r4;
		}break;
		case 0b00_010_101:{
			processor.TEMP = processor.r2 += processor.r5;
		}break;
		case 0b00_010_110:{
			processor.TEMP = processor.r2 += processor.r6;
		}break;
		case 0b00_010_111:{
			processor.TEMP = processor.r2 += processor.r7;
		}break;
		case 0b00_011_000:{
			processor.TEMP = processor.r3 += processor.r0;
		}break;
		case 0b00_011_001:{
			processor.TEMP = processor.r3 += processor.r1;
		}break;
		case 0b00_011_010:{
			processor.TEMP = processor.r3 += processor.r2;
		}break;
		case 0b00_011_100:{
			processor.TEMP = processor.r3 += processor.r4;
		}break;
		case 0b00_011_101:{
			processor.TEMP = processor.r3 += processor.r5;
		}break;
		case 0b00_011_110:{
			processor.TEMP = processor.r3 += processor.r6;
		}break;
		case 0b00_011_111:{
			processor.TEMP = processor.r3 += processor.r7;
		}break;
		case 0b00_100_000:{
			processor.TEMP = processor.r4 += processor.r0;
		}break;
		case 0b00_100_001:{
			processor.TEMP = processor.r4 += processor.r1;
		}break;
		case 0b00_100_010:{
			processor.TEMP = processor.r4 += processor.r2;
		}break;
		case 0b00_100_011:{
			processor.TEMP = processor.r4 += processor.r3;
		}break;
		case 0b00_100_101:{
			processor.TEMP = processor.r4 += processor.r5;
		}break;
		case 0b00_100_110:{
			processor.TEMP = processor.r4 += processor.r6;
		}break;
		case 0b00_100_111:{
			processor.TEMP = processor.r4 += processor.r7;
		}break;
		case 0b00_101_000:{
			processor.TEMP = processor.r5 += processor.r0;
		}break;
		case 0b00_101_001:{
			processor.TEMP = processor.r5 += processor.r1;
		}break;
		case 0b00_101_010:{
			processor.TEMP = processor.r5 += processor.r2;
		}break;
		case 0b00_101_011:{
			processor.TEMP = processor.r5 += processor.r3;
		}break;
		case 0b00_101_100:{
			processor.TEMP = processor.r5 += processor.r4;
		}break;
		case 0b00_101_110:{
			processor.TEMP = processor.r5 += processor.r6;
		}break;
		case 0b00_101_111:{
			processor.TEMP = processor.r5 += processor.r7;
		}break;
		case 0b00_110_000:{
			processor.TEMP = processor.r6 += processor.r0;
		}break;
		case 0b00_110_001:{
			processor.TEMP = processor.r6 += processor.r1;
		}break;
		case 0b00_110_010:{
			processor.TEMP = processor.r6 += processor.r2;
		}break;
		case 0b00_110_011:{
			processor.TEMP = processor.r6 += processor.r3;
		}break;
		case 0b00_110_100:{
			processor.TEMP = processor.r6 += processor.r4;
		}break;
		case 0b00_110_101:{
			processor.TEMP = processor.r6 += processor.r5;
		}break;
		case 0b00_110_111:{
			processor.TEMP = processor.r6 += processor.r7;
		}break;
		case 0b00_111_000:{
			processor.TEMP = processor.r7 += processor.r0;
		}break;
		case 0b00_111_001:{
			processor.TEMP = processor.r7 += processor.r1;
		}break;
		case 0b00_111_010:{
			processor.TEMP = processor.r7 += processor.r2;
		}break;
		case 0b00_111_011:{
			processor.TEMP = processor.r7 += processor.r3;
		}break;
		case 0b00_111_100:{
			processor.TEMP = processor.r7 += processor.r4;
		}break;
		case 0b00_111_101:{
			processor.TEMP = processor.r7 += processor.r5;
		}break;
		case 0b00_111_110:{
			processor.TEMP = processor.r7 += processor.r6;
		}break;
		//SUB
		case 0b01_000_001:{
			processor.TEMP = processor.r0 -= processor.r1;
		}break;
		case 0b01_000_010:{
			processor.TEMP = processor.r0 -= processor.r2;
		}break;
		case 0b01_000_011:{
			processor.TEMP = processor.r0 -= processor.r3;
		}break;
		case 0b01_000_100:{
			processor.TEMP = processor.r0 -= processor.r4;
		}break;
		case 0b01_000_101:{
			processor.TEMP = processor.r0 -= processor.r5;
		}break;
		case 0b01_000_110:{
			processor.TEMP = processor.r0 -= processor.r6;
		}break;
		case 0b01_000_111:{
			processor.TEMP = processor.r0 -= processor.r7;
		}break;
		case 0b01_001_000:{
			processor.TEMP = processor.r1 -= processor.r0;
		}break;
		case 0b01_001_010:{
			processor.TEMP = processor.r1 -= processor.r2;
		}break;
		case 0b01_001_011:{
			processor.TEMP = processor.r1 -= processor.r3;
		}break;
		case 0b01_001_100:{
			processor.TEMP = processor.r1 -= processor.r4;
		}break;
		case 0b01_001_101:{
			processor.TEMP = processor.r1 -= processor.r5;
		}break;
		case 0b01_001_110:{
			processor.TEMP = processor.r1 -= processor.r6;
		}break;
		case 0b01_001_111:{
			processor.TEMP = processor.r1 -= processor.r7;
		}break;
		case 0b01_010_000:{
			processor.TEMP = processor.r2 -= processor.r0;
		}break;
		case 0b01_010_001:{
			processor.TEMP = processor.r2 -= processor.r1;
		}break;
		case 0b01_010_011:{
			processor.TEMP = processor.r2 -= processor.r3;
		}break;
		case 0b01_010_100:{
			processor.TEMP = processor.r2 -= processor.r4;
		}break;
		case 0b01_010_101:{
			processor.TEMP = processor.r2 -= processor.r5;
		}break;
		case 0b01_010_110:{
			processor.TEMP = processor.r2 -= processor.r6;
		}break;
		case 0b01_010_111:{
			processor.TEMP = processor.r2 -= processor.r7;
		}break;
		case 0b01_011_000:{
			processor.TEMP = processor.r3 -= processor.r0;
		}break;
		case 0b01_011_001:{
			processor.TEMP = processor.r3 -= processor.r1;
		}break;
		case 0b01_011_010:{
			processor.TEMP = processor.r3 -= processor.r2;
		}break;
		case 0b01_011_100:{
			processor.TEMP = processor.r3 -= processor.r4;
		}break;
		case 0b01_011_101:{
			processor.TEMP = processor.r3 -= processor.r5;
		}break;
		case 0b01_011_110:{
			processor.TEMP = processor.r3 -= processor.r6;
		}break;
		case 0b01_011_111:{
			processor.TEMP = processor.r3 -= processor.r7;
		}break;
		case 0b01_100_000:{
			processor.TEMP = processor.r4 -= processor.r0;
		}break;
		case 0b01_100_001:{
			processor.TEMP = processor.r4 -= processor.r1;
		}break;
		case 0b01_100_010:{
			processor.TEMP = processor.r4 -= processor.r2;
		}break;
		case 0b01_100_011:{
			processor.TEMP = processor.r4 -= processor.r3;
		}break;
		case 0b01_100_101:{
			processor.TEMP = processor.r4 -= processor.r5;
		}break;
		case 0b01_100_110:{
			processor.TEMP = processor.r4 -= processor.r6;
		}break;
		case 0b01_100_111:{
			processor.TEMP = processor.r4 -= processor.r7;
		}break;
		case 0b01_101_000:{
			processor.TEMP = processor.r5 -= processor.r0;
		}break;
		case 0b01_101_001:{
			processor.TEMP = processor.r5 -= processor.r1;
		}break;
		case 0b01_101_010:{
			processor.TEMP = processor.r5 -= processor.r2;
		}break;
		case 0b01_101_011:{
			processor.TEMP = processor.r5 -= processor.r3;
		}break;
		case 0b01_101_100:{
			processor.TEMP = processor.r5 -= processor.r4;
		}break;
		case 0b01_101_110:{
			processor.TEMP = processor.r5 -= processor.r6;
		}break;
		case 0b01_101_111:{
			processor.TEMP = processor.r5 -= processor.r7;
		}break;
		case 0b01_110_000:{
			processor.TEMP = processor.r6 -= processor.r0;
		}break;
		case 0b01_110_001:{
			processor.TEMP = processor.r6 -= processor.r1;
		}break;
		case 0b01_110_010:{
			processor.TEMP = processor.r6 -= processor.r2;
		}break;
		case 0b01_110_011:{
			processor.TEMP = processor.r6 -= processor.r3;
		}break;
		case 0b01_110_100:{
			processor.TEMP = processor.r6 -= processor.r4;
		}break;
		case 0b01_110_101:{
			processor.TEMP = processor.r6 -= processor.r5;
		}break;
		case 0b01_110_111:{
			processor.TEMP = processor.r6 -= processor.r7;
		}break;
		case 0b01_111_000:{
			processor.TEMP = processor.r7 -= processor.r0;
		}break;
		case 0b01_111_001:{
			processor.TEMP = processor.r7 -= processor.r1;
		}break;
		case 0b01_111_010:{
			processor.TEMP = processor.r7 -= processor.r2;
		}break;
		case 0b01_111_011:{
			processor.TEMP = processor.r7 -= processor.r3;
		}break;
		case 0b01_111_100:{
			processor.TEMP = processor.r7 -= processor.r4;
		}break;
		case 0b01_111_101:{
			processor.TEMP = processor.r7 -= processor.r5;
		}break;
		case 0b01_111_110:{
			processor.TEMP = processor.r7 -= processor.r6;
		}break;
		//MUL
		case (byte) 0b10_000_001:{
			processor.TEMP = processor.r0 *= processor.r1;
		}break;
		case (byte) 0b10_000_010:{
			processor.TEMP = processor.r0 *= processor.r2;
		}break;
		case (byte) 0b10_000_011:{
			processor.TEMP = processor.r0 *= processor.r3;
		}break;
		case (byte) 0b10_000_100:{
			processor.TEMP = processor.r0 *= processor.r4;
		}break;
		case (byte) 0b10_000_101:{
			processor.TEMP = processor.r0 *= processor.r5;
		}break;
		case (byte) 0b10_000_110:{
			processor.TEMP = processor.r0 *= processor.r6;
		}break;
		case (byte) 0b10_000_111:{
			processor.TEMP = processor.r0 *= processor.r7;
		}break;
		case (byte) 0b10_001_000:{
			processor.TEMP = processor.r1 *= processor.r0;
		}break;
		case (byte) 0b10_001_010:{
			processor.TEMP = processor.r1 *= processor.r2;
		}break;
		case (byte) 0b10_001_011:{
			processor.TEMP = processor.r1 *= processor.r3;
		}break;
		case (byte) 0b10_001_100:{
			processor.TEMP = processor.r1 *= processor.r4;
		}break;
		case (byte) 0b10_001_101:{
			processor.TEMP = processor.r1 *= processor.r5;
		}break;
		case (byte) 0b10_001_110:{
			processor.TEMP = processor.r1 *= processor.r6;
		}break;
		case (byte) 0b10_001_111:{
			processor.TEMP = processor.r1 *= processor.r7;
		}break;
		case (byte) 0b10_010_000:{
			processor.TEMP = processor.r2 *= processor.r0;
		}break;
		case (byte) 0b10_010_001:{
			processor.TEMP = processor.r2 *= processor.r1;
		}break;
		case (byte) 0b10_010_011:{
			processor.TEMP = processor.r2 *= processor.r3;
		}break;
		case (byte) 0b10_010_100:{
			processor.TEMP = processor.r2 *= processor.r4;
		}break;
		case (byte) 0b10_010_101:{
			processor.TEMP = processor.r2 *= processor.r5;
		}break;
		case (byte) 0b10_010_110:{
			processor.TEMP = processor.r2 *= processor.r6;
		}break;
		case (byte) 0b10_010_111:{
			processor.TEMP = processor.r2 *= processor.r7;
		}break;
		case (byte) 0b10_011_000:{
			processor.TEMP = processor.r3 *= processor.r0;
		}break;
		case (byte) 0b10_011_001:{
			processor.TEMP = processor.r3 *= processor.r1;
		}break;
		case (byte) 0b10_011_010:{
			processor.TEMP = processor.r3 *= processor.r2;
		}break;
		case (byte) 0b10_011_100:{
			processor.TEMP = processor.r3 *= processor.r4;
		}break;
		case (byte) 0b10_011_101:{
			processor.TEMP = processor.r3 *= processor.r5;
		}break;
		case (byte) 0b10_011_110:{
			processor.TEMP = processor.r3 *= processor.r6;
		}break;
		case (byte) 0b10_011_111:{
			processor.TEMP = processor.r3 *= processor.r7;
		}break;
		case (byte) 0b10_100_000:{
			processor.TEMP = processor.r4 *= processor.r0;
		}break;
		case (byte) 0b10_100_001:{
			processor.TEMP = processor.r4 *= processor.r1;
		}break;
		case (byte) 0b10_100_010:{
			processor.TEMP = processor.r4 *= processor.r2;
		}break;
		case (byte) 0b10_100_011:{
			processor.TEMP = processor.r4 *= processor.r3;
		}break;
		case (byte) 0b10_100_101:{
			processor.TEMP = processor.r4 *= processor.r5;
		}break;
		case (byte) 0b10_100_110:{
			processor.TEMP = processor.r4 *= processor.r6;
		}break;
		case (byte) 0b10_100_111:{
			processor.TEMP = processor.r4 *= processor.r7;
		}break;
		case (byte) 0b10_101_000:{
			processor.TEMP = processor.r5 *= processor.r0;
		}break;
		case (byte) 0b10_101_001:{
			processor.TEMP = processor.r5 *= processor.r1;
		}break;
		case (byte) 0b10_101_010:{
			processor.TEMP = processor.r5 *= processor.r2;
		}break;
		case (byte) 0b10_101_011:{
			processor.TEMP = processor.r5 *= processor.r3;
		}break;
		case (byte) 0b10_101_100:{
			processor.TEMP = processor.r5 *= processor.r4;
		}break;
		case (byte) 0b10_101_110:{
			processor.TEMP = processor.r5 *= processor.r6;
		}break;
		case (byte) 0b10_101_111:{
			processor.TEMP = processor.r5 *= processor.r7;
		}break;
		case (byte) 0b10_110_000:{
			processor.TEMP = processor.r6 *= processor.r0;
		}break;
		case (byte) 0b10_110_001:{
			processor.TEMP = processor.r6 *= processor.r1;
		}break;
		case (byte) 0b10_110_010:{
			processor.TEMP = processor.r6 *= processor.r2;
		}break;
		case (byte) 0b10_110_011:{
			processor.TEMP = processor.r6 *= processor.r3;
		}break;
		case (byte) 0b10_110_100:{
			processor.TEMP = processor.r6 *= processor.r4;
		}break;
		case (byte) 0b10_110_101:{
			processor.TEMP = processor.r6 *= processor.r5;
		}break;
		case (byte) 0b10_110_111:{
			processor.TEMP = processor.r6 *= processor.r7;
		}break;
		case (byte) 0b10_111_000:{
			processor.TEMP = processor.r7 *= processor.r0;
		}break;
		case (byte) 0b10_111_001:{
			processor.TEMP = processor.r7 *= processor.r1;
		}break;
		case (byte) 0b10_111_010:{
			processor.TEMP = processor.r7 *= processor.r2;
		}break;
		case (byte) 0b10_111_011:{
			processor.TEMP = processor.r7 *= processor.r3;
		}break;
		case (byte) 0b10_111_100:{
			processor.TEMP = processor.r7 *= processor.r4;
		}break;
		case (byte) 0b10_111_101:{
			processor.TEMP = processor.r7 *= processor.r5;
		}break;
		case (byte) 0b10_111_110:{
			processor.TEMP = processor.r7 *= processor.r6;
		}break;
		//DIV
		case (byte) 0b11_000_001:{
			if(processor.r1 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r0 /= processor.r1;
		}break;
		case (byte) 0b11_000_010:{
			if(processor.r2 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r0 /= processor.r2;
		}break;
		case (byte) 0b11_000_011:{
			if(processor.r3 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r0 /= processor.r3;
		}break;
		case (byte) 0b11_000_100:{
			if(processor.r4 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r0 /= processor.r4;
		}break;
		case (byte) 0b11_000_101:{
			if(processor.r5 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r0 /= processor.r5;
		}break;
		case (byte) 0b11_000_110:{
			if(processor.r6 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r0 /= processor.r6;
		}break;
		case (byte) 0b11_000_111:{
			if(processor.r7 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r0 /= processor.r7;
		}break;
		case (byte) 0b11_001_000:{
			if(processor.r0 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r1 /= processor.r0;
		}break;
		case (byte) 0b11_001_010:{
			if(processor.r2 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r1 /= processor.r2;
		}break;
		case (byte) 0b11_001_011:{
			if(processor.r3 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r1 /= processor.r3;
		}break;
		case (byte) 0b11_001_100:{
			if(processor.r4 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r1 /= processor.r4;
		}break;
		case (byte) 0b11_001_101:{
			if(processor.r5 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r1 /= processor.r5;
		}break;
		case (byte) 0b11_001_110:{
			if(processor.r6 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r1 /= processor.r6;
		}break;
		case (byte) 0b11_001_111:{
			if(processor.r7 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r1 /= processor.r7;
		}break;
		case (byte) 0b11_010_000:{
			if(processor.r0 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r2 /= processor.r0;
		}break;
		case (byte) 0b11_010_001:{
			if(processor.r1 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r2 /= processor.r1;
		}break;
		case (byte) 0b11_010_011:{
			if(processor.r3 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r2 /= processor.r3;
		}break;
		case (byte) 0b11_010_100:{
			if(processor.r4 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r2 /= processor.r4;
		}break;
		case (byte) 0b11_010_101:{
			if(processor.r5 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r2 /= processor.r5;
		}break;
		case (byte) 0b11_010_110:{
			if(processor.r6 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r2 /= processor.r6;
		}break;
		case (byte) 0b11_010_111:{
			if(processor.r7 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r2 /= processor.r7;
		}break;
		case (byte) 0b11_011_000:{
			if(processor.r0 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r3 /= processor.r0;
		}break;
		case (byte) 0b11_011_001:{
			if(processor.r1 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r3 /= processor.r1;
		}break;
		case (byte) 0b11_011_010:{
			if(processor.r2 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r3 /= processor.r2;
		}break;
		case (byte) 0b11_011_100:{
			if(processor.r4 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r3 /= processor.r4;
		}break;
		case (byte) 0b11_011_101:{
			if(processor.r5 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r3 /= processor.r5;
		}break;
		case (byte) 0b11_011_110:{
			if(processor.r6 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r3 /= processor.r6;
		}break;
		case (byte) 0b11_011_111:{
			if(processor.r7 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r3 /= processor.r7;
		}break;
		case (byte) 0b11_100_000:{
			if(processor.r0 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r4 /= processor.r0;
		}break;
		case (byte) 0b11_100_001:{
			if(processor.r1 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r4 /= processor.r1;
		}break;
		case (byte) 0b11_100_010:{
			if(processor.r2 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r4 /= processor.r2;
		}break;
		case (byte) 0b11_100_011:{
			if(processor.r3 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r4 /= processor.r3;
		}break;
		case (byte) 0b11_100_101:{
			if(processor.r5 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r4 /= processor.r5;
		}break;
		case (byte) 0b11_100_110:{
			if(processor.r6 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r4 /= processor.r6;
		}break;
		case (byte) 0b11_100_111:{
			if(processor.r7 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r4 /= processor.r7;
		}break;
		case (byte) 0b11_101_000:{
			if(processor.r0 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r5 /= processor.r0;
		}break;
		case (byte) 0b11_101_001:{
			if(processor.r1 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r5 /= processor.r1;
		}break;
		case (byte) 0b11_101_010:{
			if(processor.r2 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r5 /= processor.r2;
		}break;
		case (byte) 0b11_101_011:{
			if(processor.r3 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r5 /= processor.r3;
		}break;
		case (byte) 0b11_101_100:{
			if(processor.r4 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r5 /= processor.r4;
		}break;
		case (byte) 0b11_101_110:{
			if(processor.r6 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r5 /= processor.r6;
		}break;
		case (byte) 0b11_101_111:{
			if(processor.r7 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r5 /= processor.r7;
		}break;
		case (byte) 0b11_110_000:{
			if(processor.r0 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r6 /= processor.r0;
		}break;
		case (byte) 0b11_110_001:{
			if(processor.r1 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r6 /= processor.r1;
		}break;
		case (byte) 0b11_110_010:{
			if(processor.r2 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r6 /= processor.r2;
		}break;
		case (byte) 0b11_110_011:{
			if(processor.r3 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r6 /= processor.r3;
		}break;
		case (byte) 0b11_110_100:{
			if(processor.r4 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r6 /= processor.r4;
		}break;
		case (byte) 0b11_110_101:{
			if(processor.r5 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r6 /= processor.r5;
		}break;
		case (byte) 0b11_110_111:{
			if(processor.r7 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r6 /= processor.r7;
		}break;
		case (byte) 0b11_111_000:{
			if(processor.r0 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r7 /= processor.r0;
		}break;
		case (byte) 0b11_111_001:{
			if(processor.r1 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r7 /= processor.r1;
		}break;
		case (byte) 0b11_111_010:{
			if(processor.r2 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r7 /= processor.r2;
		}break;
		case (byte) 0b11_111_011:{
			if(processor.r3 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r7 /= processor.r3;
		}break;
		case (byte) 0b11_111_100:{
			if(processor.r4 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r7 /= processor.r4;
		}break;
		case (byte) 0b11_111_101:{
			if(processor.r5 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r7 /= processor.r5;
		}break;
		case (byte) 0b11_111_110:{
			if(processor.r6 == 0){
				new W16Exception(TipeException.DIVISION_BY_ZERO);
				return false;
			}else
				processor.TEMP = processor.r7 /= processor.r6;
		}break;
		default:
			return false;
		}

		executeFlag(processor);

		processor.IP += 3;
		return true;

	}

	private static void executeFlag(Processor processor) {
		if(processor.TEMP == 0)
			processor.FLAG_Z = true;
		else if (processor.TEMP > 0) {
			processor.FLAG_Z = false;
			processor.FLAG_S = true;
		}else{
			processor.FLAG_Z = false;
			processor.FLAG_S = false;
		}
	}

	private static boolean executeLoadStore(Processor processor) {

		short remove_signal = 0b00000000_11111111;

		switch (Memory.ram[processor.IP]) {
		//Loads
		case (byte) 0x00:
			processor.r0 = (short) (Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))] | Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))+1]);
		break;
		case (byte) 0x01:
			processor.r1 = (short) (Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))] | Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))+1]);
		break;
		case (byte) 0x02:
			processor.r2 = (short) (Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))] | Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))+1]);
		break;
		case (byte) 0x03:
			processor.r3 = (short) (Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))] | Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))+1]);
		break;
		case (byte) 0x04:
			processor.r4 = (short) (Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))] | Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))+1]);
		break;
		case (byte) 0x05:
			processor.r5 = (short) (Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))] | Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))+1]);
		break;
		case (byte) 0x06:
			processor.r6 = (short) (Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))] | Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))+1]);
		break;
		case (byte) 0x07:
			processor.r7 = (short) (Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))] | Memory.ram[(processor.DS + (Memory.ram[processor.IP+1] << 8 | Memory.ram[processor.IP+2]))+1]);
		break;
		//Stores
		case (byte) 0x08:{
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2]))] = (byte) ((processor.r0 >>> 8) & remove_signal);
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2])) + 1] = (byte) processor.r0;
		}break;
		case (byte) 0x09:{
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2]))] = (byte) ((processor.r0 >>> 8) & remove_signal);
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2])) + 1] = (byte) processor.r1;
		}break;
		case (byte) 0x0A:{
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2]))] = (byte) ((processor.r0 >>> 8) & remove_signal);
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2])) + 1] = (byte) processor.r2;
		}break;
		case (byte) 0x0B:{
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2]))] = (byte) ((processor.r0 >>> 8) & remove_signal);
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2])) + 1] = (byte) processor.r3;
		}break;
		case (byte) 0x0C:{
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2]))] = (byte) ((processor.r0 >>> 8) & remove_signal);
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2])) + 1] = (byte) processor.r4;
		}break;
		case (byte) 0x0D:{
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2]))] = (byte) ((processor.r0 >>> 8) & remove_signal);
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2])) + 1] = (byte) processor.r5;
		}break;
		case (byte) 0x0E:{
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2]))] = (byte) ((processor.r0 >>> 8) & remove_signal);
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2])) + 1] = (byte) processor.r6;
		}break;
		case (byte) 0x0F:{
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2]))] = (byte) ((processor.r0 >>> 8) & remove_signal);
			Memory.ram[processor.DS + ((((short)Memory.ram[processor.IP+1])<<8) | ((short)Memory.ram[processor.IP+2])) + 1] = (byte) processor.r7;
		}break;

		default:
			return false;
		}

		processor.IP += 3;
		return true;
	}

	/**
	 * Used to join two bytes and returns a short of value.
	 * @param b1
	 * @param b2
	 * @return short
	 */
	private static short shortValue(byte b1, byte b2){
		short strip_signal = 0b00000000_11111111;

		short short1 = (short) (b1 & strip_signal);
		short1 = (short) (short1 << 8);
		short short2 = (short) (b2 & strip_signal);
		
		short short3 = (short) (short1 | short2);

		return short3;
	}
}

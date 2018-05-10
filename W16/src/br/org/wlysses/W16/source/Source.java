package br.org.wlysses.W16.source;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import br.org.wlysses.W16.machine.Memory;
import br.org.wlysses.W16.machine.Processor;


/**
 * Class to use the files on disk.
 * @author Wlysses Pereira
 */
public class Source {

	/**
	 * Inserts the binary code contained in the file on disk
	 * to main memory instructions.
	 * And returns a processor with the necessary data to run the code.
	 * @param args
	 * @return {@link Processor}
	 */
	public static Processor insertCodeInRam(String args) {

		Processor processor = null;

		if(!isExecutable(args)){
			System.out.println("Binary error");
			return null;
		}

		processor = setAreas(args);

		return processor;
	}


	/**
	 * Insert the code into RAM and returns a processor configured to execute the program.
	 * @param args
	 * @return {@link Processor}
	 */
	private static Processor setAreas(String args) {
		DataInputStream dis = null;
		Processor processor = new Processor((byte) 0); 

		try {
			dis = new DataInputStream(new FileInputStream(args));

			//Descarta a palavra magica.
			dis.readByte();
			dis.readByte();
			dis.readByte();


			/*
			 * Areas de códico, dados e pilha respectivamente.
			 */
			processor.CS = dis.readShort();
			processor.DS = dis.readShort();
			processor.SS = dis.readShort();


			processor.IP = processor.CS;

			processor.BP = 0;
			processor.SP = processor.BP;


			/*
			 * Area de codigo na RAM.
			 */
			//Area de codigo.
			short posInRam = processor.CS;

			byte auxB = 0;
			while((auxB = dis.readByte()) != ((byte)0b11111111)){
				Memory.ram[posInRam] = auxB;
				Memory.ram[posInRam+1] = dis.readByte();
				Memory.ram[posInRam+2] = dis.readByte();
				posInRam += 3;
			}


			/*
			 * Area de dados.
			 */
			posInRam = processor.DS;
			while ((auxB = dis.readByte()) != ((byte)0b11111111)){
				Memory.ram[posInRam] = auxB;
				posInRam++;
			}


			//Retorna um processador com as informaçoes para executar o programa.
			return processor;


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				dis.close();
			} catch (Exception e) {}
		}

		return null;

	}


	/**
	 * Checks whether a file is binary W16
	 * @param args
	 * @return {@link boolean}
	 */
	private static boolean isExecutable(String args) {
		DataInputStream dis = null;

		try {
			dis = new DataInputStream(new FileInputStream(args));

			char magicalWord1 = (char) dis.readByte();
			char magicalWord2 = (char) dis.readByte();
			char magicalWord3 = (char) dis.readByte();

			return (magicalWord1 == 'W') && (magicalWord2 == '1') && (magicalWord3 == '6');

		} catch (FileNotFoundException e) {
			System.out.println("File "+ args +" not exists.");
			return false;
		} catch (IOException e) {
			return false;
		}finally{
			try {
				dis.close();
			} catch (Exception e) {
				return false;
			}
		}
	}
}
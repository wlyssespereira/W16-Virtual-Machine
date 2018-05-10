package br.org.wlysses.W16.machine;

/**
 * Representation of a processor machine.
 * @author Wlysses Pereira
 */
public class Processor {

	private byte pid;

	public Processor(byte pid) {
		this.pid = pid;
	}

	/**
	 * Returns the value of the identifier of the processor.
	 * @return short
	 */
	public short getPid() {
		return pid;
	}
	/**
	 * Inserts an identifier to the processor.
	 * @param pid
	 */
	public void setPid(byte pid) {
		this.pid = pid;
	}


	//General-purpose registers
	public short r0;
	public short r1;
	public short r2;
	public short r3;
	public short r4;
	public short r5;
	public short r6;
	public short r7;


	//Ponteiro de codigo, que percorre a area de código ou CS.
	/**Pointer code, which traverses the area code or CS.*/
	public short IP;


	/**Stack pointer.*/
	public short SP;
	/**Stack base.*/
	public short BP;


	/**Area code.*/
	public short CS;
	/**Area data.*/
	public short DS;
	/**Area stack.*/
	public short SS;

	/**Zero*/
	public boolean FLAG_Z;
	/**Signal*/
	public boolean FLAG_S;

	/**Temporary register.*/
	public short TEMP;


	/**
	 *Used to see the value of the registers. 
	 */
	@Override
	public String toString() {
		return 	"Processor id=(" + pid + "):\n" +
				" R0 = " + r0 +
				" R1 = " + r1 +
				" R2 = " + r2 +
				" R3 = " + r3 +
				" R4 = " + r4 +
				" R5 = " + r5 +
				" R6 = " + r6 +
				" R7 = " + r7 +
				"\n" + "\n" +
				" CS = " + CS + "\n" +
				" DS = " + DS + "\n" +
				" SS = " + SS + "\n" +
				"\n" +
				" IP = " + IP + "\n" +
				" BP = " + BP + "\n" +
				" SP = " + SP + "\n" +
				"\n" +
				"FLAG_Z = " + FLAG_Z +
				"\n" +
				"FLAG_S = " + FLAG_S +
				"\n" +
				"TEMP = " + TEMP +
				"\n";
		
		
	}


	/**
	 * Returns a processor with the same registers.
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		Processor processor = new Processor(pid);

		processor.r0 = r0;
		processor.r1 = r2;
		processor.r2 = r2;
		processor.r3 = r3;
		processor.r4 = r4;
		processor.r5 = r5;
		processor.r6 = r6;
		processor.r7 = r7;

		processor.IP = IP;
		processor.BP = BP;
		processor.SP = SP;

		processor.CS = CS;
		processor.DS = DS;
		processor.SS = SS;

		processor.FLAG_Z = FLAG_Z;
		processor.FLAG_S = FLAG_S;

		return processor;
	}

}
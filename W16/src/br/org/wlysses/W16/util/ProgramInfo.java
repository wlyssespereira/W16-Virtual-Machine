package br.org.wlysses.W16.util;

import br.org.wlysses.W16.machine.Processor;

public class ProgramInfo {

	Processor processor;

	public ProgramInfo(Processor processor) {
		this.processor = processor;
	}

	@Override
	public String toString() {
		return processor.toString() + "\n" + MemoryUtil.getMemoryInfo(processor);
	}

}

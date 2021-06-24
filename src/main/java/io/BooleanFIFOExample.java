package io;

import java.util.ArrayList;
import java.util.List;

public class BooleanFIFOExample {
	private List<Boolean> fifo;
	private int capacity;
	
	public BooleanFIFOExample(int capacity) {
		this.fifo = new ArrayList<>(capacity);
		this.capacity = capacity;
	}
	
	
	public boolean offer(Boolean value) {
		if (fifo.size() < capacity) {
			fifo.add(value);
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean poll() {
		if (fifo.isEmpty()) {
			return null;
		} else {
			return fifo.remove(fifo.size()-1); 
		}
	}
}

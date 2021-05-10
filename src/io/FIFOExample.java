package io;
import java.util.ArrayList;
import java.util.List;

public class FIFOExample {
	private List<Integer> fifo;
	private int capacity;
	
	public FIFOExample(int capacity) {
		this.fifo = new ArrayList<>(capacity);
		this.capacity = capacity;
	}
	
	
	public boolean offer(Integer value) {
		if (fifo.size() < capacity) {
			fifo.add(value);
			return true;
		} else {
			return false;
		}
	}
	
	public Integer poll() {
		if (fifo.isEmpty()) {
			return null;
		} else {
			return fifo.remove(fifo.size()-1); 
		}
	}
}

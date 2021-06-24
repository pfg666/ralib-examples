package main;

import java.io.File;

import com.mathworks.engine.EngineException;
import com.mathworks.engine.MatlabEngine;

import matlab.LSMSUL;
import matlab.MatlabWrapper;

public class Main {

	public static void main(String[] args) throws EngineException, IllegalArgumentException, IllegalStateException, InterruptedException {
		String path = LSMSUL.class.getResource("/matlab").getFile();
		System.out.println(new File(path).isDirectory());
		MatlabWrapper.getInstance().connect();
		MatlabEngine.startMatlab();

	}

}

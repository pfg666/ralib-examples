package matlab;

import com.mathworks.engine.EngineException;
import com.mathworks.engine.MatlabEngine;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MatlabWrapper {

    private static MatlabWrapper instance = null;
    private static MatlabEngine matEng = null;

    private MatlabWrapper() throws EngineException, InterruptedException {
        connect();
    }


    public static MatlabWrapper getInstance() throws EngineException, InterruptedException {
        if(instance == null) {
            instance = new MatlabWrapper();
        }
        return instance;
    }

    public MatlabEngine connect() throws EngineException, InterruptedException {
        if(matEng == null){
            matEng = MatlabEngine.startMatlab();
        }
        return  matEng;
    }

    public void disconnect() throws EngineException {
        matEng.disconnect();
        matEng = null;
    }

    public void loadPath(String filepath) throws ExecutionException, InterruptedException, TimeoutException {

        matEng.evalAsync(filepath).get(30, TimeUnit.SECONDS);
    }

    public Object runFunction(int retArgs, String functionName, Object param) throws ExecutionException, InterruptedException {
        return matEng.feval(retArgs, functionName, param);
    }

}

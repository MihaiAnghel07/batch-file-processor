import controller.Controller;
import org.apache.log4j.Logger;
import shutdownhook.ShutDownHook;

public class EntryPoint {

    private final static Logger logger = Logger.getLogger(EntryPoint.class);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new ShutDownHook());
        Controller.startApp();
    }
}
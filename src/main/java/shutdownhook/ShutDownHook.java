package shutdownhook;

import utils.Utils;

public class ShutDownHook extends Thread {

    public void run() {
        Utils.deleteEmptyOutput();
    }
}

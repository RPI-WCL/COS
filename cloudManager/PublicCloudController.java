package cloudManager;

import common.Message;

public abstract class PublicCloudController extends CloudController {

    public PublicCloudController(int port) {
        super(port);
    }

    abstract public boolean bootstrap();
}

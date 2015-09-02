package au.com.billon.stt.handlers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trevor Li on 7/14/15.
 */
public class HandlerFactory {
    private static HandlerFactory instance;

    private Map<String, STTHandler> handlers = new HashMap<String, STTHandler>();

    private HandlerFactory() { }

    public static synchronized HandlerFactory getInstance() {
        if ( instance == null ) {
            instance = new HandlerFactory();
        }
        return instance;
    }

    public STTHandler getHandler(String endpointType) {
        STTHandler handler = null;
        if (endpointType != null) {
            handler = handlers.get(endpointType);
            if (handler == null) {
                try {
                    Class handlerClass = Class.forName("au.com.billon.stt.handlers." + endpointType + "Handler");
                    handler = (STTHandler) handlerClass.newInstance();
                    handlers.put(endpointType, handler);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return handler;
    }
}

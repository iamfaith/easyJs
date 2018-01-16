package easyjs.com.easyjs.engine.service.handler;

import java.util.Set;

/**
 * Created by faith on 2018/1/16.
 */

public abstract class IEventHandler {

    public abstract Set<String> getEventType();
    public abstract String handle();

}

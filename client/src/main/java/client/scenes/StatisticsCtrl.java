package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Event;

public class StatisticsCtrl {
    private Event event;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    /**
     * Controller responsible for handling the editing tags
     * functionality.
     *
     * @param server   An instance of ServerUtils for server-related operations.
     * @param mainCtrl An instance of MainCtrl for coordinating with the main
     *                 controller.
     */
    @Inject
    public StatisticsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Initialize the event data
     *
     * @param ev the event
     */
    public void setEvent(Event ev) {
        this.event = ev;
    }

    /**
     * Goes back to the event overview.
     */
    public void back() {
        mainCtrl.showOverviewEvent(event);
    }
}

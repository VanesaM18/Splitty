package client.utils;

import client.scenes.MainCtrl;
import commons.Event;

import java.util.EmptyStackException;
import java.util.Stack;

public class SceneManager {
    private MainCtrl mainCtrl;
    private final Stack<SceneEnum> sceneHistory = new Stack<>();;

    private Event event;
    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public Event getEvent() {
        return event;
    }

    private SceneEnum getCurrentScene() {
        return sceneHistory.peek();
    }

    public void goBack() {
        sceneHistory.pop();
        showScene(sceneHistory.peek(), this.event);
    }

    public void showCurrentScene() {
        showScene(sceneHistory.peek(), this.event);
    }

    public void pushScene(SceneEnum scene) {
        if(this.sceneHistory.isEmpty() || this.sceneHistory.peek() != scene)
            sceneHistory.push(scene);
    }

    public SceneEnum popScene() {
        try {
            return sceneHistory.pop();
        } catch (EmptyStackException e) {
            return SceneEnum.START;
        }
    }

    public void pushScene(SceneEnum scene, Event event) {
        this.event = event != null ? event : this.event;
       this.pushScene(scene);
    }

    public void showScene(SceneEnum scene, Event event) {
        this.event = event;
        switch (scene) {
            case STARTUP:
                mainCtrl.showAppConfiguration();
                break;
            case LOGIN:
                mainCtrl.showLogin();
                break;
            case MANAGEMENT:
                mainCtrl.showManagementOverview();
                break;
            case SETTINGS:
                mainCtrl.showSettings();
                break;
            case START:
                mainCtrl.showStartScreen();
                break;
            case OVERVIEW:
                mainCtrl.showOverviewEvent(event);
                break;
            case INVITE:
                mainCtrl.showInviteScreen(event);
            default:
                break;
        }

    }
}

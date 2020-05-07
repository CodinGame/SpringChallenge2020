package com.codingame.view.event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.codingame.gameengine.core.AbstractPlayer;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.Module;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AnimatedEventModule implements Module {

    GameManager<AbstractPlayer> gameManager;
    @Inject GraphicEntityModule entityModule;
    List<ViewerEvent> animEvents;
    public int speedboostGroupId;
    List<Integer> plasmaBallIds;

    double scale;

    @Inject
    AnimatedEventModule(GameManager<AbstractPlayer> gameManager) {
        this.gameManager = gameManager;
        animEvents = new ArrayList<>();
        gameManager.registerModule(this);
        scale = 1;
        plasmaBallIds = new ArrayList<>();
    }

    @Override
    public void onGameInit() {
        gameManager.setViewGlobalData("event", new Object[] { scale, speedboostGroupId, plasmaBallIds});
        sendFrameData();
    }

    @Override
    public void onAfterGameTurn() {
        sendFrameData();
    }

    @Override
    public void onAfterOnEnd() {
    }

    public ViewerEvent createAnimationEvent(String id, double t) {
        ViewerEvent animEvent = new ViewerEvent(id, t);
        animEvents.add(animEvent);
        return animEvent;
    }

    private void sendFrameData() {
        if (!animEvents.isEmpty()) {

            gameManager.setViewData(
                "event",
                animEvents.stream()
                    .map(ViewerEvent::toString)
                    .collect(Collectors.toList())
            );
            animEvents.clear();
        }
    }

    public void init(double scale, int speedboostGroupId) {
        this.scale = scale;
        this.speedboostGroupId = speedboostGroupId;
    }

    public void addPlasmaBall(int id) {
        plasmaBallIds.add(id);
    }
}

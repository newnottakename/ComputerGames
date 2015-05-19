package nl.tudelft.ti2806.riverrush.domain.entity.game;

import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;
import nl.tudelft.ti2806.riverrush.domain.event.GameStartedEvent;

/**
 * The game is on!
 */
public class PlayingGameState implements GameState {

    private final EventDispatcher eventDispatcher;

    public PlayingGameState(EventDispatcher dispatcher) {
        this.eventDispatcher = dispatcher;

        dispatcher.dispatch(new GameStartedEvent());
    }

    @Override
    public GameState play() {
        return this;
    }

    @Override
    public GameState stop() {
        return new StoppedGameState(this.eventDispatcher);
    }

    @Override
    public GameState finish() {
        return new FinishedGameState(this.eventDispatcher);
    }


}

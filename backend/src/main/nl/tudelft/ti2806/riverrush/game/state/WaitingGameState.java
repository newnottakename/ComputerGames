package nl.tudelft.ti2806.riverrush.game.state;

import nl.tudelft.ti2806.riverrush.domain.entity.Team;
import nl.tudelft.ti2806.riverrush.domain.event.*;
import nl.tudelft.ti2806.riverrush.game.Game;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * State in which the game is waiting for players to join.
 */
public class WaitingGameState implements GameState {

    /**
     * Game about to start timer delay.
     */
    public static final int DELAY = 5;

    private final EventDispatcher dispatcher;
    private Game game;
    HandlerLambda<AnimalAddedEvent> AnimalAddedEventHandlerLambda = (e) -> animalAddedHandler();

    /**
     * Create the waiting game state.
     *  @param eventDispatcher The event dispatcher for firing events
     * @param theGame    The game
     */
    public WaitingGameState(final EventDispatcher eventDispatcher, final Game theGame) {
        this.dispatcher = eventDispatcher;
        this.game = theGame;
        this.dispatcher.dispatch(new GameWaitingEvent());
        this.dispatcher.attach(AnimalAddedEvent.class, AnimalAddedEventHandlerLambda);
        animalAddedHandler();
    }

    /**
     * Is called when a new animal is added.
     */
    public void animalAddedHandler() {
        if (hasEnoughAnimals()) {
            GameAboutToStartEvent event = new GameAboutToStartEvent();
            event.setSeconds(DELAY);
            this.dispatcher.dispatch(event);

            final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.schedule(game::start, DELAY, TimeUnit.SECONDS);
        }
    }

    /**
     * @return if every team has at least one animal
     */
    public boolean hasEnoughAnimals() {
        Collection<Team> teams = game.getTeams();
        if (teams.size() == 0) {
            return false;
        }
        for(Team t : teams) {
            if(t.size() < 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void dispose() {
        // Nothing to dispose.
    }

    @Override
    public GameState start() {
        this.dispose();
        return new PlayingGameState(this.dispatcher, this.game);
    }

    @Override
    public GameState stop() {
        this.dispose();
        return new StoppedGameState(this.dispatcher, this.game);
    }

    @Override
    public GameState finish() {
        return this;
    }

    @Override
    public GameState waitForPlayers() {
        return this;
    }

    private class AnimalAddedEventHandler {
    }
}

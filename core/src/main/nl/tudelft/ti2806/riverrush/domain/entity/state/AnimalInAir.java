package nl.tudelft.ti2806.riverrush.domain.entity.state;

import nl.tudelft.ti2806.riverrush.domain.entity.Monkey;
import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;

/**
 * State in which the animal is in mid-air. This means the player can't control the animal while in
 * this state.
 */
public class AnimalInAir implements AnimalState {

    /**
     * The animal that is in the air.
     */
  private Monkey monkey;
    /**
     * The eventdispatcher of this event.
     */
  private final EventDispatcher dispatcher;

  /**
   * Constructor.
   *
   * @param monk
   *          - The monkey that is in the air
   * @param eventDispatcher
   *          - The event dispatcher of this event
   */
  public AnimalInAir(final Monkey monk, final EventDispatcher eventDispatcher) {
    this.monkey = monk;
    this.dispatcher = eventDispatcher;
  }

  @Override
  public AnimalState jump() {
    return this;
  }

  @Override
  public AnimalState drop() {
    return new AnimalOnBoat(this.monkey, this.dispatcher);
  }

  @Override
  public AnimalState collide() {
    return this;
  }

  @Override
  public AnimalState returnToBoat() {
    return this;
  }
}

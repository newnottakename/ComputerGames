package nl.tudelft.ti2806.riverrush.domain.entity.state;

import nl.tudelft.ti2806.riverrush.domain.entity.Monkey;
import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;
import nl.tudelft.ti2806.riverrush.network.event.AnimalFellOff;

import com.badlogic.gdx.scenes.scene2d.Action;

/**
 * Created by thomas on 7-5-15.
 */
public class AnimalOnBoat implements AnimalState {

  private Monkey monkey;
  private final EventDispatcher dispatcher;

  public AnimalOnBoat(Monkey monk, EventDispatcher eventDispatcher) {
    this.monkey = monk;
    this.dispatcher = eventDispatcher;
  }

  @Override
  public AnimalState jump() {
    return new AnimalInAir(this.monkey, this.dispatcher);
  }

  @Override
  public AnimalState drop() {
    return this;
  }

  @Override
  public AnimalState collide() {
    Action hit = this.monkey.getHit();
    this.monkey.addAction(hit);
    this.dispatcher.dispatch(new AnimalFellOff());
    return new AnimalInWater(this.monkey, this.dispatcher);
  }
}

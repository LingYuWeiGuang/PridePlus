package op.wawa.prideplus.utils.animation.animations;

/**
 * @author ChengFeng
 * @since 2024/7/29
 **/
public abstract class ComposedAnimation<T> {
    public abstract T getOutput();
    public abstract void changeDirection();
}

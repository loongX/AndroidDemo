package leadfair.observer;

/**
 * Created by TZT on 2017/8/21.
 */

/**
 * 观察者
 */
public interface PPObserver<E> {

    <S extends E> void push(S data);

    <S extends E> S pull();

    <S extends E> boolean pull(S pullData);

    void close();
}

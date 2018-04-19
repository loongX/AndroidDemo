package leadfair.observer;

/**
 * Created by TZT on 2017/8/21.
 */

/**
 * 事件源
 */
public interface PPObservable<E> {

    <S extends E> Puller<PushMessage<S>> subscibe(PPObserver tppObserver);

    <S extends E> void pushMessage(PushMessage<S> anyThing);

    <S extends E> void push(S anyThing);

    <S extends E> S cacheLastPushThing();

    <S extends E> boolean notifyMessage(PullMessage<S> pullMessage);

    void close();
}

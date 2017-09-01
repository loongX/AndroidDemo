package leadfair.observer;

/**
 * Created by TZT on 2017/8/21.
 */

public abstract class Message<T> {
    protected T data;

    @SuppressWarnings("unchecked")
    public <S extends T> S getData() {
        return (S) data;
    }

    public <S extends T> void setData(S data) {
        this.data = data;
    }
}

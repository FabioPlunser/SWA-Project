import java.util.ArrayList;
import java.util.List;

public interface ISubject {
    void registerObserver(IObserver observer);
    void removeObserver(IObserver observer);
    void notifyObservers(String message);
}

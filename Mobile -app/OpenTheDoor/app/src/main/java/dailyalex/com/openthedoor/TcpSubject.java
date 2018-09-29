package dailyalex.com.openthedoor;

/**
 * Created by alex on 18.03.2018.
 */

public interface TcpSubject {
    void Attach(TcpObserver o);
    void Detach(TcpObserver o);
    void NotifyMessageReceived(String message);
}

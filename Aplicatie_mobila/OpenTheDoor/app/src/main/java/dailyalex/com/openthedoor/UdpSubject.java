package dailyalex.com.openthedoor;

/**
 * Created by alex on 26.04.2018.
 */

public interface UdpSubject {
    void Attach(UdpObserver o);
    void Detach(UdpObserver o);
    void NotifyImageReceived(byte[] image);
}

package theshakers.cmpt276.sfu.ca.robottelepresense.WebServer.ResponseCallback;

/**
 * Created by baesubin on 2018-11-04.
 */

// This callback is for getting response after SendAndReceiveJsonAsyncTask
public interface StringResponseCallback {
    void onResponseReceived(String result);
}

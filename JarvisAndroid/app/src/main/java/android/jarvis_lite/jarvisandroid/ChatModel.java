package android.jarvis_lite.jarvisandroid;

/**
 * Created by prashant on 2/1/17.
 */
public class ChatModel {
    private String sender;
    private String message;


    public ChatModel(){

    }
    public ChatModel(String sender, String message){
        this.sender = sender;
        this.message = message;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}

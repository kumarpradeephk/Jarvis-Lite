package android.jarvis_lite.jarvisandroid;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String sender,message;
    private ChatAdapter cAdapter;
    private List<ChatModel> chatModelList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText editText;
    private ChatModel chatModel;
    private Socket mSocket;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        cAdapter = new ChatAdapter(chatModelList);
        editText = (EditText)findViewById(R.id.editText);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cAdapter);

        try{
            mSocket = IO.socket("http://192.168.43.242:3000");
            Log.v("pkpk",mSocket+"");
        }
        catch (Exception e){}

        mSocket.on("chat message", onNewMessage);
        mSocket.connect();

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    getDataFromEditText();
                    return true;
                }
                return false;
            }
        });


    }


 private Emitter.Listener onNewMessage = new Emitter.Listener() {
     @Override
     public void call(final Object... args) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.v("pkpk","inside run");
                JSONObject jsonObject = (JSONObject)args[0];
                String msg="",sender="";
                try{
                    msg = jsonObject.getString("message");
                    sender = jsonObject.getString("sender");
                    Log.v("pkpk","msg : "+msg);
                }
                catch (Exception e){
                }

                recyclerRefresh(sender,msg);

            }

        });
     }
 };



    public void send(View view){

        promptSpeechInput();
    }

    public void getDataFromEditText(){
        sender = "Prashant";
        message = editText.getText().toString().trim();
        editText.setText("");
        JSONObject jsonObject = new JSONObject();

        if (!message.isEmpty()){
            try {
                jsonObject.put("sender", sender);
                jsonObject.put("message",message);
            }
            catch (Exception e){}
            Log.v("pkpk",jsonObject.toString()+"");

            //delete this. Only for tsting
            recyclerRefresh(sender,message);
            //ends here
            mSocket.emit("chat message",jsonObject);
        }
    }

    //Refresh the recycler view with new data
    public void recyclerRefresh(String sender, String message){
        chatModel = new ChatModel(sender,message);
        chatModelList.add(chatModel);
        cAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(cAdapter.getItemCount()-1);
    }


    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("voice",resultCode+" ok"+requestCode);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String msg = result.get(0);
                    Log.v("voice",msg+" ok");
                    recyclerRefresh("Prashant",msg);
                }
                break;
            }

        }
    }

}

package android.jarvis_lite.jarvisandroid;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by prashant on 2/1/17.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatModel> chatList;

    public ChatAdapter(List<ChatModel> chatList){
        this.chatList = chatList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView sender,message;
        public CardView chat_card;
        public MyViewHolder(View view) {
            super(view);
            chat_card = (CardView)view.findViewById(R.id.chat_card);
            sender = (TextView)view.findViewById(R.id.sender);
            message = (TextView)view.findViewById(R.id.message);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatModel chatModel = chatList.get(position);
        holder.sender.setText(chatModel.getSender());
        holder.message.setText(chatModel.getMessage());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list, parent, false);

        return new MyViewHolder(itemView);
    }
}

package com.plplim.david.talktalk.chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plplim.david.talktalk.R;
import com.plplim.david.talktalk.model.ChatModel;

public class MessageActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid(); //채팅을 요구하는 아이디 즉 단말기에 로그인된 UID
        destinationUid = getIntent().getStringExtra("destinationUid"); //채팅을 당하는 아이디
        button = (Button) findViewById(R.id.messageActivity_button);
        editText = (EditText) findViewById(R.id.messageActivity_editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                if (chatRoomUid == null) {
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);
                } else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment);
                }

            }
        });
        checkChatRoom();
    }

    void checkChatRoom(){
        //내가 속해있는 chatroom을 체크하는 함수

        //oderByChild user 밑에 해당 아이디가 있는지 체크
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    //내가 요구한 chatrooms에 상대방의 아이디가 있는지 확인함
                    if (chatModel.users.containsKey(destinationUid)) {
                        chatRoomUid = item.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

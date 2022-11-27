package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class RecruitActivity extends AppCompatActivity {

    private EditText delevey_et; // 배달내용 에디터텍스트
    private EditText title_et; // 제목 에디터텍스트
    private Spinner people_sp; // 총인원 스피너
    private Spinner place_sp; //place 스피너
    private TextView date_view1; //날짜 텍스트
    private TextView date_view2; //시간 텍스트
    private RadioButton radio_together; //같이 라디오버튼
    private RadioButton radio_solo; //따로 라디오버튼
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어 베이스 store
    private Context rContext; //context
    private FirebaseAuth firebaseAuth;     //파이어베이스 인증처리
    private final Board board = new Board(); // 게시판
    long mNow;
    Date mDate;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);
        // 게시판 uid 가져와서 저장
        firebaseAuth = FirebaseAuth.getInstance();
        rContext = getApplicationContext();
        //장소스피너
        place_sp = findViewById(R.id.place_sp);
        //제목 에디터 텍스트
        title_et = findViewById(R.id.title_et);
        //배달내용 EditText
        delevey_et = findViewById(R.id.delevey_et);
        //같이 라디오버튼
        radio_together = findViewById(R.id. radio_together);
        //따로 라디오버튼
        radio_solo = findViewById(R.id.radio_solo);
        //다른 인텐트 받아올때 사용
        Intent anotherintent = getIntent();
        //현재날짜
        date_view1 = findViewById(R.id.date_view1);
        //날짜 지정
        date_view1.setText(getTime());
        //시간 ID 가져오기
        date_view2 = findViewById(R.id.date_view2);

        //시간 데이터 가져오기
        String data = anotherintent.getStringExtra("time");
        date_view2.setText(data);
        // 총인원 스피너
        people_sp = findViewById(R.id.people_sp);
        people_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
    }
    // 삭제하기 버튼 이벤트
    public void deletebtn(){
        /*db.collection(place_name).document(board_lists.get(position).getDoc_id())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!" );
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });*/
    }
    // 수정하기 버튼 이벤트
    public void retouchbtn(){
        /*DocumentReference washingtonRef = db.collection("cities").document("DC");

        // Set the "isCapital" field of the city 'DC'
        washingtonRef
                .update("capital", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });*/
    }

    // 등록하기 버튼 함수
    public void recruitbtn(View view){
        // 파이어베이스 store에 저장
        save_boarddata(board); // 파이어 데이터베이스 저장 함수로 만들어 둠

        Intent placeBoardintent = new Intent(RecruitActivity.this, PlaceActivity2.class); //장소게시판 액티비티에 전달
        // 장소게시판에 넘겨줄 스트링 선언
        String place_name = place_sp.getSelectedItem().toString();
        placeBoardintent.putExtra("place_name", place_name); //화면 전환시 넘겨줄 장소이름
        startActivity(placeBoardintent);//화면전환
        finish();//등록게시판 종료
    }
    // 시간선택 버튼 기능 = 타임피커
    public void rider_time(View view) {
        Intent intent = new Intent(RecruitActivity.this, TimePickerActivity.class); //타임피커 activity에 대한 화면전환 객체
        getRiderTimeActicity.launch(intent); //타임피커로 이동하면서 값 받아오는 함수
    };
    // 함수로 데이터 정보 받아옴
    private void save_boarddata(Board board){
        Map<String, Object> data = new HashMap<>(); // 문서 id 자동 생성
        FirebaseUser user = firebaseAuth.getCurrentUser();
        board.setBoard_name(place_sp.getSelectedItem().toString()); // 장소 스피너 텍스트 저장
        board.setPeople_number(people_sp.getSelectedItem().toString()); //인원수 스피너 텍스트 저장
        board.setDate(date_view1.getText().toString()); //날짜 저장
        board.setTime(date_view2.getText().toString()); //시간 저장
        board.setDelevery_info(delevey_et.getText().toString()); //배달내용 저장
        board.setTitle_info(title_et.getText().toString()); //제목 저장
        assert user != null;
        board.setUid(user.getUid()); // 유저 UID 저장
        // 같이 먹을건지 따로먹을건지 저장
        if (radio_together.isChecked()){
            board.setTogether(radio_together.getText().toString()); // 같이하면 같이 저장
        }else if(radio_solo.isChecked()){
            board.setSolo(radio_solo.getText().toString()); // 따로하면 따로 저장
        }
        DocumentReference newCityRef = db.collection("post").document();
        db.collection("post") //post 데이터 베이스
                .add(board)
                .addOnSuccessListener(aVoid -> Toast.makeText(rContext, " 정상적으로 게시글이 작성되었습니다. ", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(rContext, " 게시글 내용을 확인해주세요. ",Toast.LENGTH_SHORT).show());
    }

    //현재날짜 출력 함수
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    //툴바 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//toolbar의 back키 눌렀을 때 동작
            // 액티비티 이동
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //화면전환 RegisterForActivityResult
    private final ActivityResultLauncher<Intent> getRiderTimeActicity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // 타임피커로부터 돌아올때 타임피커의 결괏값을 받아올 수 있는 통로.
                //성공적으로 값을 받아왔는지 확인
                 if(result.getResultCode()==RESULT_OK){
                     //타임피커의 액티비티 값을 텍스트뷰에 가져옴
                     assert result.getData() != null;
                     date_view2.setText(result.getData().getStringExtra("time"));
                 }
            }
    );
}

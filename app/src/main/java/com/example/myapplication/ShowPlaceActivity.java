package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class ShowPlaceActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // 파이어store 인스턴스 선언
    private FirebaseAuth firebaseAuth;     //파이어베이스 인증처리
    private TextView title_txt;
    private Context shcontext;
    private String id, place_name, showtitle, showpeople, showinfo, dateview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        //파이어베이스 인증
        firebaseAuth = FirebaseAuth.getInstance();

        // 사용하는 모든 텍스트들
        TextView show_title = findViewById(R.id.show_title); // 제목
        TextView show_people = findViewById(R.id.show_people); //총인원 수
        TextView show_info = findViewById(R.id.show_info); //보여줄 내용
        TextView show_placename = findViewById(R.id.show_placename); //장소이름
        TextView date_view1 = findViewById(R.id.date_view1); //시간
        title_txt = findViewById(R.id.title_txt);

        shcontext = this;
        //인텐트시 정보 받아옴.
        Intent intent = getIntent();
        place_name = intent.getStringExtra("place_name");
        showtitle = intent.getStringExtra("title_info");
        showpeople = intent.getStringExtra("people_number");
        showinfo = intent.getStringExtra("delevery_info");
        dateview = intent.getStringExtra("time");
        id = intent.getStringExtra("id");


        // 유저에게 보여지는 정보 셋팅
        show_title.setText(showtitle);
        show_placename.setText("<"+place_name+">");
        show_people.setText(showpeople);
        show_info.setText(showinfo);
        date_view1.setText(dateview);


        // 뒤로가기버튼 툴바
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김
    }
    //툴바 함수
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//옵션메뉴
        if (item.getItemId() == android.R.id.home) {//toolbar의 back키 눌렀을 때 동작
            // 액티비티 이동
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);//옵션메뉴 리턴*/
    }

    // 수정하기 버튼 이벤트
    public void update(View view){
        db.collection("post") //컬렉션 게시물 선택
                .get() // 데이터 가져옴
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board Board = document.toObject(Board.class); // 오브젝트 형식으로 변환.
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user.getUid().equals(Board.getUid())){ // 로그인 되어있는 회원의 uid와 게시판에 저장된 uid와 일치할 경우 실행
                                Intent reintent = new Intent(ShowPlaceActivity.this, RecruitActivity.class); //장소게시판 액티비티에 전달
                                reintent.putExtra("place_name", place_name); //장소이름
                                reintent.putExtra("title_info", showtitle); //제목
                                reintent.putExtra("people_number", showpeople); //총인원
                                reintent.putExtra("delevery_info", showinfo); //상세정보
                                reintent.putExtra("time",  dateview);//등록시간
                                reintent.putExtra("id",id);
                                startActivity(reintent);//화면전환
                                finish();//등록게시판 종료
                            }else{
                                Toast.makeText(shcontext, " 수정할 수 있는 권한이 없습니다. ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    // 삭제하기 버튼 이벤트
    public void delete(View view){
        db.collection("post") //컬렉션 게시물 선택
                .get() // 데이터 가져옴
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Board Board = document.toObject(Board.class); // 오브젝트 형식으로 변환.
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user.getUid().equals(Board.getUid())){ // 로그인 되어있는 회원의 uid와 게시판에 저장된 uid와 일치할 경우 삭제
                                db.collection("post")
                                        .document(id)//doc id와 일치할 경우 삭제
                                        .delete()
                                        .addOnSuccessListener(aVoid -> Toast.makeText(shcontext, " 정상적으로 게시글이 삭제되었습니다. ", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(shcontext, " 게시글을 찾지 못했습니다. ", Toast.LENGTH_SHORT).show());
                            }else{
                                Toast.makeText(shcontext, " 삭제할 수 있는 권한이 없습니다. ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
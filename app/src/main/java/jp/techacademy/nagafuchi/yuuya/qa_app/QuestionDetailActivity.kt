package jp.techacademy.nagafuchi.yuuya.qa_app

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*

class QuestionDetailActivity : AppCompatActivity(),DatabaseReference.CompletionListener {
    var  favoriteFlag = false
    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private lateinit var mFavoriteRef:DatabaseReference

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val map = p0.value as Map<String, String>
            val answerUid = p0.key ?: ""
            for (answer in mQuestion.answers) {
                //同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.anserUid) {
                    return
                }
            }
            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""
            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
    //=================--新規変更======================================================
    private val mFavoriteEventListener = object : ChildEventListener {
        override fun onChildAdded(p0: DataSnapshot, p1: String?) {//呼ばれる　== 子要素あり == お気に入り済み
            favoriteButton.setImageResource(R.drawable.ic_star_black_24dp)
            favoriteFlag = true
//            val map = p0.value as Map<String, String>
  //          val mGenre = map["genre"]
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
        override fun onChildRemoved(p0: DataSnapshot) {}
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onCancelled(p0: DatabaseError) {}

    }
    //=================--新規変更ここまで======================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)
        //わたってきたQuestionオブジェクトを保持
        val extras = intent.extras
        //データベースの追加
        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference

        if (user == null){//ログインしていなときはfavoriteボタンは消しておく。
            favoriteButton.hide()
        }

        mQuestion = extras.get("question") as Question
        title = mQuestion.title

        //ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        fab.setOnClickListener {
            //ログイン済みユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                val intent = Intent(applicationContext,LoginActivity::class.java)
                startActivity(intent)
            } else {
                //Questionを渡して回答作成画面を起動する
                val intent = Intent(applicationContext,AnswerSendActivity::class.java)
                intent.putExtra("question",mQuestion)
                startActivity(intent)
            }
        }
        favoriteButton.setOnClickListener {
            if(favoriteFlag){
                favoriteButton.setImageResource(R.drawable.ic_star_border_black_24dp)
                mFavoriteRef.removeValue()
                favoriteFlag = false
            }else{
                val data = HashMap<String,String>()
                data["genre"] = mQuestion.genre.toString()
                mFavoriteRef.setValue(data)
                favoriteButton.setImageResource(R.drawable.ic_star_black_24dp)
            }

        }

        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(
            AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
        if (user != null){
            mFavoriteRef = dataBaseReference.child(FavoritesPath).child(user!!.uid).child(mQuestion.questionUid)
            mFavoriteRef.addChildEventListener(mFavoriteEventListener)
        }

    }
    override fun onComplete(databaseError: DatabaseError?, databaseReference: DatabaseReference) {


        if (databaseError == null) {
            finish()
        } else {
            Snackbar.make(findViewById(android.R.id.content), "投稿に失敗しました。", Snackbar.LENGTH_LONG)
                .show()
        }
    }

}
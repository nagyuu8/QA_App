package jp.techacademy.nagafuchi.yuuya.qa_app

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*

class QuestionDetailActivity : AppCompatActivity(),DatabaseReference.CompletionListener {
    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        //わたってきたQuestionオブジェクトを保持
        val extras = intent.extras
        //データベースの追加
        val user = FirebaseAuth.getInstance().currentUser
        val dataBaseReference = FirebaseDatabase.getInstance().reference



        mQuestion = extras.get("question") as Question
        title = mQuestion.title

        //ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
//ここから===================================================================================--
        if (user != null) {


            val favoriteRef = dataBaseReference
                .child(FavoritesPath)
                .child(user.toString())

            val data = HashMap<String, String>()
            //UID
            data["uid"] = FirebaseAuth.getInstance().currentUser!!.uid

            var mFavorite: String
            if (data["favorite"] == null) {
                mFavorite = "true"
            } else {
                mFavorite = data["favorite"]!!
            }

            if (mFavorite == "true") {
                favoriteButton.hide()//お気に入りボタン（済）を消しておく。
            } else {
                favoriteButton2.hide()//お気に入りボタン（済）を消しておく。
            }

            if (user == null) {// もし、ログイン状態でなければ、お気に入りボタン（未）も隠す。
                favoriteButton.hide()
            }
            favoriteButton.setOnClickListener {
                //favoriteボタンが押されたら、登録をし、登録済みの画像へ変更する
                favoriteButton.hide()
                favoriteButton2.show()
                mFavorite = "true"
                favoriteRef.setValue(mFavorite)
            }
            favoriteButton2.setOnClickListener {
                //お気に入り済みのボタンを再度おしたら、お気に入りを解除し、登録未登録へ戻す。
                favoriteButton2.hide()
                favoriteButton.show()
                mFavorite = "false"
                favoriteRef.setValue(mFavorite)
            }
        }else{
            favoriteButton.hide()
            favoriteButton2.hide()
        }
//ここまで =======================================================================================================================

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

        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(
            AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
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
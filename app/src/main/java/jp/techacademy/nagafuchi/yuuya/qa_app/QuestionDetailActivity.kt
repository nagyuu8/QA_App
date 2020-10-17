package jp.techacademy.nagafuchi.yuuya.qa_app

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*

class QuestionDetailActivity : AppCompatActivity() {
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
        mQuestion = extras.get("question") as Question

        title = mQuestion.title

        //ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        if (FirebaseAuth.getInstance().currentUser != null){ ///Userがあれば、mAdapterのisLoginをtrueにする。
            mAdapter.isLogin = true
        }
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
        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(
            AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }
}
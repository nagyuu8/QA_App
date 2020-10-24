package jp.techacademy.nagafuchi.yuuya.qa_app

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoriteActivity : AppCompatActivity() {
    private var mFavoriteRef: DatabaseReference? = null
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mAdapter: QuestionsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_favorite)
        Log.d("test","呼ばれた？")
        super.onCreate(savedInstanceState)

        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        //ListViewの準備
        mListView = findViewById(R.id.listView3)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()
        mAdapter.setQuestionArrayList(mQuestionArrayList)
        mListView.adapter  = mAdapter


        mListView.setOnItemClickListener { parent, view, position, id ->
            //Questionのインスタンスを渡して質問詳細画面を起動する
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }
        val user = FirebaseAuth.getInstance().currentUser
        mFavoriteRef = mDatabaseReference.child(FavoritesPath).child(user!!.uid)
        Log.d("test2",mFavoriteRef!!.ref.key.toString())
        mFavoriteRef!!.addChildEventListener(mEventListener)
    }
    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val questionID = dataSnapshot.key ?: ""
            val map = dataSnapshot.value as Map<String,String>
            val genre = map["genre"] ?: ""

            val mRef  = mDatabaseReference.child(ContentsPATH).child(genre).child(questionID)
            mRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = snapshot.value as Map<String,String>
                    val title = map["title"] ?: ""
                    val body =map["body"] ?: ""
                    val name = map["name"] ?: ""
                    val uid = map["uid"] ?: ""
                    val imageString = map["image"] ?:""
                    val bytes =
                        if(imageString.isNotEmpty()){
                            Base64.decode(imageString, Base64.DEFAULT)
                        }else{
                            byteArrayOf()
                        }
                    val answerArrayList = ArrayList<Answer>()
                    val answerMap = map["answers"] as Map<String,String>?
                    if(answerMap != null){
                        for(key in answerMap.keys){
                            val temp = answerMap[key] as Map<String,String>
                            val answerBody = temp["body"] ?: ""
                            val answerName = temp["name"] ?: ""
                            val answerUid = temp["uid"] ?: ""
                            val answer = Answer(answerBody,answerName,answerUid,key)
                            answerArrayList.add(answer)
                        }
                    }
                    val question = Question(title,body,name,uid,dataSnapshot.key ?:"",genre.toInt(),bytes,answerArrayList)
                    mQuestionArrayList.add(question)
                    mAdapter.notifyDataSetChanged()
                    Log.d("test","きてるか？")
                }
                override fun onCancelled(firebaseError: DatabaseError) {}
            })//TDOO LoginActivity参考  //データのとり方はMainActivity
        }
        override fun onCancelled(p0: DatabaseError) {}
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
        override fun onChildRemoved(p0: DataSnapshot) {}
    }
}
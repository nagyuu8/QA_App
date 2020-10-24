package jp.techacademy.nagafuchi.yuuya.qa_app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
        // mListView = findViewById(R.id.listView3)
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()

//        mListView.setOnItemClickListener { parent, view, position, id ->
//            //Questionのインスタンスを渡して質問詳細画面を起動する
//            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
//            intent.putExtra("question", mQuestionArrayList[position])
//            startActivity(intent)
//        }
        val user = FirebaseAuth.getInstance().currentUser
        mFavoriteRef = mDatabaseReference.child(FavoritesPath).child(user!!.uid)
        mFavoriteRef!!.addChildEventListener(mEventListener)
    }
    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            Log.d("test",dataSnapshot.ref.toString())
            val user = FirebaseAuth.getInstance().currentUser
//            dataSnapshot.ref.addChildEventListener()
            val mEventListener2 = object :ChildEventListener{
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {

                }
                override fun onCancelled(p0: DatabaseError) {}
                override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                override fun onChildRemoved(p0: DataSnapshot) {}

            }
        }
        override fun onCancelled(p0: DatabaseError) {}
        override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
        override fun onChildRemoved(p0: DataSnapshot) {}
    }
}
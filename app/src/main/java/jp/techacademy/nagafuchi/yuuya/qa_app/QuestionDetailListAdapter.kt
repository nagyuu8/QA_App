package jp.techacademy.nagafuchi.yuuya.qa_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.w3c.dom.Text

/**
 *
 */
class QuestionDetailListAdapter(context: Context,private val mQuestion: Question): BaseAdapter() {
    companion object{
        private val TYPE_QUESTION = 0
        private val TYPE_ANSWER = 1
    }

    private var mLayoutInflater: LayoutInflater? = null
    var isLogin = false

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return 1+mQuestion.answers.size
    }

    /**
     * positionが0ならばTYPE_QUESTION(0)を返す、それ以外はTYPE_ANSWER(1)を返す
     */
    override fun getItemViewType(position: Int): Int {
        return  if(position == 0){
            TYPE_QUESTION
        }else{
            TYPE_ANSWER
        }
    }

    /**
     *  Int型の2を返す。
     */
    override fun getViewTypeCount(): Int {
        return 2
    }

    /**
     * mQuestionを返す。
     */
    override fun getItem(position: Int): Any {
        return mQuestion
    }

    /**
     *  0を返す。
     */
    override fun getItemId(position: Int): Long {
        return 0
    }

    /**
     * getItemViewTypeメソッドを呼び足して、どちらのタイプか判断し、レイアウトファイルを指定する。
     */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
       var convertView = convertView
        if(getItemViewType(position) == TYPE_QUESTION){
            if(convertView == null){
                convertView = mLayoutInflater!!.inflate(R.layout.list_quesriton_detail,parent,false)!!
            }
            val body = mQuestion.body
            val name = mQuestion.name

            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name

            val bytes = mQuestion.imageBytes
            if(bytes.isNotEmpty()){
                val image = BitmapFactory.decodeByteArray(bytes,0,bytes.size).copy(Bitmap.Config.ARGB_8888,true)
                val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(image)
            }
//            val favorite = TODO()
            val favoriteButton = convertView.findViewById<Button>(R.id.favorite) as Button
            if (isLogin){
                favoriteButton.visibility = View.VISIBLE
            }

        }else{
            if(convertView == null){
                convertView = mLayoutInflater!!.inflate(R.layout.list_answer,parent,false)!!
            }
            val answer = mQuestion.answers[position -1]
            val body = answer.body
            val name = answer.name

            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name
        }
        return convertView
    }




}
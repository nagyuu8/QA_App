package jp.techacademy.nagafuchi.yuuya.qa_app

import java.io.Serializable

/**
 * Firebaseから取得した質問のデータを保持するモデルクラス。
 * @param title Firebaseから取得したタイトル
 * @param body Firebaseから取得した質問本文
 * @param name Firebaseから取得した質問者の名前
 * @param uid Firebaseから取得した質問者のUID
 * @param questionUid Firebaseから取得した質問のUID
 * @param genre 質問のジャンル
 * @param imageBytes Firebaseから取得した画像をbyte型の配列にしたもの
 * @param ansers Firebaseから取得した質問のモデルクラスであるAnswerのArrayList
 */
class Question(val title:String,val body:String,val name:String,val uid:String,
               val questionUid:String,val genre:Int,bytes:ByteArray,val answers:ArrayList<Answer>):Serializable {
    val imageBytes:ByteArray
    init {
        imageBytes = bytes.clone()
    }
}
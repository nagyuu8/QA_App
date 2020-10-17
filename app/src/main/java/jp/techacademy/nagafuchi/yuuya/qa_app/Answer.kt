package jp.techacademy.nagafuchi.yuuya.qa_app

import java.io.Serializable

/**
 * 質問の回答のモデルクラス
 * @param body Firebaseから取得した回答本文
 * @param name Firebaseから取得した回答者の名前
 * @param uid Firebaseから取得した回答者のUID
 * @param anserUid Firebaseから取得した回答のUID
 */
class Answer(val body:String,val name:String,val uid:String,val anserUid:String):Serializable {

}
package com.example.nycopenjobs.util

val Any.TAG:String
        get(){
            return if (!javaClass.isAnonymousClass){
                val name = javaClass.simpleName

                //first 23 char
                if (name.length <= 23) name else name.substring(0,23)
            } else {
                val name = javaClass.name

                //last 23 chars
                if (name.length <= 23) name else name.substring(name.length - 23, name.length)         }
        }


//extension function to capitalize words
fun String.capitalizeWords(delimter: String = " ") =
    split (delimter).joinToString(delimter) { word ->
        val lowercaseWord = word.lowercase ( )
        lowercaseWord.replaceFirstChar(Char::titlecaseChar)
    }
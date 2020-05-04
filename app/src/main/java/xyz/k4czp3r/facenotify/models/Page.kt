package xyz.k4czp3r.facenotify.models

import androidx.fragment.app.Fragment

class Page(
    private val name: String,
    private val icon: Int,
    private val position: Int) {

    fun getPageName(): String{
        return name
    }
    fun getPageIcon(): Int{
        return icon
    }
    fun getPagePosition(): Int{
        return position
    }

}
package fr.nicolaspomepuy.discreetapprate

import android.app.Activity
import android.view.animation.Animation
import android.view.animation.AnimationUtils

enum class Position {
    TOP,
    BOTTOM
}

fun Position.hideAnimation(activity: Activity): Animation? = AnimationUtils.loadAnimation(activity, when(this) {
    Position.TOP -> R.anim.fade_out_from_top
    Position.BOTTOM -> R.anim.fade_out
})

fun Position.showAnimation(activity: Activity): Animation? = AnimationUtils.loadAnimation(activity, when(this) {
    Position.TOP -> R.anim.fade_in_from_top
    Position.BOTTOM -> R.anim.fade_in
})

// Java compatibility
class PositionExtensions{
    companion object {
        fun hideAnimation(activity: Activity, position: Position) = position.hideAnimation(activity)
        fun showAnimation(activity: Activity, position: Position) = position.showAnimation(activity)
    }
}
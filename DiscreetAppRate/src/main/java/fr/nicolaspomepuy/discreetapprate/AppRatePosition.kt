package fr.nicolaspomepuy.discreetapprate

import android.app.Activity
import android.view.animation.Animation
import android.view.animation.AnimationUtils

enum class Direction {
    FROM_TOP,
    FROM_BOTTOM
}

// Margin in pixels
data class Position (var direction: Direction, var margin: Int) {
    companion object {
        val top = Position(Direction.FROM_TOP)
        val bottom = Position(Direction.FROM_BOTTOM)

    }
    constructor(direction: Direction): this(direction, 0)
}

fun Position.hideAnimation(activity: Activity): Animation? = AnimationUtils.loadAnimation(activity, when(this.direction) {
    Direction.FROM_TOP -> R.anim.fade_out_from_top
    Direction.FROM_BOTTOM -> R.anim.fade_out
})

fun Position.showAnimation(activity: Activity): Animation? = AnimationUtils.loadAnimation(activity, when(this.direction) {
    Direction.FROM_TOP -> R.anim.fade_in_from_top
    Direction.FROM_BOTTOM -> R.anim.fade_in
})

// Java compatibility
class PositionExtensions{
    companion object {
        fun hideAnimation(activity: Activity, position: Position) = position.hideAnimation(activity)
        fun showAnimation(activity: Activity, position: Position) = position.showAnimation(activity)
    }
}
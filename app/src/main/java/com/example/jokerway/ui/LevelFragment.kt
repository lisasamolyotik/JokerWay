package com.example.jokerway.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.jokerway.R
import com.example.jokerway.databinding.LevelFragmentBinding
import com.example.jokerway.model.Cell
import com.example.jokerway.model.Direction
import com.example.jokerway.util.BallUtils
import kotlin.math.absoluteValue

class LevelFragment : Fragment(R.layout.level_fragment), GestureDetector.OnGestureListener {
    private var _binding: LevelFragmentBinding? = null
    private val binding get() = _binding!!
    private var animator: ObjectAnimator? = null
    private var ballUtils: BallUtils? = null
    private lateinit var mDetector: GestureDetectorCompat

    private var cells: MutableList<Cell> = mutableListOf()

    private var layoutChangedListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var cellWidth = 0f
    private var animationDistance: Float = 0f
    private var animationTime: Long = 0L
    private var isGameActive = true
    private var currentDirection: Direction = Direction.NO_DIRECTION
    private var ballPosition = IntArray(2)
    private var initialPosition = IntArray(2)
    private var currentCell: Cell? = null

    companion object {
        private const val HORIZONTAL_MOVING = "translationX"
        private const val VERTICAL_MOVING = "translationY"
        private const val DEBUG_TAG = "LevelFragment"
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LevelFragmentBinding.inflate(inflater, container, false)
        mDetector = GestureDetectorCompat(requireContext(), this)
        binding.levelLayout.setOnTouchListener { _, event ->
            mDetector.onTouchEvent(event).let {
                true
            }
        }
        layoutChangedListener = ViewTreeObserver.OnGlobalLayoutListener {
            binding.ball.getLocationInWindow(initialPosition)
            cellWidth = binding.cell1.width.toFloat()
            inflateCellsList()
            binding.ball.viewTreeObserver.removeOnGlobalLayoutListener(layoutChangedListener)
            layoutChangedListener = null
        }
        binding.ball.viewTreeObserver.addOnGlobalLayoutListener(layoutChangedListener)
        ballUtils = BallUtils(requireContext())
        return binding.root
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.d(DEBUG_TAG, "onFling is called")
        if (isGameActive) {
            if (binding.staticBall.visibility == View.VISIBLE) {
                binding.staticBall.visibility = View.GONE
                binding.ball.visibility = View.VISIBLE
            }
            animator?.cancel()
            binding.ball.getLocationInWindow(initialPosition)
            binding.ball.getLocationInWindow(ballPosition)
            animationDistance += binding.cellsContainer.height.toFloat()
            animationTime += 5000

            val distanceX = e2!!.x - e1!!.x
            val distanceY = e2.y - e1.y
            changeDirection(distanceX, distanceY)
        }
        return isGameActive
    }

    private fun changeDirection(distanceX: Float, distanceY: Float) {
        if (distanceX.absoluteValue > distanceY.absoluteValue) {
            if (distanceX > 0) {
                Log.d(DEBUG_TAG, "swiped right")
                currentDirection = Direction.RIGHT
                binding.ball.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ball_right
                    )
                )
                animate(HORIZONTAL_MOVING, animationDistance, animationTime)
            } else {
                Log.d(DEBUG_TAG, "swiped left")
                currentDirection = Direction.LEFT
                binding.ball.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ball_left
                    )
                )
                animate(HORIZONTAL_MOVING, -animationDistance, animationTime)
            }
        } else {
            if (distanceY > 0) {
                Log.d(DEBUG_TAG, "swiped down")
                currentDirection = Direction.DOWN
                binding.ball.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ball_down
                    )
                )
                animate(VERTICAL_MOVING, animationDistance, animationTime)
            } else {
                Log.d(DEBUG_TAG, "swiped up")
                currentDirection = Direction.UP
                binding.ball.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ball_up
                    )
                )
                animate(VERTICAL_MOVING, -animationDistance, animationTime)
            }
        }
    }

    private fun animate(propertyName: String, distance: Float, duration: Long) {
        animator = ObjectAnimator.ofFloat(binding.ball, propertyName, distance)
        animator?.duration = duration
        animator?.interpolator = LinearInterpolator()
        animator?.start()
        Log.d(DEBUG_TAG, "started animation")
        getCurrentPosition()
    }

    private fun getCurrentPosition() { //делает все :(
        animator?.addUpdateListener {
            binding.ball.getLocationInWindow(ballPosition)
            ballPosition = ballUtils!!.getActualLocation(ballPosition, currentDirection)
            if (isBallInsideAnyCell(ballPosition)) {
                if (!currentCell!!.isCurrent) {
                    Log.d(DEBUG_TAG, "ball in cell ${currentCell?.coordinates.contentToString()}")
                    requireActivity().findViewById<ImageView>(currentCell!!.id).alpha = 0.5f
                    currentCell!!.isCurrent = true
                    removePastCell()
                }
                if (cells.filterNot { it.visited }.size == 1) {
                    Log.d(DEBUG_TAG, "level passed")
                    isGameActive = false
                    animator?.cancel()
                    requireActivity().supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        add<YouWinFragment>(R.id.fragment_container_view)
                    }
                }
            } else {
                Log.d(DEBUG_TAG, "level failed")
                isGameActive = false
                animator?.cancel()
                requireActivity().supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add<TryAgainFragment>(R.id.fragment_container_view)
                }
            }
        }
    }

    private fun isBallInsideAnyCell(ballLocation: IntArray): Boolean {
        currentCell = cells.filterNot { it.visited }.find {
            it.coordinates[0] <= ballLocation[0] &&
                    it.coordinates[0] + cellWidth > ballLocation[0] &&
                    it.coordinates[1] <= ballLocation[1] &&
                    it.coordinates[1] + cellWidth > ballLocation[1]
        }
        return currentCell != null
    }

    private fun removePastCell() {
        for (cell in cells.filter { it.isCurrent }) {
            if (cell != currentCell) {
                cell.visited = true
                cell.isCurrent = false
                requireActivity().findViewById<ImageView>(cell.id).alpha = 0f
                Log.d(DEBUG_TAG, "removed cell ${cell.coordinates.contentToString()}")
            }
        }
    }

    private fun inflateCellsList() {
        for (i in 0 until binding.cellsContainer.childCount) {
            Log.d(DEBUG_TAG, "count of cells: ${binding.cellsContainer.childCount}")
            val location = IntArray(2)
            val cell = binding.cellsContainer.getChildAt(i)
            cell.getLocationInWindow(location)
            cells.add(Cell(coordinates = location, id = cell.id))
            Log.d(DEBUG_TAG, "added cell with coordinates ${location.contentToString()}")
        }
        cellWidth = binding.cell1.width.toFloat()
        Log.d(DEBUG_TAG, "cells width: $cellWidth")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onPause() {
        super.onPause()
        animator?.cancel()
    }
}
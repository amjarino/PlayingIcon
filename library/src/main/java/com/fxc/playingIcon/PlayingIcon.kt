package com.fxc.playingIcon

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.abs
import kotlin.math.sin

/**
 * @author fxc
 * @date 2018/3/21
 */
class PlayingIcon : View {
	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		initAttr(attrs)
	}

	private fun initAttr(attrs: AttributeSet) {
		mPaint = Paint()
		mPaint.color = ContextCompat.getColor(context, R.color.blank)
//		val array = context.obtainStyledAttributes(attrs, R.styleable.PlayingIcon)
//		array.recycle()
		createPoints(pointNum)
	}

	private fun createPoints(size: Int) {
		pointers.clear()
		for (i in 0 until size) {
			pointers.add(Pointer(0f, 0f, 0f, 0f))
		}
	}

	private lateinit var mPaint: Paint
	private val pointers = ArrayList<Pointer>()
	private var pointNum = 4
	private var basePointX = 0f
	private var basePointY = 0f
	private var pointMargin = 0f
	private var pointWidth = 0f
	private var spaceRatio = 0.4f

	private val random = Random()


	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		locatePoints()
	}

	private fun locatePoints() {
		val itemWidth = getItemWidth(spaceRatio, pointNum)
		pointWidth = itemWidth[0]
		pointMargin = itemWidth[1]
		basePointX = paddingLeft.toFloat()
		basePointY = (height - paddingBottom).toFloat()
		var lastRatio = 0f
		for (point in pointers) {
			var ratio = (0.1 * random.nextInt(10)).toFloat()
			var delta = abs(ratio - lastRatio)
			while (lastRatio != 0f && !(1 / 4 < delta && delta < 0.25 * 3)) {
				ratio = (0.1 * random.nextInt(10)).toFloat()
				delta = abs(ratio - lastRatio)
			}
			point.ratio = ratio
			point.left = basePointX
			point.right = basePointX + pointWidth
			point.top = abs(sin(point.ratio)) * getHeightWithoutPadding()
			point.bottom = basePointY
			basePointX += pointMargin + pointWidth
			lastRatio = point.ratio
		}
		invalidate()
	}

	override fun onDraw(canvas: Canvas) {
		super.onDraw(canvas)
		for (point in pointers) {
			canvas.drawRect(point.rectF, mPaint)
			point.ratio += 0.1f
			point.top = abs(sin(point.ratio)) * getHeightWithoutPadding() + paddingTop
		}
		postDelayed({
			invalidate()
		}, 16)
	}

	private fun getWidthWithoutPadding(): Int {
		return width - paddingLeft - paddingRight
	}

	private fun getHeightWithoutPadding(): Int {
		return height - paddingBottom
	}

	/**
	 * 获取 Item width
	 * @param ratio item 和间隙的比例
	 * @param num item 数目
	 * @return [FloatArray] index of 0 is item width, index of 1 is item space width
	 */
	private fun getItemWidth(ratio: Float, num: Int): FloatArray {
		val itemWidth = getWidthWithoutPadding() / ((ratio + 1) * num - ratio)
		return floatArrayOf(itemWidth, itemWidth * ratio)
	}

	data class Pointer(var left: Float, var top: Float, var right: Float, var bottom: Float) {
		var ratio = 0f
		private val rect = RectF()
		var rectF = rect
			get() {
				rect.set(left, top, right, bottom)
				return rect
			}

	}
}
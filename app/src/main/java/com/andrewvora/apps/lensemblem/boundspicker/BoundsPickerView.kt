package com.andrewvora.apps.lensemblem.boundspicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import com.andrewvora.apps.lensemblem.R

/**
 * Based on code from
 * @link https://github.com/edmodo/cropper
 *
 * Created on 4/2/2018.
 * @author Andrew Vorakrajangthiti
 */
class BoundsPickerView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
: ImageView(context, attrs, defStyleAttr) {

    private var selectionBorderPaint: Paint
    private var cornerPaint: Paint
    private var darkOverlayPaint: Paint
    private var cornerTouchRadius: Float = 0.0f
    private var boundaryRadius: Float = 0.0f

    private var cornerLineSize: Float = 0.0f
    private var borderLineSize: Float = 0.0f
    private var cornerLineLength: Float = 0.0f

    private lateinit var bitmapRect: RectF
    private var touchOffset: PointF = PointF()
    private var pressedHandle: Handle? = null

    init {
        cornerTouchRadius  = resources.getDimension(R.dimen.bounds_picker_view_touch_radius)
        boundaryRadius = resources.getDimension(R.dimen.bounds_picker_view_boundary_radius)
        cornerLineSize = resources.getDimension(R.dimen.bounds_picker_view_corner_line_size)
        cornerLineLength = resources.getDimension(R.dimen.bounds_picker_view_corner_line_length)
        borderLineSize = resources.getDimension(R.dimen.bounds_picker_view_border_line_size)

        selectionBorderPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = borderLineSize
            color = ContextCompat.getColor(context, R.color.white)
        }
        cornerPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = cornerLineSize
            color = ContextCompat.getColor(context, R.color.white)
        }
        darkOverlayPaint = Paint().apply {
            style = Paint.Style.FILL
            color = ContextCompat.getColor(context, R.color.translucent_black)
        }
    }

    fun setCoordinates(left: Float, top: Float, right: Float, bottom: Float) {
        Edge.LEFT.coordinate = left
        Edge.TOP.coordinate = top
        Edge.RIGHT.coordinate = right
        Edge.BOTTOM.coordinate = bottom
        invalidate()
    }

    fun getLeftEdge(): Float = Edge.LEFT.coordinate
    fun getTopEdge(): Float = Edge.TOP.coordinate
    fun getRightEdge(): Float = Edge.RIGHT.coordinate
    fun getBottomEdge(): Float = Edge.BOTTOM.coordinate

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        bitmapRect = getBitmapRect()
        initSelectionWindow()
    }

    private fun getBitmapRect(): RectF {
        if (drawable == null) {
            return RectF()
        }

        val matrixValues = FloatArray(9)
        imageMatrix.getValues(matrixValues)

        val scaleX = matrixValues[Matrix.MSCALE_X]
        val scaleY = matrixValues[Matrix.MSCALE_Y]
        val transX = matrixValues[Matrix.MTRANS_X]
        val transY = matrixValues[Matrix.MTRANS_Y]

        val drawableIntrinsicWidth = drawable.intrinsicWidth
        val drawableIntrinsicHeight = drawable.intrinsicHeight

        val drawableDisplayWidth = Math.round(drawableIntrinsicWidth * scaleX)
        val drawableDisplayHeight = Math.round(drawableIntrinsicHeight * scaleY)

        val left = Math.max(transX, 0f)
        val top = Math.max(transY, 0f)
        val right = Math.min(left + drawableDisplayWidth, width.toFloat())
        val bottom = Math.min(top + drawableDisplayHeight, height.toFloat())

        return RectF(left, top, right, bottom)
    }

    private fun initSelectionWindow() {
        val horizontalPadding = 0.1f * bitmapRect.width()
        val verticalPadding = 0.1f * bitmapRect.height()

        Edge.LEFT.coordinate = bitmapRect.left + horizontalPadding
        Edge.TOP.coordinate = bitmapRect.top + verticalPadding
        Edge.RIGHT.coordinate = bitmapRect.right - horizontalPadding
        Edge.BOTTOM.coordinate = bitmapRect.bottom - verticalPadding
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawDarkOverlay(canvas)
        drawSelectionBorders(canvas)
        drawSelectionCorners(canvas)
    }

    private fun drawDarkOverlay(canvas: Canvas?) {
        val left = Edge.LEFT.coordinate
        val right = Edge.RIGHT.coordinate
        val top = Edge.TOP.coordinate
        val bottom = Edge.BOTTOM.coordinate

        canvas?.drawRect(bitmapRect.left, bitmapRect.top, bitmapRect.right, top, darkOverlayPaint)
        canvas?.drawRect(bitmapRect.left, bottom, bitmapRect.right, bitmapRect.bottom, darkOverlayPaint)
        canvas?.drawRect(bitmapRect.left, top, left, bottom, darkOverlayPaint)
        canvas?.drawRect(right, top, bitmapRect.right, bottom, darkOverlayPaint)
    }

    private fun drawSelectionBorders(canvas: Canvas?) {
        canvas?.drawRect(
                Edge.LEFT.coordinate,
                Edge.TOP.coordinate,
                Edge.RIGHT.coordinate,
                Edge.BOTTOM.coordinate,
                selectionBorderPaint)
    }

    private fun drawSelectionCorners(canvas: Canvas?) {
        val left = Edge.LEFT.coordinate
        val right = Edge.RIGHT.coordinate
        val top = Edge.TOP.coordinate
        val bottom = Edge.BOTTOM.coordinate

        val lateralOffset = (cornerLineSize - borderLineSize) / 2
        val startOffset = cornerLineSize - (borderLineSize / 2)

        // top left corner
        canvas?.drawLine(left - lateralOffset, top - startOffset, left - lateralOffset, top + cornerLineLength, cornerPaint)
        canvas?.drawLine(left - startOffset, top - lateralOffset, left + cornerLineLength, top - lateralOffset, cornerPaint)

        // top right corner
        canvas?.drawLine(right + lateralOffset, top - startOffset, right + lateralOffset, top + cornerLineLength, cornerPaint)
        canvas?.drawLine(right + startOffset, top - lateralOffset, right - cornerLineLength, top - lateralOffset, cornerPaint)

        // bottom left corner
        canvas?.drawLine(left - lateralOffset, bottom + startOffset, left - lateralOffset, bottom - cornerLineLength, cornerPaint)
        canvas?.drawLine(left - startOffset, bottom + lateralOffset, left + cornerLineLength, bottom + lateralOffset, cornerPaint)

        // bottom right
        canvas?.drawLine(right + lateralOffset, bottom + startOffset, right + lateralOffset, bottom - cornerLineLength, cornerPaint)
        canvas?.drawLine(right + startOffset, bottom + lateralOffset, right - cornerLineLength, bottom + lateralOffset, cornerPaint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEnabled.not()) {
            return false
        }

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                onActionDown(event.x, event.y)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                onActionUp()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                onActionMove(event.x, event.y)
                parent.requestDisallowInterceptTouchEvent(true)
                return true
            }
        }

        return false
    }

    private fun onActionDown(x: Float, y: Float) {
        val left = Edge.LEFT.coordinate
        val top = Edge.TOP.coordinate
        val right = Edge.RIGHT.coordinate
        val bottom = Edge.BOTTOM.coordinate

        pressedHandle = Handle.getPressedHandle(x, y, left, top, right, bottom, cornerTouchRadius)

        pressedHandle?.let {
            Handle.getOffset(it, x, y, left, top, right, bottom, touchOffset)
            invalidate()
        }
    }

    private fun onActionUp() {
        pressedHandle?.let {
            pressedHandle = null
            invalidate()
        }
    }

    private fun onActionMove(x: Float, y: Float) {
        if (pressedHandle == null) {
            return
        }

        pressedHandle?.updateSelectionWindow(
                x + touchOffset.x,
                y + touchOffset.y,
                bitmapRect,
                boundaryRadius)
        invalidate()
    }
}

private enum class Edge {
    LEFT,
    TOP,
    RIGHT,
    BOTTOM;

    var coordinate: Float = 0f

    fun offset(distance: Float) {
        coordinate += distance
    }

    fun adjustCoordinate(x: Float,
                         y: Float,
                         imageRect: RectF,
                         boundaryRadius: Float,
                         aspectRatio: Float) {
        coordinate = when (this) {
            LEFT -> { adjustLeft(x, imageRect, boundaryRadius, aspectRatio) }
            TOP -> { adjustTop(y, imageRect, boundaryRadius, aspectRatio) }
            RIGHT -> { adjustRight(x, imageRect, boundaryRadius, aspectRatio) }
            BOTTOM -> { adjustBottom(y, imageRect, boundaryRadius, aspectRatio) }
        }
    }

    fun alignToRectBoundary(imageRect: RectF): Float {
        val prevCoordinate = coordinate
        coordinate = when (this) {
            LEFT -> { imageRect.left }
            TOP -> { imageRect.top }
            RIGHT -> { imageRect.right }
            BOTTOM -> { imageRect.bottom }
        }

        return coordinate - prevCoordinate
    }

    fun isOutsideMargin(rectF: RectF, margin: Float): Boolean {
        return when (this) {
            LEFT -> { coordinate - rectF.left < margin }
            TOP -> { coordinate - rectF.top < margin }
            RIGHT -> { rectF.right - coordinate < margin }
            BOTTOM -> { rectF.bottom - coordinate < margin }
        }
    }

    companion object {
        // min distance between any two parallel edges
        private const val MIN_EDGE_DISTANCE_PX = 40

        @Suppress("unused")
        fun width(): Float {
            return Edge.RIGHT.coordinate - Edge.LEFT.coordinate
        }

        fun height(): Float {
            return Edge.BOTTOM.coordinate - Edge.TOP.coordinate
        }

        fun adjustLeft(x: Float,
                       imageRect: RectF,
                       imageBoundaryRadius: Float,
                       aspectRatio: Float): Float {
            return if (x - imageRect.left < imageBoundaryRadius) {
                imageRect.left
            } else {
                var xHorizontal = Float.POSITIVE_INFINITY
                var xVertical = Float.POSITIVE_INFINITY

                if (x >= RIGHT.coordinate - MIN_EDGE_DISTANCE_PX) {
                    xHorizontal = RIGHT.coordinate - MIN_EDGE_DISTANCE_PX
                }

                if ((RIGHT.coordinate - x) / aspectRatio <= MIN_EDGE_DISTANCE_PX) {
                    xVertical = RIGHT.coordinate - (MIN_EDGE_DISTANCE_PX * aspectRatio)
                }

                Math.min(x, Math.min(xHorizontal, xVertical))
            }
        }

        fun adjustRight(x: Float,
                        imageRect: RectF,
                        imageBoundaryRadius: Float,
                        aspectRatio: Float): Float {
            return if (imageRect.right - x < imageBoundaryRadius) {
                imageRect.right
            } else {
                var xHorizontal = Float.NEGATIVE_INFINITY
                var xVertical = Float.NEGATIVE_INFINITY

                if (x <= LEFT.coordinate + MIN_EDGE_DISTANCE_PX) {
                    xHorizontal = LEFT.coordinate + MIN_EDGE_DISTANCE_PX
                }

                if ((x - LEFT.coordinate) / aspectRatio <= MIN_EDGE_DISTANCE_PX) {
                    xVertical = LEFT.coordinate + (MIN_EDGE_DISTANCE_PX * aspectRatio)
                }

                Math.max(x, Math.max(xHorizontal, xVertical))
            }
        }

        fun adjustTop(y: Float,
                      imageRect: RectF,
                      imageBoundaryRadius: Float,
                      aspectRatio: Float): Float {
            return if (y - imageRect.top < imageBoundaryRadius) {
                imageRect.top
            } else {
                var yVertical = Float.POSITIVE_INFINITY
                var yHorizontal = Float.POSITIVE_INFINITY

                if (y >= BOTTOM.coordinate - MIN_EDGE_DISTANCE_PX) {
                    yHorizontal = BOTTOM.coordinate - MIN_EDGE_DISTANCE_PX
                }
                if ((BOTTOM.coordinate - y) * aspectRatio <= MIN_EDGE_DISTANCE_PX) {
                    yVertical = BOTTOM.coordinate - (MIN_EDGE_DISTANCE_PX / aspectRatio)
                }

                Math.min(y, Math.min(yHorizontal, yVertical))
            }
        }

        fun adjustBottom(y: Float,
                         imageRect: RectF,
                         imageBoundaryRadius: Float,
                         aspectRatio: Float): Float {
            return if (imageRect.bottom - y < imageBoundaryRadius) {
                imageRect.bottom
            } else {
                var yVertical = Float.NEGATIVE_INFINITY
                var yHorizontal = Float.NEGATIVE_INFINITY

                if (y <= TOP.coordinate + MIN_EDGE_DISTANCE_PX) {
                    yVertical = TOP.coordinate + MIN_EDGE_DISTANCE_PX
                }
                if ((y - TOP.coordinate) * aspectRatio <= MIN_EDGE_DISTANCE_PX) {
                    yHorizontal = TOP.coordinate + (MIN_EDGE_DISTANCE_PX / aspectRatio)
                }

                Math.max(y, Math.max(yHorizontal, yVertical))
            }
        }
    }
}

private enum class Handle(private val handleHelper: HandleHelper) {
    TOP_LEFT(HandleHelper(Edge.TOP, Edge.LEFT)),
    TOP_RIGHT(HandleHelper(Edge.TOP, Edge.RIGHT)),
    BOTTOM_LEFT(HandleHelper(Edge.BOTTOM, Edge.LEFT)),
    BOTTOM_RIGHT(HandleHelper(Edge.BOTTOM, Edge.RIGHT)),
    LEFT(HandleHelper(null, Edge.LEFT)),
    TOP(HandleHelper(Edge.TOP, null)),
    RIGHT(HandleHelper(null, Edge.RIGHT)),
    BOTTOM(HandleHelper(Edge.BOTTOM, null)),
    CENTER(HandleHelper.CenterHandleHelper());

    fun updateSelectionWindow(x: Float, y: Float, imageRect: RectF, boundaryRadius: Float) {
        handleHelper.updateSelectionWindow(x, y, imageRect, boundaryRadius)
    }

    companion object {
        fun getPressedHandle(x: Float,
                             y: Float,
                             left: Float,
                             top: Float,
                             right: Float,
                             bottom: Float,
                             targetRadius: Float): Handle? {
            var closestHandle: Handle? = null
            var closestDistance = Float.POSITIVE_INFINITY

            val distanceToTopLeft = distanceBetweenTwoPoints(x, y, left, top)
            if (distanceToTopLeft < closestDistance) {
                closestDistance = distanceToTopLeft
                closestHandle = Handle.TOP_LEFT
            }

            val distanceToTopRight = distanceBetweenTwoPoints(x, y, right, top)
            if (distanceToTopRight < closestDistance) {
                closestDistance = distanceToTopRight
                closestHandle = Handle.TOP_RIGHT
            }

            val distanceToBottomLeft = distanceBetweenTwoPoints(x, y, left, bottom)
            if (distanceToBottomLeft < closestDistance) {
                closestDistance = distanceToBottomLeft
                closestHandle = Handle.BOTTOM_LEFT
            }

            val distanceToBottomRight = distanceBetweenTwoPoints(x, y, right, bottom)
            if (distanceToBottomRight < closestDistance) {
                closestDistance = distanceToBottomRight
                closestHandle = Handle.BOTTOM_RIGHT
            }

            if (closestDistance <= targetRadius && closestHandle != null) {
                return closestHandle
            }

            when {
                isInHorizontalTargetZone(x, y, left, right, top, targetRadius) -> Handle.TOP
                isInHorizontalTargetZone(x, y, left, right, bottom, targetRadius) -> Handle.BOTTOM
                isInVerticalTargetZone(x, y, left, top, bottom, targetRadius) -> Handle.LEFT
                isInVerticalTargetZone(x, y, right, top, bottom, targetRadius) -> Handle.RIGHT
            }

            if (isWithinBounds(x, y, left, top, right, bottom)) {
                return Handle.CENTER
            }

            return null
        }

        private fun distanceBetweenTwoPoints(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            val side1 = x2 - x1
            val side2 = y2 - y1
            return Math.sqrt((side1 * side1 + side2 * side2).toDouble()).toFloat()
        }

        fun getOffset(handle: Handle,
                      x: Float,
                      y: Float,
                      left: Float,
                      top: Float,
                      right: Float,
                      bottom: Float,
                      touchOffsetOutput: PointF) {
            var touchOffsetX = 0f
            var touchOffsetY = 0f
            when (handle) {
                TOP_LEFT -> {
                    touchOffsetX = left - x
                    touchOffsetY = top - y
                }
                TOP_RIGHT -> {
                    touchOffsetX = right - x
                    touchOffsetY = top - y
                }
                BOTTOM_LEFT -> {
                    touchOffsetX = left - x
                    touchOffsetY = bottom - y
                }
                BOTTOM_RIGHT -> {
                    touchOffsetX = right - x
                    touchOffsetY = bottom - y
                }
                LEFT -> {
                    touchOffsetX = left - x
                    touchOffsetY = 0f
                }
                TOP -> {
                    touchOffsetX = 0f
                    touchOffsetY = top - y
                }
                RIGHT -> {
                    touchOffsetX = right - x
                    touchOffsetY = 0f
                }
                BOTTOM -> {
                    touchOffsetX = 0f
                    touchOffsetY = bottom - y
                }
                CENTER -> {
                    touchOffsetX = ((right + left) / 2) - x
                    touchOffsetY = ((top + bottom) / 2) - y
                }
            }

            touchOffsetOutput.x = touchOffsetX
            touchOffsetOutput.y = touchOffsetY
        }

        private fun isInHorizontalTargetZone(x: Float,
                                             y: Float,
                                             handleXStart: Float,
                                             handleXEnd: Float,
                                             handleY: Float,
                                             targetRadius: Float): Boolean {
            return (x > handleXStart && x < handleXEnd && Math.abs(y - handleY) <= targetRadius)
        }

        private fun isInVerticalTargetZone(x: Float,
                                           y: Float,
                                           handleX: Float,
                                           handleYStart: Float,
                                           handleYEnd: Float,
                                           targetRadius: Float): Boolean {
            return (Math.abs(x - handleX) <= targetRadius && y > handleYStart && y < handleYEnd)
        }

        private fun isWithinBounds(x: Float,
                                   y: Float,
                                   left: Float,
                                   top: Float,
                                   right: Float,
                                   bottom: Float): Boolean {
            return x in left..right && y >= top && y <= bottom
        }
    }
}

private open class HandleHelper(horizontalEdge: Edge?,
                                verticalEdge: Edge?) {
    companion object {
        private const val UNFIXED_ASPECT_RATIO_CONSTANT = 1f
    }

    var edges: Pair<Edge?, Edge?> = Pair(horizontalEdge, verticalEdge)

    open fun updateSelectionWindow(x: Float,
                              y: Float,
                              imageRect: RectF,
                              boundaryRadius: Float) {
        edges.first?.adjustCoordinate(x, y, imageRect, boundaryRadius, UNFIXED_ASPECT_RATIO_CONSTANT)
        edges.second?.adjustCoordinate(x, y, imageRect, boundaryRadius, UNFIXED_ASPECT_RATIO_CONSTANT)
    }

    class CenterHandleHelper : HandleHelper(null, null) {
        override fun updateSelectionWindow(x: Float, y: Float, imageRect: RectF, boundaryRadius: Float) {
            val left = Edge.LEFT.coordinate
            val top = Edge.TOP.coordinate
            val right = Edge.RIGHT.coordinate
            val bottom = Edge.BOTTOM.coordinate

            val currentCenterX = (left + right) / 2
            val currentCenterY = (top + bottom) / 2

            val offsetX = x - currentCenterX
            val offsetY = y - currentCenterY

            Edge.LEFT.offset(offsetX)
            Edge.TOP.offset(offsetY)
            Edge.RIGHT.offset(offsetX)
            Edge.BOTTOM.offset(offsetY)

            if (Edge.LEFT.isOutsideMargin(imageRect, boundaryRadius)) {
                Edge.RIGHT.offset(Edge.LEFT.alignToRectBoundary(imageRect))
            } else if (Edge.RIGHT.isOutsideMargin(imageRect, boundaryRadius)) {
                Edge.LEFT.offset(Edge.RIGHT.alignToRectBoundary(imageRect))
            }

            if (Edge.TOP.isOutsideMargin(imageRect, boundaryRadius)) {
                Edge.BOTTOM.offset(Edge.TOP.alignToRectBoundary(imageRect))
            } else if (Edge.BOTTOM.isOutsideMargin(imageRect, boundaryRadius)) {
                Edge.TOP.offset(Edge.BOTTOM.alignToRectBoundary(imageRect))
            }
        }
    }
}
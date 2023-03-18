package bas.leanback.effect

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.zhang.phoenix.R

internal class EffectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val params: EffectParams = EffectParams.Builder(context, attrs, defStyleAttr).build(),
) : View(context, attrs, defStyleAttr) {

    private val strokePaint = Paint()
    private val strokeRectF = RectF()
    private val strokePath: Path = Path()

    private val shadowPaint = Paint()
    private val shadowRectF = RectF()
    private val shadowPath: Path = Path()
    private var drawOnFocus: Boolean = false
    private var drawOnTouch: Boolean = false

//    @SuppressLint("RestrictedApi")
//    private val pathProvider: ShapeAppearancePathProvider = ShapeAppearancePathProvider.getInstance()
//
//    private val shapeAppearanceModel = ShapeAppearanceModel.builder()
//        .setBottomLeftCornerSize(params.cornerSizeBottomLeft)
//        .setBottomRightCornerSize(params.cornerSizeBottomRight)
//        .setTopLeftCornerSize(params.cornerSizeTopLeft)
//        .setTopRightCornerSize(params.cornerSizeTopRight)
//        .build()


    /**
     * 阴影往内偏移的距离:属于绘制优化策略
     */
    private val shadowInset: Float

    //呼吸动画
    private val breatheAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(this, "alpha", 1f, 0.2f, 1f).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = params.breatheDuration.toLong()
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            startDelay = params.shimmerDelay.toLong()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationCancel(animation: Animator) {
                    super.onAnimationCancel(animation)
                    alpha = 1f
                }
            })
        }
    }

    init {
        // 需禁用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        val minInset = (params.strokeWidth / 4f).coerceAtLeast(1f)
        /**
         * 效果优化：当存在边框和圆角时，rect往内部缩小 1/4边框大小
         */
        shadowInset = if (strokeEnabled && params.isRoundedShape) minInset else 0f

        if (shadowEnabled) {
            this.shadowPaint.isAntiAlias = true
            this.shadowPaint.maskFilter =
                BlurMaskFilter(params.shadowWidth + shadowInset, BlurMaskFilter.Blur.OUTER)
            this.shadowPaint.color = params.shadowColor
        }

        if (strokeEnabled) {
            this.strokePaint.isAntiAlias = true
            this.strokePaint.color = params.strokeColor
            this.strokePaint.strokeWidth = params.strokeWidth
            this.strokePaint.style = Paint.Style.STROKE
            this.strokePaint.maskFilter = BlurMaskFilter(minInset, BlurMaskFilter.Blur.NORMAL)
        }
        drawOnFocus = false
        drawOnTouch = false
//        setBackgroundResource(R.drawable.effect_layout_transparent_slt_bas)
    }

    private val shadowEnabled: Boolean get() = params.shadowWidth > 0
    private val strokeEnabled: Boolean get() = params.strokeWidth > 0

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
        updateShadowParamsOnSizeChanged(width, height, oldw, oldh)
        updateStrokeParamsOnSizeChanged(width, height, oldw, oldh)
        println("EffectView@$this:onSizeChanged")
    }

    private fun updateShadowParamsOnSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        if (!shadowEnabled)
            return

        val newLeft = paddingLeft + params.shadowWidth + shadowInset
        val newTop = paddingTop + params.shadowWidth + shadowInset
        val newRight = width - paddingRight.toFloat() - params.shadowWidth - shadowInset
        val newBottom = height - paddingBottom.toFloat() - params.shadowWidth - shadowInset

        if (newLeft == shadowRectF.left
            && newTop == shadowRectF.top
            && newRight == shadowRectF.right
            && newBottom == shadowRectF.bottom
        )
            return

        shadowRectF.set(newLeft, newTop, newRight, newBottom)

        shadowPath.rewind()
        //必须使用这种方式，否则在某些情况下会出现中间有个阴影色的色块
        if (params.isRoundedShape) {
            shadowPath.addRoundRect(shadowRectF, params.cornerRadius, Path.Direction.CW)
        } else {
            shadowPath.addRoundRect(shadowRectF, 0f, 0f, Path.Direction.CW)
        }

//        pathProvider.calculatePath(
//            shapeAppearanceModel,
//            1f /*interpolation*/,
//            shadowRectF,
//            shadowPath
//        )
    }

    private fun updateStrokeParamsOnSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        if (!strokeEnabled)
            return

        //stroke都需要考虑一半的宽度，此数字改了效果不好：尤其是只有stroke没有阴影的情况下，由于stroke的绘制如果从左上角顶点作为边开始绘制，会有一半的边框被裁剪
        val offset = (params.strokeWidth / 2f).coerceAtLeast(1f)
        val newLeft = paddingLeft + params.shadowWidth + offset
        val newTop = paddingTop + params.shadowWidth + offset
        val newRight = width - paddingRight - params.shadowWidth - offset
        val newBottom = height - paddingBottom - params.shadowWidth - offset

        //rect 未发生变化，不做path更新
        if (newLeft == strokeRectF.left
            && newTop == strokeRectF.top
            && newRight == strokeRectF.right
            && newBottom == strokeRectF.bottom
        )
            return

        strokeRectF.set(newLeft, newTop, newRight, newBottom)
        strokePath.rewind()
        //必须使用这种方式，否则在某些情况下会出现中间有个阴影色的色块
        if (params.isRoundedShape) {
            strokePath.addRoundRect(strokeRectF, params.cornerRadius, Path.Direction.CW)
        } else {
            strokePath.addRect(strokeRectF, Path.Direction.CW)
        }
//        pathProvider.calculatePath(
//            shapeAppearanceModel,
//            1f /*interpolation*/,
//            strokeRectF,
//            strokePath
//        )
    }

    /**
     * 用于处理按压状态之类的变化
     */
    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateDrawFlagOnPressedState(isPressed)
        println("EffectView@$this:drawableStateChanged isPressed=$isPressed")
    }

    internal fun updateDrawFlagOnPressedState(isPress: Boolean) {
        println("EffectView@$this:updateDrawFlagOnPressedState isPressed=$isPress")
        if (!params.pressStateEnable)
            return
        val changed = params.pressStateEnable && isPress
        if (changed != drawOnTouch) {
            drawOnTouch = changed
            tryInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        println("EffectView@$this:onDraw shadowEnabled=$shadowEnabled strokeEnabled=${strokeEnabled}")
//        val canDraw = drawEnabled || (params.pressStateEnable && isPressed)
//        if (!canDraw) {
//            return
//        }
        if (!drawOnFocus && !drawOnTouch)
            return

        if (shadowEnabled) {
//            canvas.save()
            canvas.drawPath(this.shadowPath, this.shadowPaint)
//            canvas.restore()
        }
        if (strokeEnabled) {
//            canvas.save()
            canvas.drawPath(this.strokePath, this.strokePaint)
//            canvas.restore()
        }
    }

    fun startAnimation() {
        println("EffectView@$this:startAnimation")
        drawOnFocus = true
        if (params.breatheEnabled) {
            if (breatheAnim.isStarted)
                breatheAnim.cancel()
            breatheAnim.start()
        }
        tryInvalidate()
    }

    fun stopAnimation() {
        println("EffectView@$this:stopAnimation")
        drawOnFocus = false
        if (params.breatheEnabled && breatheAnim.isStarted) {
            breatheAnim.cancel()
        }
        tryInvalidate()
    }

    private fun tryInvalidate() {
        if (this.width > 0 && this.height > 0) {
            println("EffectView@$this:tryInvalidate invalidate ${this.width} ${this.height} ${(this.parent as ViewGroup).width} ${(this.parent as ViewGroup).height}")
            invalidate()
        } else {
//            val parent = this.parent as ViewGroup
//            if(parent.width ==0 && parent.height == 0){
//                //parent都还没有设置大小
//            }
            requestLayout()
            println("EffectView@$this:tryInvalidate requestLayout  ${this.width} ${this.height} ${(this.parent as ViewGroup).width} ${(this.parent as ViewGroup).height}")
        }
    }
}
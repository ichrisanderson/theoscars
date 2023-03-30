/*
 * Copyright 2023 Chris Anderson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chrisa.theoscars.core.ui.common

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

@Composable
fun InteractiveRatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onRatingChange: ((Int) -> Unit),
    itemCount: Int = 5,
    itemSize: Dp = 32.dp,
    itemSpacing: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.secondary,
) {
    Box(modifier) {
        val painterBackground: VectorPainter =
            rememberVectorPainter(image = Icons.Outlined.StarOutline)
        val painterForeground: VectorPainter = rememberVectorPainter(image = Icons.Outlined.Star)
        val colorFilterFilled: ColorFilter = remember(color) { ColorFilter.tint(color) }

        val spacePx: Float = LocalDensity.current.run { itemSpacing.toPx() }
        val itemWidthPx: Float = LocalDensity.current.run { itemSize.toPx() }
        val totalWidth: Dp = itemSize * itemCount + itemSpacing * (itemCount - 1)

        val itemIntervals = remember {
            ratingItemPositions(itemWidthPx, spacePx, itemCount)
        }
        var interactiveRating by remember { mutableStateOf(rating) }

        val gestureModifier = Modifier
            .pointerInput(Unit) {
                val ratingBarWidth = size.width.toFloat()
                detectDragGestures { change, _ ->
                    interactiveRating = getRatingFromTouchPosition(
                        xPos = change.position.x,
                        itemIntervals = itemIntervals,
                        ratingBarDimension = ratingBarWidth,
                        space = spacePx,
                        totalCount = itemCount,
                    )
                }
            }
            .pointerInput(Unit) {
                val ratingBarWidth = size.width.toFloat()

                detectTapGestures { change ->
                    interactiveRating = getRatingFromTouchPosition(
                        xPos = change.x,
                        itemIntervals = itemIntervals,
                        ratingBarDimension = ratingBarWidth,
                        space = spacePx,
                        totalCount = itemCount,
                    )
                    onRatingChange.invoke(interactiveRating)
                }
            }

        Box(
            modifier = Modifier
                .then(gestureModifier)
                .width(totalWidth)
                .height(itemSize)
                .drawBehind {
                    drawRatingPainters(
                        rating = interactiveRating,
                        itemCount = itemCount,
                        painterEmpty = painterBackground,
                        painterFilled = painterForeground,
                        tintEmpty = color,
                        colorFilterFilled = colorFilterFilled,
                        space = spacePx,
                    )
                },
        )
    }
}

private fun ratingItemPositions(
    itemSize: Float,
    space: Float,
    totalCount: Int,
): List<ClosedFloatingPointRange<Float>> {
    val list = mutableListOf<ClosedFloatingPointRange<Float>>()

    for (i in 0 until totalCount) {
        val start = itemSize * i + space * i
        list.add(start..start + itemSize)
    }

    return list
}

private fun getRatingFromTouchPosition(
    xPos: Float,
    itemIntervals: List<ClosedFloatingPointRange<Float>>,
    ratingBarDimension: Float,
    space: Float,
    totalCount: Int,
): Int {
    val ratingBarItemSize = (ratingBarDimension - space * (totalCount - 1)) / totalCount
    val ratingInterval = ratingBarItemSize + space

    var rating = 0f
    var isInInterval = false
    itemIntervals.forEachIndexed { index: Int, interval: ClosedFloatingPointRange<Float> ->
        if (interval.contains(xPos)) {
            rating = index.toFloat() + (xPos - interval.start) / ratingBarItemSize
            isInInterval = true
        }
    }

    rating =
        if (!isInInterval) {
            (1 + xPos / ratingInterval).toInt().coerceAtMost(totalCount).toFloat()
        } else {
            rating
        }

    return ceil(rating).toInt()
}

private fun DrawScope.drawRatingPainters(
    rating: Int,
    itemCount: Int,
    painterEmpty: Painter,
    painterFilled: Painter,
    tintEmpty: Color?,
    colorFilterFilled: ColorFilter?,
    space: Float,
) {
    val imageWidth = size.height

    val startOfEmptyItems = imageWidth * itemCount + space * (itemCount - 1)
    val endOfFilledItems = rating * imageWidth + rating * space
    val rectWidth = startOfEmptyItems - endOfFilledItems

    drawWithLayer {
        // Draw foreground rating items
        for (i in 0 until itemCount) {
            val start = imageWidth * i + space * i
            // Destination
            translate(left = start, top = 0f) {
                with(painterFilled) {
                    draw(
                        size = Size(size.height, size.height),
                        colorFilter = colorFilterFilled,
                    )
                }
            }
        }
        // Source
        drawRect(
            Color.Transparent,
            topLeft = Offset(endOfFilledItems, 0f),
            size = Size(rectWidth, height = size.height),
            blendMode = BlendMode.SrcIn,
        )

        for (i in 0 until itemCount) {
            translate(left = (imageWidth * i + space * i), top = 0f) {
                with(painterEmpty) {
                    draw(
                        size = Size(size.height, size.height),
                        colorFilter = ColorFilter.tint(
                            tintEmpty ?: Color.Transparent,
                            blendMode = BlendMode.SrcIn,
                        ),
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawWithLayer(block: DrawScope.() -> Unit) {
    with(drawContext.canvas.nativeCanvas) {
        val checkPoint = saveLayer(null, null)
        block()
        restoreToCount(checkPoint)
    }
}

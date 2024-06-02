package com.anupkunwar.nestedscrollingconnection

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

@Composable
fun App() {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        val height = 50.dp
        val inPixel = with(LocalDensity.current) { height.toPx() }

        val nestedScrollData = remember {
            nestedScrollingQuickReturn(heightInPixel = inPixel)
        }
        Box(
            modifier = Modifier
                .padding(it)
                .nestedScroll(nestedScrollData.nestedScrollConnection)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = height),
            ) {
                repeat(104) { id ->
                    item(key = id) {
                        Column {
                            Text(
                                text = "My item $id",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Divider()
                        }
                    }

                }
            }
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(x = 0, y = (nestedScrollData.state.value * inPixel).toInt())
                    }
                    .fillMaxWidth()
                    .height(height)
                    .background(Color.Gray)
            )
        }
    }
}

fun nestedScrollingQuickReturn(heightInPixel: Float): NestScrollingData {

    val yOffset = mutableFloatStateOf(0f)
    val floatOffset = derivedStateOf {
        yOffset.floatValue / heightInPixel
    }

    val nestedScrollingConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                yOffset.floatValue = (yOffset.floatValue + available.y).coerceIn(-heightInPixel, 0f)
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (consumed.y == 0f) {
                    yOffset.floatValue = 0f
                }
                return super.onPostScroll(consumed, available, source)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                return super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return super.onPostFling(consumed, available)
            }

        }

    return NestScrollingData(nestedScrollingConnection, floatOffset)

}

@Stable
class NestScrollingData(
    val nestedScrollConnection: NestedScrollConnection,
    val state: State<Float>
)
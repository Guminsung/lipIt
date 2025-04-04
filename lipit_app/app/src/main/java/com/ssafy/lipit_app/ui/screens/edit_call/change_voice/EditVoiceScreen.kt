package com.ssafy.lipit_app.ui.screens.edit_call.change_voice

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.my_voice.MyVoiceIntent

@Composable
fun EditVoiceScreen(
    state: EditVoiceState,
    onIntent: (EditVoiceIntent) -> Unit,
    onBack: () -> Unit,
    onClickAddVoice: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDF8FF))
            .padding(top = 44.dp, start = 20.dp, end = 20.dp),
    ) {

        BackHandler {
            onBack()
        }

        // 전체 제목
        Text(
            text = "My Voices",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),

                )
        )

        Spacer(modifier = Modifier.height(20.dp))

        //셀럽
        textTitle("Celebrity")
        Spacer(modifier = Modifier.height(21.dp))
        // 리스트
        VoiceItemList(false, onClickAddVoice = onClickAddVoice)

        Spacer(modifier = Modifier.height(50.dp))

        //커스텀
        textTitle("Custom")
        Spacer(modifier = Modifier.height(21.dp))
        //리스트
        VoiceItemList(true, onClickAddVoice = onClickAddVoice)

        Spacer(modifier = Modifier.height(50.dp))

    }
}

@Composable
fun textTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontSize = 20.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight(590),
            color = Color(0xFF222124),
        )
    )
}

@Composable
fun VoiceItemList(isCustom: Boolean, onClickAddVoice: () -> Unit) {
    val myVoiceList = listOf(
        VoiceList(
            voiceName = "Aiden Blackwood",
            voiceUrl = "https://picsum.photos/600/400",
            isOwned = true,
            isSelected = true,
            isCustom = false
        ),
        VoiceList(
            voiceName = "Luna Fairchildo",
            voiceUrl = "https://picsum.photos/600/400",
            isOwned = false,
            isSelected = false,
            isCustom = false
        ),
        VoiceList(
            voiceName = "Jasper Thornhill",
            voiceUrl = "https://picsum.photos/600/400",
            isOwned = true,
            isSelected = false,
            isCustom = true
        ),
        VoiceList(
            voiceName = "Ivy Whitmore",
            voiceUrl = "https://picsum.photos/600/400",
            isOwned = true,
            isSelected = false,
            isCustom = true
        )
    )

    // isCustom에 따라서 필터링
    val filteredList = myVoiceList.filter { it.isCustom == isCustom }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(clip = false)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            //modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(filteredList.size) { index ->
                val voice = filteredList[index]
                VoiceItem(
                    url = voice.voiceUrl,
                    name = voice.voiceName,
                    isOwned = voice.isOwned,
                    isSelected = voice.isSelected
                )
            }

            // Custom Voice 추가 부분
            if (isCustom) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .width(120.dp)
                                .height(120.dp)
                                .offset(y = (-10).dp, x = (-10).dp)
                                .graphicsLayer {
                                    clip = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.edit_voice_plus_icon),
                                contentDescription = "plus icon",
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(120.dp)
                                    .clip(CircleShape)
                                    .clickable{
                                        onClickAddVoice()
//                                        onIntent(MyVoiceIntent.NavigateToAddVoice)
//                                        navController.navigate("add_voice")
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(7.dp))

                        Text(
                            text = "",
                            style = TextStyle(
                                fontSize = 15.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight(590),
                                color = Color(0xFF000000),
                                textAlign = TextAlign.Center
                            )
                        )


                    }
                }
            }

        }
    }
}

@Composable
fun VoiceItem(url: String, name: String, isOwned: Boolean, isSelected: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.graphicsLayer(clip = false)
    ) {

        Box(
            modifier = Modifier
                .padding(start = 5.dp)
                .width(90.dp)
                .height(90.dp)
                .shadow(
                    elevation = if (isSelected) 10.dp else 0.dp,
                    shape = CircleShape,
                    ambientColor = Color(0xFF9C27B0),
                    spotColor = Color(0xFF9C27B0)
                )

                .background(
                    color = if (isSelected) Color(0x33D372FF) else Color.Transparent,
                    shape = CircleShape
                )
                .border(
                    width = if (isSelected) 4.dp else 0.dp,
                    color = Color(0xFFD372FF),
                    shape = CircleShape
                )
                .graphicsLayer {
                    clip = false // 그림자 잘리지 않게 설정!
                }
        ) {
            Image(
                painter = painterResource(
                    id = if (isOwned) R.drawable.profile_test_img
                    else R.drawable.profile_sample_locked
                ),
                contentDescription = "테스트 프로필",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )

        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = name,
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight(590),
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
            )
        )

    }
}


@Preview(showBackground = true)
@Composable
fun EditVoiceScreenPreview() {

    EditVoiceScreen(
        state = EditVoiceState(
            selectedVoiceName = "Harry Potter",
            selectedVoiceUrl = "https://picsum.photos/600/400",
//            celebrityVoices = sampleVoiceList,
//            myCustomVoices = sampleVoiceList,
        ),
        onIntent = {},
        onBack = {},
        onClickAddVoice = {}
    )

}
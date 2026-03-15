package org.coffeezip.auth

object NicknameGenerator {
    private val adjectives = listOf(
        "따뜻한", "사랑스러운", "귀여운", "포근한", "설레는",
        "맑은", "달콤한", "졸린", "행복한", "신나는",
        "용감한", "수줍은", "느긋한", "엉뚱한", "깜찍한",
        "다정한", "씩씩한", "반짝이는", "몽글몽글한", "두근두근한",
        "배고픈", "졸졸한", "뭉클한", "통통한", "포슬포슬한",
    )

    private val animals = listOf(
        "카피바라", "코모도도마뱀", "고양이", "토끼", "강아지",
        "펭귄", "수달", "너구리", "판다", "알파카",
        "라마", "미어캣", "오리", "해달", "다람쥐",
        "고슴도치", "미니돼지", "햄스터", "플라밍고", "기린",
        "코알라", "나무늘보", "오소리", "스컹크", "물범",
    )

    fun generate(): String {
        val adj = adjectives.random()
        val animal = animals.random()
        return "$adj $animal"
    }
}

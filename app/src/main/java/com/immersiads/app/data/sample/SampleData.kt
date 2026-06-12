package com.immersiads.app.data.sample

import com.immersiads.app.data.model.Advertisement
import com.immersiads.app.data.model.DifficultyLevel
import com.immersiads.app.data.model.Subtitle
import com.immersiads.app.data.model.VocabularyItem

object SampleData {

    val sampleSubtitlesEs = listOf(
        Subtitle(0, 2000, "¡Descubre el sabor auténtico!", "Discover the authentic taste!"),
        Subtitle(2000, 5000, "Hecho con ingredientes naturales", "Made with natural ingredients"),
        Subtitle(5000, 8000, "Pruébalo hoy mismo", "Try it today"),
        Subtitle(8000, 12000, "¡Lo mejor para tu familia!", "The best for your family!")
    )

    val sampleSubtitlesFr = listOf(
        Subtitle(0, 3000, "Bienvenue dans notre monde", "Welcome to our world"),
        Subtitle(3000, 6000, "La qualité avant tout", "Quality above all"),
        Subtitle(6000, 9000, "Profitez de chaque moment", "Enjoy every moment"),
        Subtitle(9000, 12000, "C'est la vie!", "That's life!")
    )

    val sampleSubtitlesDe = listOf(
        Subtitle(0, 2500, "Erleben Sie die Qualität", "Experience the quality"),
        Subtitle(2500, 5500, "Für die ganze Familie", "For the whole family"),
        Subtitle(5500, 8500, "Jetzt kaufen und sparen", "Buy now and save"),
        Subtitle(8500, 12000, "Weil Sie es verdienen!", "Because you deserve it!")
    )

    val sampleAdvertisements = listOf(
        Advertisement(
            id = "ad_001",
            title = "Sabor Auténtico - Bebida Natural",
            brand = "NaturAl",
            languageCode = "es",
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/FF6B35/FFFFFF?text=NaturAl",
            durationMs = 12000,
            subtitles = sampleSubtitlesEs,
            tags = listOf("food", "drinks", "family"),
            difficulty = DifficultyLevel.BEGINNER
        ),
        Advertisement(
            id = "ad_002",
            title = "La Qualité Française - Mode",
            brand = "LeBlanc",
            languageCode = "fr",
            videoUrl = "https://www.w3schools.com/html/movie.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/4A90D9/FFFFFF?text=LeBlanc",
            durationMs = 12000,
            subtitles = sampleSubtitlesFr,
            tags = listOf("fashion", "lifestyle"),
            difficulty = DifficultyLevel.INTERMEDIATE
        ),
        Advertisement(
            id = "ad_003",
            title = "Deutsche Qualität - Technik",
            brand = "TechWerk",
            languageCode = "de",
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/2ECC71/FFFFFF?text=TechWerk",
            durationMs = 12000,
            subtitles = sampleSubtitlesDe,
            tags = listOf("technology", "quality"),
            difficulty = DifficultyLevel.ADVANCED
        ),
        Advertisement(
            id = "ad_004",
            title = "Café del Pueblo - Artesanal",
            brand = "CaféArte",
            languageCode = "es",
            videoUrl = "https://www.w3schools.com/html/movie.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/8B4513/FFFFFF?text=CaféArte",
            durationMs = 10000,
            subtitles = listOf(
                Subtitle(0, 2500, "Cada taza es una historia", "Every cup is a story"),
                Subtitle(2500, 5500, "Cultivado con amor y tradición", "Grown with love and tradition"),
                Subtitle(5500, 10000, "El café que te hace sonreír", "The coffee that makes you smile")
            ),
            tags = listOf("coffee", "artisan", "culture"),
            difficulty = DifficultyLevel.BEGINNER
        )
    )

    val sampleVocabularyItems = listOf(
        VocabularyItem(
            word = "auténtico",
            translation = "authentic",
            languageCode = "es",
            context = "El sabor auténtico de España",
            adId = "ad_001"
        ),
        VocabularyItem(
            word = "ingredientes",
            translation = "ingredients",
            languageCode = "es",
            context = "Hecho con ingredientes naturales",
            adId = "ad_001"
        ),
        VocabularyItem(
            word = "qualité",
            translation = "quality",
            languageCode = "fr",
            context = "La qualité avant tout",
            adId = "ad_002"
        )
    )
}

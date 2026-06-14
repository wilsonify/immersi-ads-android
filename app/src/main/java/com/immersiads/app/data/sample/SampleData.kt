package com.immersiads.app.data.sample

import com.immersiads.app.data.model.Advertisement
import com.immersiads.app.data.model.DifficultyLevel
import com.immersiads.app.data.model.Subtitle

object SampleData {

    val sampleSubtitlesEn = listOf(
        Subtitle(0, 2500, "Discover the fresh taste of nature", "Descubre el sabor fresco de la naturaleza"),
        Subtitle(2500, 5500, "Made with real ingredients you can trust", "Hecho con ingredientes reales en los que puedes confiar"),
        Subtitle(5500, 8500, "Part of a balanced lifestyle", "Parte de un estilo de vida equilibrado"),
        Subtitle(8500, 12000, "Good for you. Good for the planet.", "Bueno para ti. Bueno para el planeta.")
    )

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

    val sampleSubtitlesIt = listOf(
        Subtitle(0, 2500, "Scopri il gusto autentico", "Discover the authentic taste"),
        Subtitle(2500, 5500, "Fatto con ingredienti genuini", "Made with genuine ingredients"),
        Subtitle(5500, 8500, "Tradizione e qualità italiana", "Italian tradition and quality"),
        Subtitle(8500, 12000, "Porta la dolce vita a casa tua", "Bring the sweet life to your home")
    )

    val sampleAdvertisements = listOf(
        Advertisement(
            id = "ad_001",
            title = "Pure Pulse - Natural Energy Drink",
            brand = "PurePulse",
            languageCode = "en",
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/FF6B35/FFFFFF?text=PurePulse",
            durationMs = 12000,
            subtitles = sampleSubtitlesEn,
            tags = listOf("food", "drinks", "lifestyle"),
            difficulty = DifficultyLevel.BEGINNER,
            license = "CC-BY 3.0",
            attribution = "Blender Foundation",
            attributionUrl = "https://peach.blender.org/"
        ),
        Advertisement(
            id = "ad_002",
            title = "Sabor Auténtico - Bebida Natural",
            brand = "NaturAl",
            languageCode = "es",
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/FF6B35/FFFFFF?text=NaturAl",
            durationMs = 12000,
            subtitles = sampleSubtitlesEs,
            tags = listOf("food", "drinks", "family"),
            difficulty = DifficultyLevel.BEGINNER,
            license = "CC-BY 3.0",
            attribution = "Blender Foundation",
            attributionUrl = "https://peach.blender.org/"
        ),
        Advertisement(
            id = "ad_003",
            title = "La Qualité Française - Mode",
            brand = "LeBlanc",
            languageCode = "fr",
            videoUrl = "https://www.w3schools.com/html/movie.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/4A90D9/FFFFFF?text=LeBlanc",
            durationMs = 12000,
            subtitles = sampleSubtitlesFr,
            tags = listOf("fashion", "lifestyle"),
            difficulty = DifficultyLevel.INTERMEDIATE,
            license = "CC-BY 3.0",
            attribution = "Blender Foundation",
            attributionUrl = "https://peach.blender.org/"
        ),
        Advertisement(
            id = "ad_004",
            title = "Deutsche Qualität - Technik",
            brand = "TechWerk",
            languageCode = "de",
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/2ECC71/FFFFFF?text=TechWerk",
            durationMs = 12000,
            subtitles = sampleSubtitlesDe,
            tags = listOf("technology", "quality"),
            difficulty = DifficultyLevel.ADVANCED,
            license = "CC-BY 3.0",
            attribution = "Blender Foundation",
            attributionUrl = "https://peach.blender.org/"
        ),
        Advertisement(
            id = "ad_005",
            title = "Dolce Vita - Olio d'Oliva",
            brand = "OlioVerde",
            languageCode = "it",
            videoUrl = "https://www.w3schools.com/html/movie.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/9ACD32/FFFFFF?text=OlioVerde",
            durationMs = 12000,
            subtitles = sampleSubtitlesIt,
            tags = listOf("food", "tradition", "italian"),
            difficulty = DifficultyLevel.BEGINNER,
            license = "CC-BY 3.0",
            attribution = "Blender Foundation",
            attributionUrl = "https://peach.blender.org/"
        ),
        Advertisement(
            id = "ad_006",
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
            difficulty = DifficultyLevel.BEGINNER,
            license = "CC-BY 3.0",
            attribution = "Blender Foundation",
            attributionUrl = "https://peach.blender.org/"
        ),
        Advertisement(
            id = "ad_007",
            title = "Green Globe - Eco Living",
            brand = "GreenGlobe",
            languageCode = "en",
            videoUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/27AE60/FFFFFF?text=GreenGlobe",
            durationMs = 12000,
            subtitles = listOf(
                Subtitle(0, 3000, "Small changes make a big impact", "Pequeños cambios generan gran impacto"),
                Subtitle(3000, 6000, "Reduce, reuse, recycle every day", "Reduce, reutiliza, recicla cada día"),
                Subtitle(6000, 9000, "Join the green revolution", "Únete a la revolución verde"),
                Subtitle(9000, 12000, "Together for a sustainable future", "Juntos por un futuro sostenible")
            ),
            tags = listOf("eco", "lifestyle", "sustainability"),
            difficulty = DifficultyLevel.INTERMEDIATE,
            license = "CC-BY 3.0",
            attribution = "Blender Foundation",
            attributionUrl = "https://peach.blender.org/"
        ),
        Advertisement(
            id = "ad_008",
            title = "Moda Milano - Fashion",
            brand = "ModaMilano",
            languageCode = "it",
            videoUrl = "https://www.w3schools.com/html/movie.mp4",
            thumbnailUrl = "https://via.placeholder.com/320x180/E91E63/FFFFFF?text=ModaMilano",
            durationMs = 12000,
            subtitles = listOf(
                Subtitle(0, 2500, "Eleganza senza tempo", "Timeless elegance"),
                Subtitle(2500, 5500, "Lo stile che ti distingue", "Style that sets you apart"),
                Subtitle(5500, 8500, "Fatto a mano in Italia", "Handmade in Italy"),
                Subtitle(8500, 12000, "La moda parla italiano", "Fashion speaks Italian")
            ),
            tags = listOf("fashion", "lifestyle"),
            difficulty = DifficultyLevel.ADVANCED,
            license = "CC-BY 3.0",
            attribution = "Blender Foundation",
            attributionUrl = "https://peach.blender.org/"
        )
    )

}

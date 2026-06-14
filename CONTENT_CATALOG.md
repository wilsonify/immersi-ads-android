# Content Catalog

## Overview

This catalog lists all advertisements available in the ImmersiAds demo app. Each entry pairs a video asset (licensed under Creative Commons) with custom subtitle tracks written for language-learning purposes. The subtitles are original content; the video assets are third-party works used under their respective licenses.

## Advertisement Entries

### ad_001 - Pure Pulse / Natural Energy Drink

| Field       | Value                                                 |
|-------------|-------------------------------------------------------|
| Language    | English (`en`)                                        |
| Brand       | PurePulse                                             |
| Difficulty  | Beginner                                              |
| Tags        | food, drinks, lifestyle                               |
| Duration    | 12 s                                                  |
| Video       | `mov_bbb.mp4` (Big Buck Bunny)                        |
| License     | CC-BY 3.0                                             |
| Attribution | Blender Foundation, https://peach.blender.org/        |

### ad_002 - Sabor Auténtico / Bebida Natural

| Field       | Value                                                 |
|-------------|-------------------------------------------------------|
| Language    | Spanish (`es`)                                        |
| Brand       | NaturAl                                               |
| Difficulty  | Beginner                                              |
| Tags        | food, drinks, family                                  |
| Duration    | 12 s                                                  |
| Video       | `mov_bbb.mp4` (Big Buck Bunny)                        |
| License     | CC-BY 3.0                                             |
| Attribution | Blender Foundation, https://peach.blender.org/        |

### ad_003 - La Qualité Française / Mode

| Field       | Value                                                 |
|-------------|-------------------------------------------------------|
| Language    | French (`fr`)                                         |
| Brand       | LeBlanc                                               |
| Difficulty  | Intermediate                                          |
| Tags        | fashion, lifestyle                                    |
| Duration    | 12 s                                                  |
| Video       | `movie.mp4` (Big Buck Bunny)                          |
| License     | CC-BY 3.0                                             |
| Attribution | Blender Foundation, https://peach.blender.org/        |

### ad_004 - Deutsche Qualität / Technik

| Field       | Value                                                 |
|-------------|-------------------------------------------------------|
| Language    | German (`de`)                                         |
| Brand       | TechWerk                                              |
| Difficulty  | Advanced                                              |
| Tags        | technology, quality                                   |
| Duration    | 12 s                                                  |
| Video       | `mov_bbb.mp4` (Big Buck Bunny)                        |
| License     | CC-BY 3.0                                             |
| Attribution | Blender Foundation, https://peach.blender.org/        |

### ad_005 - Dolce Vita / Olio d'Oliva

| Field       | Value                                                 |
|-------------|-------------------------------------------------------|
| Language    | Italian (`it`)                                        |
| Brand       | OlioVerde                                             |
| Difficulty  | Beginner                                              |
| Tags        | food, tradition, italian                              |
| Duration    | 12 s                                                  |
| Video       | `movie.mp4` (Big Buck Bunny)                          |
| License     | CC-BY 3.0                                             |
| Attribution | Blender Foundation, https://peach.blender.org/        |

### ad_006 - Café del Pueblo / Artesanal

| Field       | Value                                                 |
|-------------|-------------------------------------------------------|
| Language    | Spanish (`es`)                                        |
| Brand       | CaféArte                                              |
| Difficulty  | Beginner                                              |
| Tags        | coffee, artisan, culture                              |
| Duration    | 10 s                                                  |
| Video       | `movie.mp4` (Big Buck Bunny)                          |
| License     | CC-BY 3.0                                             |
| Attribution | Blender Foundation, https://peach.blender.org/        |

### ad_007 - Green Globe / Eco Living

| Field       | Value                                                 |
|-------------|-------------------------------------------------------|
| Language    | English (`en`)                                        |
| Brand       | GreenGlobe                                            |
| Difficulty  | Intermediate                                          |
| Tags        | eco, lifestyle, sustainability                        |
| Duration    | 12 s                                                  |
| Video       | `mov_bbb.mp4` (Big Buck Bunny)                        |
| License     | CC-BY 3.0                                             |
| Attribution | Blender Foundation, https://peach.blender.org/        |

### ad_008 - Moda Milano / Fashion

| Field       | Value                                                 |
|-------------|-------------------------------------------------------|
| Language    | Italian (`it`)                                        |
| Brand       | ModaMilano                                            |
| Difficulty  | Advanced                                              |
| Tags        | fashion, lifestyle                                    |
| Duration    | 12 s                                                  |
| Video       | `movie.mp4` (Big Buck Bunny)                          |
| License     | CC-BY 3.0                                             |
| Attribution | Blender Foundation, https://peach.blender.org/        |

## Language Coverage

| Language  | Code | Entries |
|-----------|------|---------|
| English   | en   | ad_001, ad_007 |
| Spanish   | es   | ad_002, ad_006 |
| French    | fr   | ad_003 |
| German    | de   | ad_004 |
| Italian   | it   | ad_005, ad_008 |

## Adding New Content

To add a new advertisement:

1. Choose a CC / public-domain video asset and add its license info to LICENSE-ASSETS.md.
2. Write subtitle tracks with native text + translation.
3. Create a new `Advertisement` entry in `SampleData.kt`.
4. Add a row to this catalog.

package coffee.app.core

/**
 * Enhanced coffee bean origin data – a curated list of famous coffee-growing regions.
 * This data can be safely displayed in the origin picker sheet without a remote network call.
 */
data class CoffeeOrigin(
    val id: String,
    val country: String,
    val region: String,
    val roaster: String,
    val roastDate: Long, // epoch millis – use format helper for display
)

object CoffeeOrigins {

    /** Static list of seed origins – includes country, region, and a representative roaster. */
    val seedOrigins = listOf(
        CoffeeOrigin(
            id = "ethiopia-yirgacheffe",
            country = "Ethiopia",
            region = "Yirgacheffe",
            roaster = "720 Coffee Roasters",
            roastDate = 1733568000000L, // approx 2025‑04‑01
        ),
        CoffeeOrigin(
            id = "colombia-supremo",
            country = "Colombia",
            region = "Huila",
            roaster = "Intelligentsia Coffee",
            roastDate = 1728528000000L, // approx 2024‑10‑08
        ),
        CoffeeOrigin(
            id = "kenya-symphony",
            country = "Kenya",
            region = "Nyeri",
            roaster = "Dead Earth Coffee",
            roastDate = 1710816000000L, // approx 2024‑03‑17
        ),
        CoffeeOrigin(
            id = "costa-rica-tierra",
            country = "Costa Rica",
            region = "Puria",
            roaster = "Four Barrel Coffee",
            roastDate = 1698528000000L, // approx 2023‑10‑29
        ),
        CoffeeOrigin(
            id = "ethiopia-harrar",
            country = "Ethiopia",
            region = "Harrar",
            roaster = "Blue Bottle Coffee",
            roastDate = 1684008000000L, // approx 2023‑05‑14
        ),
        CoffeeOrigin(
            id = "brazil-santaren",
            country = "Brazil",
            region = "Santarém",
            roaster = "Maverick Coffee",
            roastDate = 1676808000000L, // approx 2023‑02-19
        ),
        CoffeeOrigin(
            id = "vietnam-dalat",
            country = "Vietnam",
            region = "Đà Lạt",
            roaster = "Three Cups",
            roastDate = 1665648000000L, // approx 2022‑12-13
        ),
        CoffeeOrigin(
            id = "honduras-puter",
            country = "Honduras",
            region = "El Paraíso",
            roaster = "Verve Coffee Roasters",
            roastDate = 1655040000000L, // approx 2022‑08-11
        ),
        CoffeeOrigin(
            id = "sumatra-mandheling",
            country = "Indonesia",
            region = "North Sumatra",
            roaster = "Stumptown Coffee",
            roastDate = 1647048000000L, // approx 2022‑03‑12
        ),
        CoffeeOrigin(
            id = "mocha-yirgacheffe",
            country = "Yemen",
            region = "Mocha",
            roaster = "Camber Coffee",
            roastDate = 1638156000000L, // approx 2021‑11‑29
        ),
    )
}

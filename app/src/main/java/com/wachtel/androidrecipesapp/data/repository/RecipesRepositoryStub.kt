package com.wachtel.androidrecipesapp.data.repository

import com.wachtel.androidrecipesapp.data.model.CategoryDto
import com.wachtel.androidrecipesapp.data.model.IngredientDto
import com.wachtel.androidrecipesapp.data.model.RecipeDto

object RecipesRepositoryStub {

    private val categories = listOf(
        CategoryDto(
            id = 0,
            title = "Бургеры",
            description = "Рецепты всех популярных видов бургеров",
            imageUrl = "burger.png"
        ),
        CategoryDto(
            id = 1,
            title = "Десерты",
            description = "Самые вкусные рецепты десертов специально для вас",
            imageUrl = "dessert.png"
        ),
        CategoryDto(
            id = 2,
            title = "Пицца",
            description = "Пицца на любой вкус и цвет. Лучшая подборка для тебя",
            imageUrl = "pizza.png"
        ),
        CategoryDto(
            id = 3,
            title = "Рыба",
            description = "Печеная, жареная, сушеная, любая рыба на твой вкус",
            imageUrl = "fish.png"
        ),
        CategoryDto(
            id = 4,
            title = "Супы",
            description = "От классики до экзотики: мир в одной тарелке",
            imageUrl = "soup.png"
        ),
        CategoryDto(
            id = 5,
            title = "Салаты",
            description = "Хрустящий калейдоскоп под соусом вдохновения",
            imageUrl = "salad.png"
        )
    )

    private val burgerRecipes = listOf(
        RecipeDto(
            id = 0,
            title = "Классический бургер с говядиной",
            ingredients = listOf(
                IngredientDto("0.5", "кг", "говяжий фарш"),
                IngredientDto("1.0", "шт", "луковица, мелко нарезанная"),
                IngredientDto("2.0", "зубч", "чеснок, измельченный"),
                IngredientDto("4.0", "шт", "булочки для бургера"),
                IngredientDto("4.0", "шт", "листа салата"),
                IngredientDto("1.0", "шт", "помидор, нарезанный кольцами"),
                IngredientDto("2.0", "ст. л.", "горчица"),
                IngredientDto("2.0", "ст. л.", "кетчуп"),
                IngredientDto("по вкусу", "", "соль и черный перец")
            ),
            method = listOf(
                "1. В глубокой миске смешайте говяжий фарш, лук, чеснок, соль и перец. Разделите фарш на 4 равные части и сформируйте котлеты.",
                "2. Разогрейте сковороду на среднем огне. Обжаривайте котлеты с каждой стороны в течение 4-5 минут или до желаемой степени прожарки.",
                "3. В то время как котлеты готовятся, подготовьте булочки. Разрежьте их пополам и обжарьте на сковороде до золотистой корочки.",
                "4. Смазать нижние половинки булочек горчицей и кетчупом, затем положите лист салата, котлету, кольца помидора и закройте верхней половинкой булочки.",
                "5. Подавайте бургеры горячими с картофельными чипсами или картофельным пюре."
            ),
            imageUrl = "burger-hamburger.png"
        ),
        RecipeDto(
            id = 1,
            title = "Чизбургер с беконом",
            ingredients = listOf(
                IngredientDto("0.4", "кг", "говяжий фарш"),
                IngredientDto("4.0", "шт", "ломтика бекона"),
                IngredientDto("4.0", "шт", "ломтика сыра чеддер"),
                IngredientDto("4.0", "шт", "булочки для бургера"),
                IngredientDto("1.0", "шт", "помидор, нарезанный"),
                IngredientDto("по вкусу", "", "майонез и кетчуп")
            ),
            method = listOf(
                "1. Обжарьте бекон на сковороде до хрустящей корочки, отложите на бумажное полотенце.",
                "2. Сформируйте из фарша 4 котлеты, обжарьте с каждой стороны по 4 минуты.",
                "3. За минуту до готовности положите на каждую котлету по ломтику сыра, чтобы он расплавился.",
                "4. Соберите бургер: булочка, майонез, котлета с сыром, бекон, помидор, кетчуп.",
                "5. Подавайте горячими."
            ),
            imageUrl = "burger-cheeseburger.png"
        )
    )

    private val dessertRecipes = listOf(
        RecipeDto(
            id = 100,
            title = "Шоколадный мусс",
            ingredients = listOf(
                IngredientDto("200", "г", "темный шоколад"),
                IngredientDto("300", "мл", "сливки")
            ),
            method = listOf(
                "1. Растопите шоколад.",
                "2. Смешайте со сливками.",
                "3. Охладите перед подачей."
            ),
            imageUrl = "dessert-mousse.png"
        )
    )

    private val pizzaRecipes = listOf(
        RecipeDto(
            id = 200,
            title = "Маргарита",
            ingredients = listOf(
                IngredientDto("1", "шт", "основа для пиццы"),
                IngredientDto("150", "г", "моцарелла")
            ),
            method = listOf(
                "1. Подготовьте основу.",
                "2. Добавьте начинку.",
                "3. Выпекайте до готовности."
            ),
            imageUrl = "pizza-margherita.png"
        )
    )

    private val soupRecipes = listOf(
        RecipeDto(
            id = 300,
            title = "Томатный суп",
            ingredients = listOf(
                IngredientDto("500", "г", "помидоры"),
                IngredientDto("1", "шт", "лук")
            ),
            method = listOf(
                "1. Подготовьте овощи.",
                "2. Варите до готовности.",
                "3. Измельчите и подавайте."
            ),
            imageUrl = "soup-tomato.png"
        )
    )

    private val fishRecipes = listOf(
        RecipeDto(
            id = 400,
            title = "Запеченная рыба с лимоном",
            ingredients = listOf(
                IngredientDto("1", "шт", "рыба"),
                IngredientDto("1", "шт", "лимон"),
                IngredientDto("2", "ст. л.", "оливковое масло")
            ),
            method = listOf(
                "1. Очистите и подготовьте рыбу.",
                "2. Добавьте лимон и масло.",
                "3. Запекайте до готовности."
            ),
            imageUrl = "fish.png"
        )
    )

    private val saladRecipes = listOf(
        RecipeDto(
            id = 500,
            title = "Греческий салат",
            ingredients = listOf(
                IngredientDto("2", "шт", "помидоры"),
                IngredientDto("1", "шт", "огурец"),
                IngredientDto("150", "г", "сыр фета")
            ),
            method = listOf(
                "1. Нарежьте овощи.",
                "2. Добавьте сыр.",
                "3. Перемешайте и подавайте."
            ),
            imageUrl = "salad.png"
        )
    )

    private val allRecipes by lazy {
        burgerRecipes +
                dessertRecipes +
                pizzaRecipes +
                fishRecipes +
                soupRecipes +
                saladRecipes
    }

    fun getCategories(): List<CategoryDto> {
        return categories
    }

    fun getRecipesByCategoryId(categoryId: Int): List<RecipeDto> {
        return when (categoryId) {
            0 -> burgerRecipes
            1 -> dessertRecipes
            2 -> pizzaRecipes
            3 -> fishRecipes
            4 -> soupRecipes
            5 -> saladRecipes
            else -> emptyList()
        }
    }

    fun getRecipeById(recipeId: Int): RecipeDto? {
        return allRecipes.find { it.id == recipeId }
    }
}
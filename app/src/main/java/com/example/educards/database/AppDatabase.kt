package com.example.educards.database

import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Database(
    entities = [Card::class, Stats::class],
    version = 6
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun statsDao(): StatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS stats (" +
                            "date TEXT PRIMARY KEY NOT NULL, " +
                            "cards_solved INTEGER NOT NULL DEFAULT 0)"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE cards ADD COLUMN rating INTEGER NOT NULL DEFAULT 0"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE cards ADD COLUMN eFactor REAL NOT NULL DEFAULT 2.5"
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE cards ADD COLUMN nextReviewDate INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}"
                )
                database.execSQL(
                    "ALTER TABLE cards ADD COLUMN intervalStep INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE cards ADD COLUMN currentInterval INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Удаление столбца intervalStep
                database.execSQL("""
                    CREATE TABLE new_cards (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        question TEXT NOT NULL,
                        answer TEXT NOT NULL,
                        rating INTEGER NOT NULL DEFAULT 0,
                        eFactor REAL NOT NULL DEFAULT 2.5,
                        nextReviewDate INTEGER NOT NULL,
                        currentInterval INTEGER NOT NULL DEFAULT 0
                    )
                """)

                database.execSQL("""
                    INSERT INTO new_cards 
                    (id, question, answer, rating, eFactor, nextReviewDate, currentInterval)
                    SELECT 
                    id, question, answer, rating, eFactor, nextReviewDate, currentInterval 
                    FROM cards
                """)

                database.execSQL("DROP TABLE cards")
                database.execSQL("ALTER TABLE new_cards RENAME TO cards")
            }
        }
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cards_database"
                )
                    .fallbackToDestructiveMigration() // Важно для переустановки
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            GlobalScope.launch(Dispatchers.IO) {
                                val cards = getDefaultCards(context)
                                getDatabase(context).cardDao().insertAll(cards)
                                Log.d("DB", "Добавлено ${cards.size} карточек")
                            }
                        }
                    })
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
        private fun getDefaultCards(context: Context): List<Card> {
            return listOf(
                // Лекция 1
                Card(
                    question = "Что такое вектор?",
                    answer = "Направленный отрезок, характеризующийся величиной и направлением",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Какие бывают виды матриц?",
                    answer = "Квадратные, прямоугольные, диагональные, единичные, нулевые",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Что такое определитель матрицы?",
                    answer = "Скалярная величина, вычисляемая для квадратных матриц",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Как найти обратную матрицу?",
                    answer = "Через алгебраические дополнения или методом Гаусса",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Что такое линейная зависимость векторов?",
                    answer = "Когда один вектор можно выразить через другие",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),

                // Лекция 2
                Card(
                    question = "Как умножить матрицы?",
                    answer = "Строка на столбец, суммируя произведения элементов",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Что такое транспонированная матрица?",
                    answer = "Матрица с заменой строк на столбцы",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Какие свойства у умножения матриц?",
                    answer = "Ассоциативность, дистрибутивность, некоммутативность",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Как вычислить след матрицы?",
                    answer = "Сумма элементов главной диагонали",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Что такое блочная матрица?",
                    answer = "Матрица, разделённая на подматрицы-блоки",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),

                // Лекция 3
                Card(
                    question = "Что такое СЛАУ?",
                    answer = "Система линейных алгебраических уравнений",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Какие методы решения СЛАУ?",
                    answer = "Крамера, Гаусса, матричный, итерационные",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Когда СЛАУ имеет решение?",
                    answer = "Когда ранг матрицы равен рангу расширенной матрицы",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Что такое фундаментальная система решений?",
                    answer = "Базис пространства решений однородной СЛАУ",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Как определить совместность системы?",
                    answer = "По теореме Кронекера-Капелли",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Что называется однородной СЛАУ?",
                    answer = "Система вида Ax = 0, где A - матрица коэффициентов, 0 - нулевой вектор",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Как связаны размерность пространства решений и ранг матрицы?",
                    answer = "n - r, где n - число переменных, r - ранг матрицы",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Что такое базисный минор матрицы?",
                    answer = "Любой отличный от нуля минор максимального порядка",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "В чем суть метода Гаусса?",
                    answer = "Приведение системы к ступенчатому виду с помощью элементарных преобразований",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                ),
                Card(
                    question = "Что характеризует определитель матрицы в методе Крамера?",
                    answer = "Определитель основной матрицы системы",
                    nextReviewDate = System.currentTimeMillis(),
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    isBuiltIn = true
                )
            ).map {
                it.copy(
                    rating = 0,
                    eFactor = 2.5,
                    currentInterval = 0,
                    nextReviewDate = System.currentTimeMillis()
                )
            }
        }
    }
}

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.async
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

data class News(
    val title: String,
    val category: String
)

val readNewsCount = MutableStateFlow(0)
fun newsFlow(): Flow<News> = flow {
    val newsList = listOf(
        News("AI semakin berkembang", "Teknologi"),
        News("Timnas Indonesia meraih kemenangan", "Olahraga"),
        News("Harga saham mengalami perubahan", "Ekonomi"),
        News("Perkembangan teknologi terbaru", "Teknologi"),
        News("Pertandingan sepak bola berlangsung seru", "Olahraga")
    )

    for (news in newsList) {
        emit(news)
        delay(2000)
    }
}

suspend fun fetchNewsDetails(news: String): String {
    delay(1000)
    return "Detail berita: $news"
}

fun main() = runBlocking {
    println("=== NEWS FEED SIMULATOR ===")

    newsFlow()
        .filter { news -> news.category == "Teknologi" }
        .map { news -> "[${news.category}] ${news.title}" }
        .catch { e ->
            println("Terjadi kesalahan: ${e.message}")
        }
        .collect { news ->
            readNewsCount.value += 1

            val detail = async(Dispatchers.Default) {
                fetchNewsDetails(news)
            }

            println(news)
            println(detail.await())
            println("Jumlah berita dibaca: ${readNewsCount.value}")
            println()
        }
}
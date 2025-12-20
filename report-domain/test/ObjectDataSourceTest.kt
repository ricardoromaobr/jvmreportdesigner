import myreport.model.data.ObjectDataSource
import kotlin.test.Test


class Person(val name: String, val age: Int)

class ObjectDataSourceTest {

    val people = listOf(Person("Alice", 29), Person("Bob", 31))

    val objectDataSource = ObjectDataSource(people)

    @Test
    fun `Discover fields, fields create, fields number is greater than zero`() {

        assert(objectDataSource.discoverFields().size > 0)
    }

    @Test
    fun getValue_value_IsNotNullOrEmpty() {
        assert(!objectDataSource.getValue("name", "").isNullOrEmpty())
    }
}
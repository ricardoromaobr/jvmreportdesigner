import myreport.model.data.ObjectDataSource
import kotlin.test.Test
import kotlin.test.assertFalse


class Person(val name: String, val age: Int)

class ObjectDataSourceTest {

    val people = listOf(Person("Alice", 29), Person("Bob", 31))

    val objectDataSource = ObjectDataSource(people)
    val emptyPerson = listOf<Person>()
    val emptySource = ObjectDataSource(emptyPerson)

    @Test
    fun `Discover fields, fields create, fields number is greater than zero`() {
        assert(objectDataSource.discoverFields().size > 0)
    }

    @Test
    fun getValue_value_IsNotNullOrEmpty() {
        assert(!objectDataSource.getValue("name", "").isNullOrEmpty())
    }

    @Test
    fun nextRexord() {

        if (objectDataSource.hasNext)
            objectDataSource.moveNext()
        println(objectDataSource.getValue("name", ""))
    }

    @Test
    fun currentRexord() {
        if (objectDataSource.current != null) {
            val person = objectDataSource.current!! as Person
            println(person.name)
        }

    }

    @Test
    fun hasNext_emptySource_IsFalse() = assertFalse(emptySource.hasNext)

}
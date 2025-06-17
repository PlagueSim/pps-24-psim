import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExampleTest extends AnyFlatSpec with Matchers {

  "An example test" should "pass" in {
    val result = 1 + 1
    result shouldEqual 2
  }

  it should "fail" in {
    val result = 1 + 1
    result should not equal 3
  }

}

package io.scalaland.chimney

import utest._
import io.scalaland.chimney.dsl._

object PatcherSpec extends TestSuite {

  val tests = Tests {

    "patch simple objects" - {

      case class Foo(a: Int, b: String, c: Double)
      case class Bar(c: Double, a: Int)

      val foo = Foo(0, "", 0.0)
      val bar = Bar(10.0, 10)

      foo.patchWith(bar) ==>
        Foo(10, "", 10.0)
    }

    "patch objects with value classes in patch" - {

      import TestDomain._

      val update = UpdateDetails("xyz@def.com", 123123123L)

      exampleUser.patchWith(update) ==>
        User(10, Email("xyz@def.com"), Phone(123123123L))
    }

    "support optional types in patch" - {

      import TestDomain._

      case class UserPatch(email: Option[String], phone: Option[Phone])

      val update = UserPatch(email = Some("updated@example.com"), phone = None)

      exampleUser.patchWith(update) ==>
        User(10, Email("updated@example.com"), Phone(1234567890L))
    }

    "support mixed optional and regular types" - {

      import TestDomain._

      case class UserPatch(email: String, phone: Option[Phone])
      val update = UserPatch(email = "updated@example.com", phone = None)

      exampleUser.patchWith(update) ==>
        User(10, Email("updated@example.com"), Phone(1234567890L))
    }

    "optional fields in the patched object overwritten by None" - {

      import TestDomain._

      case class UserPatch(email: String, phone: Option[Phone])
      val update = UserPatch(email = "updated@example.com", phone = None)

      exampleUserWithOptionalField.patchWith(update) ==>
        UserWithOptionalField(10, Email("updated@example.com"), None)
    }

    "fields of type Option[T] in the patched object not overwritten by None of type Option[Option[T]]" - {

      import TestDomain._

      case class UserWithOptional(id: Int, email: Email, phone: Option[Phone])

      case class UserPatch(email: String, phone: Option[Option[Phone]])
      val update = UserPatch(email = "updated@example.com", phone = None)

      exampleUserWithOptionalField.patchWith(update) ==>
        UserWithOptionalField(10, Email("updated@example.com"), Some(Phone(1234567890L)))
    }
  }

}

object TestDomain {

  case class Email(address: String) extends AnyVal
  case class Phone(number: Long) extends AnyVal

  case class User(id: Int, email: Email, phone: Phone)
  case class UpdateDetails(email: String, phone: Long)

  case class UserWithOptionalField(id: Int, email: Email, phone: Option[Phone])

  val exampleUser = User(10, Email("abc@def.com"), Phone(1234567890L))
  val exampleUserWithOptionalField = UserWithOptionalField(10, Email("abc@def.com"), Option(Phone(1234567890L)))

}

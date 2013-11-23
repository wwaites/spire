package spire.math

import spire.implicits._

import org.scalatest.matchers.ShouldMatchers
import org.scalacheck.Arbitrary._
import org.scalatest._
import prop._

import org.scalacheck._
import Gen._
import Arbitrary.arbitrary

class CRCheck extends PropSpec with ShouldMatchers with GeneratorDrivenPropertyChecks {

  implicit val smallRational = Arbitrary(for {
    n <- arbitrary[Long]
    d <- arbitrary[Long].filter(_ != 0)
  } yield {
    Rational(n, d)
  })

  val biggerRational = Arbitrary(for {
    n <- arbitrary[BigInt]
    d <- arbitrary[BigInt].filter(_ != 0)
  } yield {
    Rational(n, d)
  })

  implicit val arbitraryCR =
    Arbitrary(for { r <- arbitrary[Rational] } yield CR(r))

  property("x + 0 = x") {
    forAll { (x0: Rational) =>
      val x = CR(x0)
      x + CR.zero should be === x
    }
  }
  
  property("x * 0 = 0") {
    forAll { (x0: Rational) =>
      val x = CR(x0)
      x * CR.zero should be === CR.zero
    }
  }
  
  property("x * 1 = x") {
    forAll { (x0: Rational) =>
      val x = CR(x0)
      x + CR.zero should be === x
    }
  }
  
  property("x + y = y + x") {
    forAll { (x0: Rational, y0: Rational) =>
      val (x, y) = (CR(x0), CR(y0))
      x + y should be === y + x
    }
  }
  
  property("x + (-x) = 0") {
    forAll { (x0: Rational) =>
      val x = CR(x0)
      x + (-x) should be === CR.zero
    }
  }

  property("x / x = 1") {
    forAll { (x0: Rational) =>
      if (x0 != 0) {
        val x = CR(x0)
        x / x should be === CR.one
      }
    }
  }
  
  property("x * y = y * x") {
    forAll { (x0: Rational, y0: Rational) =>
      val (x, y) = (CR(x0), CR(y0))
      x * y should be === y * x
    }
  }
  
  property("x + x = 2x") {
    forAll { (x0: Rational) =>
      val x = CR(x0)
      x + x should be === x * CR(2)
    }
  }

  property("x * (y + z) = xy + xz") {
    forAll { (x0: Rational, y0: Rational, z0: Rational) =>
      val (x, y, z) = (CR(x0), CR(y0), CR(z0))
      x * (y + z) should be === x * y + x * z
    }
  }

  property("x.pow(k).nroot(k) = x") {
    forAll { (x0: Rational, k0: Byte) =>
      val x = CR(x0.abs)
      val k = (k0 & 0xff) % 10 + 1
      x.pow(k).nroot(k) should be === x
    }
  }

  property("x.nroot(k).pow(k) = x") {
    forAll { (x0: Rational, k0: Byte) =>
      val x = CR(x0.abs)
      val k = (k0 & 0xff) % 10 + 1
      x.nroot(k).pow(k) should be === x
    }
  }

  property("x.pow(j).nroot(k) = x.fpow(j/k)") {
    forAll { (x0: Int, j0: Byte, k0: Byte) =>
      if (x0 > 0) {
        val x = CR(x0)
        val j = (j0 & 0xff) % 10 + 1
        val k = (k0 & 0xff) % 10 + 1
        x.pow(j).nroot(k) should be === x.fpow(Rational(j, k))
      }
    }
  }
}
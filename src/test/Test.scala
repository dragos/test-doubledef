package test

import scala.tools.nsc.interactive.tests.InteractiveTest
import scala.tools.nsc.interactive._
import scala.collection.mutable.ListBuffer
import scala.tools.nsc.Settings
import scala.reflect.internal.util.SourceFile

object Test extends InteractiveTest {
  import compiler.{ Symbol, Tree }

  protected override val sourceDir = "test-project"

  override def prepareSettings(settings: Settings) {
    settings.YpresentationDebug.value = true
    settings.YpresentationVerbose.value = true
  }
    
  def collectParsedEnteredTrees(): Seq[Tree] = {
    for (source <- sourceFiles.toList) yield {
      val r = ask { response: Response[Tree] =>
        compiler.askStructure(false)(source, response)
      }

      r.get.left.toOption.get
    }
  }

  def allSymbols(trees: Seq[Tree]): Seq[Symbol] = {
    import compiler._

    trees flatMap { root =>
      val buf = ListBuffer[Symbol]()
      for (tree <- root) tree match {
        case t: DefTree if (t.symbol ne null) && (t.symbol ne NoSymbol) => 
          buf += t.symbol
        case _ =>
      }
      buf.toSeq
    }
  }

  def slowDown() {
    compiler.ask( () => Thread.sleep(150))
  }
  
  def forceSymbols() {
    askReload(sourceFiles)
    slowDown()
    val allSyms = allSymbols(collectParsedEnteredTrees)
//    allSyms.permutations
    for (sym <- allSyms) {
      println("forcing symbol: " + sym.fullName)
      compiler.ask( () => sym.initialize )
    }

    println("\n\nAND NOW, THE ERRORS: \n\n")
    for (file <- sourceFiles) {
      askLoadedTyped(file).get
      compiler.getUnitOf(file).get.problems foreach (Console.err.println)
    }
  }
  
  override def main(args: Array[String]) {
//    loadSources()
    slowDown()
    forceSymbols()
    forceSymbols()
  }
}
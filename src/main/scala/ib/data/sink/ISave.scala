package ib.data.sink

import java.io.{FileOutputStream, PrintStream, File}

import scala.io.Source

/**
  * Created by qili on 22/11/2015.
  */
trait ISave[T] {
  def save(data: T): Boolean

  def close
}


class FileSaver[T](file: String, saveCheck: (String, T) => Boolean) extends ISave[T] {
  val outputFile = new File(file)
  if (!outputFile.exists()) {
    outputFile.createNewFile()
  }
  val fos = new PrintStream(new FileOutputStream(outputFile))

  override def save(data: T): Boolean = {
    if (saveCheck(file, data)) {
      fos.append(data.toString)
      fos.append("\n")
      true
    } else false
  }

  override def close: Unit = fos.close()
}


object FileUtil {
  def lastLine(file: String): String = {
    Source.fromFile(file).getLines().toSeq.last
  }
}
package ib.data.sink

import java.io.{FileOutputStream, PrintStream, File}
import java.util.Date

import ib.util.DateUtil
import org.joda.time.DateTime

import scala.io.Source

/**
  * Created by qili on 22/11/2015.
  */
trait ISave[T] {

  def updateToday(ticker: String): Boolean

  def save(data: T): Boolean

  def saveAll(data: Seq[T]): Int

  def close
}


class FileSaver[T](file: String, saveCheck: (String, T) => Boolean) extends ISave[T] {
  val outputFile = new File(file)
  if (!outputFile.exists()) {
    outputFile.createNewFile()
  }

  override def updateToday(ticker: String): Boolean = FileUtil.updatedToday(file)

  val fos = new PrintStream(new FileOutputStream(outputFile, true))

  override def save(data: T): Boolean = {
    if (saveCheck(file, data)) {
      fos.append(data.toString)
      fos.append("\n")
      true
    } else false
  }

  override def saveAll(data: Seq[T]) = data.map(d => save(d)).count(_ == true)

  override def close: Unit = fos.close()
}


object FileUtil {
  def lastLine(file: String): Option[String] = {
    Source.fromFile(file).getLines().toSeq.lastOption
  }

  def updatedToday(file: String) = {
    val f = new File(file)
    val today = DateUtil.DATE.format(DateTime.now().toDate)

    f.exists() && {
      val lastUpdate = DateUtil.DATE.format(new Date(f.lastModified()))
      lastUpdate >= today
    }
  }
}
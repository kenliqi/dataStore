package ib.algo

import scala.collection.mutable

/**
  * Created by qili on 27/09/2015.
  */
case class Car(x: Double, y: Double, v: Double)

case class maxV(start: Double, end: Double, v: Double, startTime: Double)

//recording the max volecity allowed between start and end
object Highway {

  def run(cars: Seq[Car]): Double = {
    val icars = cars.sortBy(_.x).reverse //O(nlogn)
    var max = 0.0
    val stack = new mutable.Stack[maxV]
    //    stack.push(maxV(0, Double.MaxValue, Double.MaxValue))
    for (car <- icars) {
      //we can use v to run from x to xj
      val tmp = new mutable.Stack[maxV]
      if (stack.isEmpty) {
        stack.push(maxV(car.x, car.y, car.v, 0))
        max = Math.max(max, (car.y - car.x) / car.v)
      } else {
        var cur: Double = car.x
        var time: Double = 0
        while (!stack.isEmpty) {
          var aheadCar = stack.pop()
          if (car.v <= aheadCar.v) {
            if (car.y <= aheadCar.end) {
              //break out here
              tmp.push(maxV(cur, car.y, car.v, time)) //slow car update status and leave
              val curCarLeaveTime = time + (car.y - cur) / car.v
              max = Math.max(max, curCarLeaveTime)
              tmp.push(maxV(car.y, aheadCar.end, aheadCar.v, aheadCar.startTime + (car.y - aheadCar.start) / aheadCar.v)) //aheadCar update
              while (!stack.isEmpty) tmp.push(stack.pop())
            } else {
              //the ahead car will be blocked by the current slow car
              val runTime = (aheadCar.end - cur) / car.v //We use the current car slow speed to run the ahead car period
              time = time + runTime
              cur = aheadCar.end //The slow car runs to the end of period
            }
          } else {
            val minEnd = Math.min(car.y, aheadCar.end)
            //catch up!
            if (car.y > aheadCar.start && ((minEnd - aheadCar.start) / aheadCar.v + aheadCar.startTime) > ((minEnd - cur) / car.v + time)) {
              val catchUpTime = (aheadCar.start - cur) / (car.v - aheadCar.v)
              val catchUpPoint = aheadCar.start + aheadCar.v * catchUpTime
              if (aheadCar.end < car.y) {
                tmp.push(maxV(cur, catchUpPoint, car.v, time)) //before catchup, although current car is faster, but is still left behind
                //update time and position
                time = time + (catchUpPoint - cur) / car.v
                cur = catchUpPoint
                tmp.push(maxV(catchUpPoint, aheadCar.end, aheadCar.v, time)) //Use ahead slow car as block after catching up
                //update time and position
                time = time + (aheadCar.end - cur) / aheadCar.v
                cur = aheadCar.end
              } else {
                tmp.push(maxV(cur, catchUpPoint, car.v, time))
                time = time + (catchUpPoint - cur) / car.v
                cur = catchUpPoint
                max = Math.max(max, (car.y - cur) / aheadCar.v + time)
                tmp.push(maxV(catchUpPoint, aheadCar.end, aheadCar.v, time))
                while (!stack.isEmpty) tmp.push(stack.pop())
              }
            } else {
              //No catch up
              if (car.y <= aheadCar.end) {
                tmp.push(maxV(car.x, car.y, car.v, time))
                tmp.push(maxV(car.y, aheadCar.end, aheadCar.v, aheadCar.startTime + (car.y - aheadCar.start) / aheadCar.v))
                while (!stack.isEmpty) tmp.push(stack.pop())
              } else {
                time = time + (aheadCar.end - cur) / car.v
                cur = aheadCar.end
              }
            }
          }
        }

        while (!tmp.isEmpty) {
          stack.push(tmp.pop())
        }

      }
    }
    max
  }
}

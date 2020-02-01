package com.gy.exercise

import scala.math._
/**
  *
  *
  * @author :  fanbingjie 
  * @email :  2395590449@qq.com
  * @version : 1.0
  * @since : 2020-01-28 / 11:54
  **
  *        快捷键 :
  *        执行 :  		alt + r   			提示补全 : 		alt+/
  *        向下移动行	alt +down		万能解错/ 生成返回值变量	alt + Enter
  *        向上移动行	alt + up			退回前一个编辑页面		alt + left
  *        进入到下一个编辑页面	alt + right
  **
  *        单行注释 : 	Ctrl + /			复制代码			Ctrl + c
  *        全选		Ctrl   + a			删除一行或删除选中行    	Ctrl + d
  *        剪切		Ctrl   + x			关闭当前打开的代码栏               ctrl + w
  *        保存		Ctrl  + s			查看类的结构，类似于eclipse的 outline   Ctrl + o
  *        粘贴		Ctrl  +  v			撤销			Ctrl  + z
  *        反撤销		Ctrl  + y			打开最近修改的文件		Ctrl + E
  *        查找/替换		Ctrl +f			查找(全局)			Ctrl + h 
  **
  *
  *        向下开始新的一行	shift + Enter
  *        如何查看原码	                Ctrl + 点击类  或   Ctrl +shift + t  (输入)
  *        向上开始新的一行		Ctl + shift + Enter   	
  *        快速搜索类中的错误                  Ctrl + shift + q	选择要粘贴的内容		          Ctrl + shift + v
  *        查找方法在哪里被调用	Ctrl + shift + h	关闭当前打开的所有代码栏	          Ctrl + shift + w
  *        抽取方法			Ctrl + shift + m	查看类的继承结构图		          Ctrl + shift + u
  *        打开代码所在硬盘文件夹	Ctrl + shift + x	多行注释:		 	          Ctrl + shift + /	
  *        格式化代码		Ctrl + shift + F	大写转小写/ 小写转大写	          Ctrl + shift + y	
  **
  *        查看方法的多层重写结构           Ctrl + alt + h	添加到收藏		          Ctrl + alt + f	
  *        提示方法的参数类型 		Ctrl +alt 	+ /	向下复制一行		          Ctrl + alt + down
  **
  *        收起所有方法		alt + shift + c	重构；修改变量名与方法名 (rename)   alt + shift + r
  *        打开所有方法		alt + shift + x	生成构造/get/set/toString	          alt  + shift + s		
  *        生成try-catch 等              	alt + shift + z	局部变量抽成成员变量	          alt + shift + f
  **
  *        选中数行，整体往后移动   	tab		选中数行，整体往前移动   	          shift + tab
  **
  *        查看继承关系		F4		查看文档说明		          F2
  *        查找文件			double shift
  *
  *
  *
  */

object Workone{

  def main(args: Array[String]): Unit = {
      val tuple = (1,2)
      val tuple1 = swap(tuple)
      println(tuple1)


      val array = Array[Any](1,2,3,4,5,6,87)
      val array2 = swapArrayPre2(array)
      println("array2 :" + array2.toBuffer )

      val list = List(Some(1),Some(2),None,Some(3) ,None)
      val s = sum(list)
      println( s )

    val h = compose(f, g)
    println( h(2) )

    val newList = values( (x) => x * x , 2,4)
    println(newList)

    val arr = Array(1,2,4,5,6,3,67,7,8,56,9)
    val max = arr.reduceLeft(f1)
    println( "最大值为:" + max)


    //测试阶乘
    println(factorial(3))

    //求最大值
    println(largest( x => 10 * x - x * x , 1 to 10) )
  }

  def largest( fun : (Int) => Int , inputs : Seq[Int] ) = {
    inputs.map(fun(_)).max
  }

  def factorial( n : Int ) : Int = {
    1 to n reduceLeft( _ * _ )
  }

  def compose(f : Double=>Option[Double] , g : Double=>Option[Double]): Double=>Option[Double] ={

    //返回一个匿名函数
    (x :Double) =>{
      //合并
      //1. 如果其中一个函数返回 None ,则组合函数也应返回None
      if( f(x) == None || g(x) == None)
        None
      else
        g(x)
    }
  }

  def f(x : Double) = if ( x >= 0) Some(sqrt(x)) else None

  def g(x : Double) = if ( x != 1) Some( 1 / ( x - 1)) else None

  def swap[S,T](tup : (S,T))={
    tup match{
      case (x , y ) => ( y , x )
      case _ => println("没有匹配到...")
    }
  }

  def swapArrayPre2(array : Array[Any])={
      array match{
        case Array(first ,second , rest @_* ) => Array(second ,first ) ++ rest
        case _ =>  array
      }
  }

  def sum(lst : List[Option[Int]]) = lst.map(_.getOrElse(0)).sum


  def  values( fun : (Int) => Int , low : Int , high : Int) ={   //函数的形式
    var newList = List[(Int , Int)]()     //空集合 ，准备放我们的对偶
    (low to high) foreach{   //遍历（low to high）
      x => newList = ( x , fun( x )) :: newList
    }
    newList
  }

  def f1( l : Int , r : Int) : Int = {	// f1 求出最大值
    if( l > r ) l else r
  }
}


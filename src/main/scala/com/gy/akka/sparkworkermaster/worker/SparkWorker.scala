package com.gy.akka.sparkworkermaster.worker

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.gy.akka.sparkworkermaster.common.{HeartBeat, RegisterWorkerInfo, RegisteredWorkerInfo, SendHeartBeat}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.control.TailCalls.Done

/**
  *
  *
  * @author :  fanbingjie 
  * @email :  2395590449@qq.com
  * @version : 1.0
  * @since : 2020-01-27 / 14:29
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
class SparkWorker(masterHost : String,masterPort : Int ,masterName : String ) extends  Actor{

  // 是 master 的代理及引用 ref
  var masterProxy : ActorSelection = _
  val id = java.util.UUID.randomUUID().toString

  override def preStart(): Unit = {
    //初始化 masterProxy
    masterProxy = context.actorSelection(s"akka.tcp://SparkMaster@${masterHost}:${masterPort}/user/${masterName}")
    println("preStart 启动 ... ")
  }

  override def receive = {
    case "start" => {
      println("worker 启动了 ...")
      //发出一个注册消息
      masterProxy ! RegisterWorkerInfo(id, 16 , 16 * 1024)

    }

    case RegisteredWorkerInfo =>{
      println("workerid = " + id + "注册成功~ ")

      //当注册成功后，就定义一个定时器，每隔一定时间，发送SendHeartBeat 给自己
      import  context.dispatcher
      //context.system.scheduler.schedule(0 , 3000 , self , SendHeartBeat)
      //1. 0 millis 不延时，立即执行定时器
      //2. 3000 millis 表示每隔 3 秒 执行一次
      //3. self : 表示发给自己
      //4. SendHeartBeat 发送的内容
      context.system.scheduler.schedule(0 millis, 3000 millis, self, SendHeartBeat)
    }
    case SendHeartBeat =>{
      println("worker = " + id + " 给master 发送心跳")
      masterProxy ! HeartBeat(id)
    }

  }
}


object  SparkWorker{

  def main(args: Array[String]): Unit = {

      if(args.length != 6 ){
        println("请输入参数 workerHost workerPort workerName masterHost masterPort masterName ")
        sys.exit()
      }


      val workerHost = args(0)
      val workerPort = args(1)
      val workerName = args(2)
      val masterHost = args(3)
      val masterPort = args(4)
      val masterName = args(5)

      val config = ConfigFactory.parseString(
                  s"""
                akka.actor.provider="akka.remote.RemoteActorRefProvider"
                akka.remote.netty.tcp.hostname=${workerHost}
                akka.remote.netty.tcp.port=${workerPort}
            """.stripMargin)

      //创建ActorSystem
      val sparkWorker = ActorSystem("SparkWorker",config )

      //创建sparkworker 的引用
      val sparkWorkerRef = sparkWorker.actorOf(
        Props(new SparkWorker(masterHost,masterPort.toInt,masterName)),s"${workerName}")

      sparkWorkerRef ! "start"
   }
}


package com.gy.akka.sparkworkermaster.master

import akka.actor.{Actor, ActorSystem, Props}
import com.gy.akka.sparkworkermaster.common._
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.collection.mutable

/**
  *
  *
  * @author :  fanbingjie 
  * @email :  2395590449@qq.com
  * @version : 1.0
  * @since : 2020-01-27 / 13:33
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
class SparkMaster extends  Actor{
  // 定义hashmap ,管理workers
  val workers = mutable.Map[String,WorkerInfo]()

  override def receive = {
    case "start" => {
      println("sparkMaster 启动成功了 ...")
      self ! StartTimeOutWorker
    }
    case RegisterWorkerInfo(id,cpu,ram) =>{
      println("接收到 worker注册信息")
      if(! workers.contains(id)){
        //床架 workerInfo 对象
        val workerInfo = new WorkerInfo(id, cpu ,ram);
        workers += ((id , workerInfo))

        println("服务器的 workers " + workers )
        sender() ! RegisteredWorkerInfo
      }

    }
    case HeartBeat(id) =>{
      //更新对应的worker的心跳时间
      //从worker里取出对应的 workerInfo
      val workerInfo = workers(id)
      workerInfo.lastHeartBeat = System.currentTimeMillis()
    }
    case StartTimeOutWorker =>{
        println("开始了定时检测worker心跳的任务")
        import context.dispatcher
      context.system.scheduler.schedule(0 millis, 9000 millis, self, RemoveTimeOutWorker)
    }
    case RemoveTimeOutWorker =>{
      val workerInfos = workers.values
      val nowTime = System.currentTimeMillis()
      //先把超时的所有 workerInfo，删除即可
      workerInfos.filter( workerInfo => ( nowTime - workerInfo.lastHeartBeat ) > 6000 )
        .foreach(workerInfo  => workers.remove(workerInfo.id))
      println("当前有"+ workers.size +"个worker存活")
    }

  }
}


object SparkMaster{
  def main(args: Array[String]): Unit = {

    if(args.length != 3 ){
      println("请输入参数 host port sparkMasterActor 名字")
      sys.exit()
    }

    val host = args(0)
    val port = args(1)
    val name = args(2)

    //先创建ActorSystem
    val config = ConfigFactory.parseString( s"""
                                  |akka.actor.provider="akka.remote.RemoteActorRefProvider"
                                  |akka.remote.netty.tcp.hostname=${host}
                                  |akka.remote.netty.tcp.port=${port}
            """.stripMargin)

    val sparkMasterSystem = ActorSystem("SparkMaster",config)
    val sparkMaster = sparkMasterSystem.actorOf(Props[SparkMaster],s"${name}")

    //启动sparkMaster
    sparkMaster ! "start"


  }
}
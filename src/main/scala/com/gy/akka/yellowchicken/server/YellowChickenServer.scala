package com.gy.akka.yellowchicken.server

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.gy.akka.yellowchicken.common.{ClientMessage, ServerMessage}
import com.typesafe.config.ConfigFactory

/**
  *
  *
  * @author :  fanbingjie 
  * @email :  2395590449@qq.com
  * @version : 1.0
  * @since : 2020-01-26 / 20:45
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
class YellowChickenServer extends Actor{
  override def receive = {
    case "start" => println("小黄鸡客服开始工作了......")
    case  ClientMessage(mes)=>{
      mes match {
        //使用 match - case 模糊匹配
        case "大数据学费" => sender() ! ServerMessage("350000RMB")
        case "学校地址" => sender() ! ServerMessage("北京昌平 xx 路 xx 大楼")
        case "学习什么技术" => sender() ! ServerMessage("大数据，前端，Python ")
        case _ => sender() ! ServerMessage("你说什么听不懂~")
      }
    }
  }
}


//主程序-入口
object YellowChickenServer extends  App{
    //创建ActorSystem
    // url (统一资源定位)
    val host = "127.0.0.1"   // 服务器 ip 地址
    val port = 9999   //端口号
    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname=$host
         |akka.remote.netty.tcp.port=$port
          """.stripMargin)
    private val serverActorSystem = ActorSystem("Server",config)
    private val yellowChickenRef: ActorRef = serverActorSystem.actorOf(Props[YellowChickenServer] , "yellowChickenServer")


    yellowChickenRef ! "start"

}

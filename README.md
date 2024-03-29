<div align="center">

<img width="500" src="l.png" alt="logo"></br>


# **BilibiliUnfollowingListener** - bilibili取关检测

**BilibiliUnfollowingListener是一个基于[JAVA](https://orangezscb.gitee.io/jvav/ "JAVA")编写的可以在全平台上运行的全自动B站关注/取关检测工具!**
#### bilibili -[ im-cwuom](https://space.bilibili.com/473400804?spm_id_from=333.1007.0.0 " im-cwuom") 关注我获取更多!
</div>

------------

## 它能做什么？
###  常规操作
- 当你不知道到底是谁取关了自己时，此程序会通过固定间隔扫描粉丝数据从而揪出是谁取关了你。
> 间隔可自行调整，**若程序休眠的时间里有多名粉丝取关程序会发送多个邮件来提醒你**。 请不要将间隔设置的**太小**，这会导致程序**过早风控**从而影响效率！
- 当然，如果检测到了**新粉丝**也会提醒你 *（可关闭）*


------------


### 异常处理 / 特殊场景
####  **风控**

处理方式
> 当程序遇到风控时，会自动**休眠**600s，并发出邮件提醒

解决方式
> 尝试**向上修改间隔时间**，保证程序的请求间隔不要太小从而不触发API风控机制


------------


#### cookie失效

处理方式
> 发送一份提示邮件并立刻终止程序运行

解决方式
> 及时补上/更新您的cookie


------------

### 邮件消息
#### 关注检测
包含如下主要信息
- 关注后粉丝数
- 关注者UID
- 关注者昵称
- 关注者主页链接

#### 取关检测
包含如下主要信息
- 取关后粉丝数
- 取关者UID
- 取关者昵称
- 取关者主页链接
- 取关者关注日期 yyyy-MM-dd HH : mm : ss (受API限制，此项不一定显示)

#### cookie检测
包含如下主要信息
- 报错信息，说明...


#### 风控检测
包含如下主要信息
- 报错信息，说明...

#### 图片预览
<details>
<summary>点击展开图片...</summary>


[![关注检测](https://raw.githubusercontent.com/cwuom/BilibiliUnfollowingListener/master/4.png "关注检测")](https://raw.githubusercontent.com/cwuom/BilibiliUnfollowingListener/master/4.png "关注检测")
[![取关检测](https://raw.githubusercontent.com/cwuom/BilibiliUnfollowingListener/master/3.png "取关检测")](https://raw.githubusercontent.com/cwuom/BilibiliUnfollowingListener/master/3.png "取关检测")
[![cookie检测](https://raw.githubusercontent.com/cwuom/BilibiliUnfollowingListener/master/2.png "cookie检测")](https://raw.githubusercontent.com/cwuom/BilibiliUnfollowingListener/master/2.png "cookie检测")
[![cookie检测](https://raw.githubusercontent.com/cwuom/BilibiliUnfollowingListener/master/1.png "cookie检测")](https://raw.githubusercontent.com/cwuom/BilibiliUnfollowingListener/master/1.png "cookie检测")

</details>

------------


### 它是如何工作的？
#### 官方API
此程序利用B站官方提供的API接口实现一系列功能，在此感谢B站的API返回十分简洁从而减小了制作时间。
|  API作用   | API地址  |
|  ----  | ----  |
| 获取粉丝列表  | http://api.bilibili.com/x/relation/followers?&vmid=UID&pn=pn |
| 用户主页  | https://space.bilibili.com/mid |

#### 为何需要cookie?
- B站对访客请求对应API一直是有限制的，所以无法获取粉丝列表第六页导致旧粉丝取关无法被程序检测到。
> 请注意，我们不会泄露您的cookie，若账号因为此程序出现问题那一定是你的问题，请不要在分享时连带cookie.txt(或包含cookie的文件)

------------


### 适用人群
#### 不适宜人群
- 本程序开发初衷是娱乐使用，本人粉丝数不多但根据推测**粉丝基数越大就会越容易被风控**，所以并不建议粉丝数量多的人时候此程序
- 如果你的每日新增/掉粉数量较多的话也不建议使用，此程序会在检测到关注/取关操作后无差别发送邮件提醒(不怕吵的话可以)

#### 适宜人群
- 想看看是哪些群体取关了自己，粉丝数量不多的人
- 仅娱乐使用


------------

## 声明
### 一切开发旨在学习，请勿用于非法用途
- 介于项目的特殊性，**开发者可能在未来会随时停更或删除此项目**
- **软件运行不会窃取你的cookie**，如果账户被盗请不要找我，首先检查分享时cookie是否清空





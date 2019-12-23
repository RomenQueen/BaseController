### 说明：
## 一、设计意图
     此代码旨在提高开发速度，完成快速开发（已在小项目中使用过）。
     
## 二、使用说明
     1.所有业务由Controller接管而非Activity
     2.启动页需要需要注册，也可以使用快速开发quick_ui中的接口实现
     3.启动页注册(WelcomeActivity)后实现如下代码:
```java
public class WelcomeActivity extends BaseActivity<WelcomeController> {
}
```
     4.之后所有Controller继承自BaseCobtroller，具体使用查看代码注释
     5.**Controller主要描述
       a.getLayoutId() 每个界面必须实现的抽象方法，返回布局文件ID,可进行初始化但不建议
       b.onViewCreated() 界面创建完成之后的回调
       c.setData2View(int,Object) 数据填充方法，前者为ViewId 后者为填充内容，适用于
         所有视图，查看代码，未收录的视图或者特殊处理可以重写 fillCustomViewData 以取
         缔常规用法
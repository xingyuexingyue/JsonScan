这是一个校验接口定义的JSON格式与实际请求返回的JSON的工具

实现的功能：

1. 对比 JSON 的字段是否一致
2. 对比 JSON 字段的值类型是否一致

假如定义接口返回的JSON格式如下：

```json
{
  "code": "0",
  "msg": "ok",
  "time": "2012-12-21 12:12:12",
  "data": {
    "count": 1,
    "list": [
      {
        "pid": "商品id",
        "title": "商品标题",
        "price": "商品价格",
        "desc": "商品描述"
      }
    ]
  }
}
```

实际返回的 JSON 格式可能如下：

```json
{
  "code": "0",
  "msg": "ok",
  "time": "2012-12-21 12:12:12",
  "test": "多出来的字段",
  "data": {
    "count": 1,
    "list": [
      {
        "pid": "12345677654321",
        "title": "宇宙超级无敌可爱的小喵咪",
        "price": "12.00",
        "desc": "大懒喵，除了睡就是吃吃吃吃"
      }
    ]
  }
}
```

分析：

对比上面的两个JSON结构，我们了解到所有的 key 都是有层次结构的，其次所有的 key 是唯一的（需要考虑的特殊情况是：不同层次结构下，相同的 key）

所以，如果存在不同层次结构下相同的key，那么该如何比较？

这里解决这个问题用到的方法是：json有个特性，就是每一层结构下，key都是唯一的，那么在每个 key 前面，增加层次结构，这样就可以保证唯一性了

所以，带有层次结构的key，应该是：

```
code,
msg,
time,
data,
data-count,
data-list,
data-list-pid,
data-list-title,
data-list-price,
data-list-desc,
```

在实际中将需要对比的 JSON 放到文件中，格式如下：

```json
[
{
  "code": "0",
  "msg": "ok",
  "time": "2012-12-21 12:12:12",
  "test": "testtesttesttesttest",
  "data": {
    "count": 1,
    "list": [
      {
        "pid": "12345677654321",
        "title": "宇宙超级无敌可爱的小喵咪",
        "price": "12.00",
        "desc": "大懒喵，除了睡就是吃吃吃吃"
      }
    ]
  }
},{
  "code": "0",
  "msg": "ok",
  "time": "2012-12-21 12:12:12",
  "test": "testtesttesttesttest",
  "data": {
    "count": 1,
    "list": [
      {
        "pid": "12345677654321",
        "title": "宇宙超级无敌可爱的小喵咪",
        "price": "12.00",
        "desc": "大懒喵，除了睡就是吃吃吃吃"
      }
    ]
  }
}
]
```

然后将读取到信息转成 List<Object> 的格式，在一一对比返回结果，从而实现批量对比

这里要注意一点，在读 JSON 文件的时候，需要添加判断，如果字符串开始和结束字符含有非{}，必须要去掉；否则会报错

```
com.alibaba.fastjson.JSONException: not close json text, token : :
	at com.alibaba.fastjson.parser.DefaultJSONParser.close(DefaultJSONParser.java:1115)
	at com.alibaba.fastjson.JSON.parse(JSON.java:93)
	at com.alibaba.fastjson.JSON.parse(JSON.java:80)
	at com.alibaba.fastjson.JSON.parseObject(JSON.java:151)
	at org.springframework.cglib.proxy.MethodProxy.invoke(MethodProxy.java:204)
	at org.springframework.aop.framework.CglibAopProxy$CglibMethodInvocation.invokeJoinpoint(CglibAopProxy.java:708)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:157)
	at org.springframework.transaction.interceptor.TransactionInterceptor$1.proceedWithInvocation(TransactionInterceptor.java:98)
	at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:262)
	at org.springframework.transaction.interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:95)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.interceptor.ExposeInvocationInterceptor.invoke(ExposeInvocationInterceptor.java:92)
	at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179)
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:644)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:74)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:83)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:72)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:233)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:87)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:71)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:176)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:50)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:38)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:467)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:683)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:390)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:197)
```


---
layout: post
title: "Use SMO in .NET 4.0"
description: "How to use SMO in .NET 4.0 environment."
category: tech
tags: [.net, sqlserver]
---
{% include JB/setup %}

有时候需要批量刷新数据库，批处理脚本不是很合适，最好可以有一种方法执行*.sql脚本文件。从SQL Server 2005开始，SDK里出现了SQL Server Management Objects (SMO)库，于是我们可以用如下方法解决问题。

    using System.Data.SqlClient;
    using System.IO;
    using Microsoft.SqlServer.Management.Common;
    using Microsoft.SqlServer.Management.Smo;
 
    namespace ConsoleApplication1
    {
        class Program
        {
            static void Main(string[] args)
            {
                string sqlConnectionString = "Data Source=(local);Initial Catalog=AdventureWorks;Integrated Security=True";
                FileInfo file = new FileInfo("C:\myscript.sql");
                string script = file.OpenText().ReadToEnd();
                SqlConnection conn = new SqlConnection(sqlConnectionString);
                Server server = new Server(new ServerConnection(conn));
                server.ConnectionContext.ExecuteNonQuery(script);
            }
        }
    }

但是如果直接在.NET 4.0的项目里引用SMO，会出现编译错误。

    Microsoft.SqlServer.Management.Smo.FailedOperationException: ExecuteNonQuery failed for Database 'TM-Data'.
    System.IO.FileLoadException: 
    Mixed mode assembly is built against version 'v2.0.50727' of the runtime and cannot be loaded in the 4.0 runtime without additional configuration information.

似乎是这个SDK基于.NET 2.0编译，并不适用于.NET 4.0的运行时。但是我们可以用一个work around来解决，在应用程序的配置文件里添加useLegacyV2RuntimeActivationPolicy属性，比如app.exe.config，代码如下。


<pre name="code" class="c-sharp">
<configuration> 
  <startup useLegacyV2RuntimeActivationPolicy="true"> 
    <supportedRuntime version="v4.0"/> 
  </startup> 
</configuration> 
</pre>


这样就可以正常编译并且运行引用了SMO的.NET 4.0项目。
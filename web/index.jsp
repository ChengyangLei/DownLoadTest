<%--
  Created by IntelliJ IDEA.
  User: 占大帅
  Date: 2018/5/26
  Time: 18:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>首页</title>
</head>
<body>
<input type="button" name="按钮啊" id="zipButton" value="压缩文件下载">
<input type="button" name="按钮" id="button" value="按钮">
<a href="/downLoadServlet">下载</a>
<script src="js/jquery-3.3.1.min.js"></script>
<script>
    $("#button").click(function () {
        window.location.href = "/downLoadServlet?templateId=111111";
    });
    $("#zipButton").click(function () {
        window.location.href = "/zipDownLoadServlet";
    })
</script>
</body>
</html>

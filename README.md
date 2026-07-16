# Agri-Link
实训项目

## 163 邮箱验证码

启动后端前配置平台发件邮箱和该邮箱的 SMTP 授权码（不是邮箱登录密码）：

```powershell
$env:AGRILINK_DB_PASSWORD='你的MySQL密码'
$env:AGRILINK_MAIL_USERNAME='sender@163.com'
$env:AGRILINK_MAIL_AUTH_CODE='<163 SMTP 授权码>'
.\mvnw.cmd spring-boot:run
```

数据库密码、邮箱账号和 SMTP 授权码只通过环境变量传入，不应写入 `application.yml` 或数据库。

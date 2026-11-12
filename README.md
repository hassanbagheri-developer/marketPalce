<div align="center">

# 🛒 MarketPlace

**Marketplace service of the Wiki Mount platform**

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-6DB33F?logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/DB-MySQL-4479A1?logo=mysql&logoColor=white)
![Keycloak](https://img.shields.io/badge/Auth-Keycloak-4D4D4D?logo=keycloak&logoColor=white)
![Status](https://img.shields.io/badge/status-early%20stage-yellow)

</div>

---

## 🇬🇧 Overview

A **Spring Boot 4** service for the marketplace part of the platform. It is in an early stage: the security, logging and
Keycloak integration are in place, while the business endpoints are still samples.

- **OAuth2 resource server** validating Keycloak JWTs (realm `my-realm`)
- **Automatic role sync**: on startup and every hour, it scans the controllers' authorities and creates the matching roles in Keycloak
- **MySQL** with JPA, **Jasypt** for encrypted properties, **Logbook** for HTTP logging, **Swagger UI**

Port `8090`, context path `/marketPlace`. Reached through **api-gateway** at `/marketPlace/**`.

---

<div dir="rtl">

## 🇮🇷 راهنمای فارسی

### وضعیت پروژه

این سرویس در مرحله‌ی اولیه است. زیرساخت آماده است (امنیت، Keycloak، لاگ، Swagger)، ولی endpointها فعلاً نمونه‌اند.

</div>

```mermaid
flowchart LR
    G[🚪 api-gateway] -->|/marketPlace/**| M[🛒 marketPlace<br/>:8090]
    M --> DB[(🐬 MySQL<br/>hassan)]
    M -. بررسی JWT .-> K[(🔐 Keycloak)]
    M -->|همگام‌سازی نقش‌ها<br/>هر ۱ ساعت| K
```

<div dir="rtl">

### امکانات

| امکان | توضیح |
|---|---|
| 🔐 امنیت | همه‌ی مسیرها به‌جز `public/**` توکن Keycloak می‌خواهند |
| 🔄 همگام‌سازی نقش‌ها | موقع شروع و هر یک ساعت، authorityهای کنترلرها خوانده و به‌عنوان role در Keycloak ساخته می‌شوند |
| 🪪 اعتبارسنجی | annotationهای `@NationalCode` (کد ملی) و `@ValidDate` |
| 📜 لاگ | Logbook، همراه با نام کاربر در هر درخواست |
| 🔑 رمزنگاری | مقادیر `ENC(...)` در تنظیمات با Jasypt باز می‌شوند |
| 📖 مستندات | Swagger UI |

### Endpointهای نمونه

| مسیر | دسترسی |
|---|---|
| `GET /marketPlace/public/test1` … `test3` | عمومی |
| `GET /marketPlace/api/test1` … `test3` | با توکن و نقش مخصوص هر مسیر، مثلاً `api.marketplace.api.test1` |

نقش‌های داخل `@PreAuthorize("hasAuthority('...')")` همان‌هایی هستند که همگام‌سازی خودکار در Keycloak می‌سازد؛ کافی است آن‌ها را به کاربر بدهید.

### اجرا روی سیستم خودتان

پیش‌نیاز: Java 21، MySQL با دیتابیس `hassan`، Keycloak با realm `my-realm` و کلاینت‌های `marketPlace` و `adminKeycloack`.

</div>

```bash
./mvnw spring-boot:run
# http://localhost:8090/marketPlace/public/test1
# http://localhost:8090/marketPlace/swagger-ui/index.html
```

<div dir="rtl">

### تنظیمات مهم

| کلید | توضیح |
|---|---|
| `spring.datasource.*` | آدرس، کاربر و رمز MySQL |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | آدرس realm در Keycloak |
| `keycloak.serverUrl` / `realm` | آدرس Keycloak و realm |
| `keycloak.clientId` / `clientSecret` | کلاینت `marketPlace` |
| `keycloak.rootClient` | کلاینت ادمین برای ساخت نقش‌ها (`adminKeycloack`) |
| `jasypt.encryptor.password` | کلید باز کردن مقادیر `ENC(...)` |
| `spring.config.import` | آدرس config-server |

### CI/CD

فایل: [`.github/workflows/cicd.yaml`](.github/workflows/cicd.yaml). با هر push روی `master` (یا اجرای دستی):
jar ساخته می‌شود و image به Docker Hub push می‌شود (`hassan233/marketplace:1.0.<run>` و `latest`). deploy خودکار هنوز ندارد.

| Secret | مقدار |
|---|---|
| `DOCKERHUB_TOKEN` | توکن Docker Hub |

برای استفاده در حساب خودتان، `IMAGE_NAME` و نام کاربری `docker login` را در workflow عوض کنید.

### پروژه‌های مرتبط

- [api-gateway](https://github.com/hassanbagheri-developer/api-gateway)
- [core-service](https://github.com/hassanbagheri-developer/core-service)
- [config-server](https://github.com/hassanbagheri-developer/config-server)

</div>

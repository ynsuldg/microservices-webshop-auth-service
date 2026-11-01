# Auth Service

## Ansvar

Auth Service sköter autentisering i Microservices Webshop. Användaren loggar in med användarnamn och lösenord
och får en signerad JWT med identitet och roller. Lösenord lagras som BCrypt-hashar.
Tjänsten tillhandahåller `/auth/login`, `/auth/me` och publika nycklar på `/auth/jwks`.
Gatewayn använder auth-service för att verifiera kunden vid betalnings- och avbrottsanrop.

## Kontohantering

| Anrop | Funktion |
| --- | --- |
| `POST /appusers` | Registrerar ett konto med e-postadress och lösenord. Rollen sätts alltid till USER. |
| `POST /appusers/password-reset` | Sparar ett nytt hashat lösenord för en befintlig e-postadress. |
| `DELETE /appusers/me` | Raderar kontot från verifierad JWT efter kontroll av nuvarande lösenord. |

Nya lösenord kräver minst åtta tecken och högst 72 UTF-8-byte. Lösenord returneras aldrig i API-svaren.
Frontend anropar dessa funktioner via gatewayn. Efter registrering och återställning loggar användaren in på nytt.

Demoåterställningen saknar ägarverifiering: den som känner till e-postadressen kan byta lösenordet.
Den är avsedd för testkonton och ska inte användas i produktion.

## Tekniker

Java 17, Spring Boot 4, Spring Security, JWT, Maven och PostgreSQL.

## Databas

PostgreSQL hos Neon lagrar användare, lösenordshashar och roller.

## Miljövariabler

| Variabel | Lokalt | Render |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | Utvecklingsdatabasens JDBC-adress. | JDBC-adress som tjänsten kan nå från Render. |
| `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | Databasens inloggning. | Databasens inloggning. |
| `JWT_PUBLIC_KEY` | Base64-kodad RSA-nyckel i X.509-format. | Motsvarande publik nyckel för Render-miljön. |
| `JWT_PRIVATE_KEY` | Base64-kodad RSA-nyckel i PKCS#8-format. | Matchande privat nyckel; endast auth-service ska ha den. |
| `ALLOWED_ORIGINS` | `http://localhost:5173` | `https://microservices-webshop-frontend.onrender.com` |
| `JWT_EXPIRATION_MINUTES` | Valfri, standard 60. | Valfri, standard 60. |
| `JWT_KEY_ID` | Valfri, standard `jwt-key-1`. | Identifierare för nyckelparet. |

Nycklarna måste höra ihop. Klistra in rena värden utan citattecken eller escape-tecken.
Hemliga värden ska inte sparas i Git.

## Starta applikationen lokalt

Sätt miljövariablerna och starta från projektmappen.

**macOS/Linux:**

```bash
./mvnw spring-boot:run
```

**Windows PowerShell:**

```powershell
.\mvnw.cmd spring-boot:run
```

Kräver Java 17 eller senare och tillgång till databasen. Lokal adress: `http://localhost:8080`.
Frontend startas i sitt repo med `npm install` och `npm run dev` och ansluter via gatewayn.

## Render

| Tjänst | Render-adress |
| --- | --- |
| Frontend – öppna webshoppen | [microservices-webshop-frontend.onrender.com](https://microservices-webshop-frontend.onrender.com) |
| Auth Service | [microservices-webshop-auth-service.onrender.com](https://microservices-webshop-auth-service.onrender.com) |
| Product Service | [microservices-webshop-product-service.onrender.com](https://microservices-webshop-product-service.onrender.com) |
| Order Service | [microservices-webshop-order-service.onrender.com](https://microservices-webshop-order-service.onrender.com) |
| Email Service | [microservices-webshop-email-service.onrender.com](https://microservices-webshop-email-service.onrender.com) |
| Gateway Service | [microservices-webshop-gateway-service.onrender.com](https://microservices-webshop-gateway-service.onrender.com) |
| Payment Service | [microservices-webshop-payment-service.onrender.com](https://microservices-webshop-payment-service.onrender.com) |
- Docker-image: `ynsuldg/microservices-webshop-auth-service:1.0.0`.

Använd **Web Service → Existing Image**, sätt miljövariablerna i Render och behåll Docker-imagens startkommando.
Efter en ny image från senaste `main` väljs **Manual Deploy → Deploy latest reference**.
En äldre Actions-omkörning kan skriva över samma tagg med äldre kod.

Backendadressen är ett API, inte en vanlig webbsida. Rotadressen kan ge 401 eller 404 utan att tjänsten är nere.
Render Free kan ge väntetid när tjänsterna väcks. Se [Render-dokumentationen](https://render.com/docs/deploying-an-image).

Publika nycklar: [`/auth/jwks`](https://microservices-webshop-auth-service.onrender.com/auth/jwks).
Product- och order-service använder denna adress som `JWT_PUBLIC_URL`.

## Relaterade repositories

- [Product Service](https://github.com/ynsuldg/microservices-webshop-product-service)
- [Order Service](https://github.com/ynsuldg/microservices-webshop-order-service)
- [Email Service](https://github.com/ynsuldg/microservices-webshop-email-service)
- [Gateway Service](https://github.com/ynsuldg/microservices-webshop-gateway-service)
- [Payment Service](https://github.com/ynsuldg/microservices-webshop-payment-service)
- [Frontend](https://github.com/ynsuldg/microservices-webshop-frontend)

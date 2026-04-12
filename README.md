# PassGo
Kupuj oraz odsprzedawaj bilety na wybrane wydarzenia w twoim mieście.

## Spis treści
- [PassGo](#passgo)
  - [Spis treści](#spis-treści)
  - [Opis projektu](#opis-projektu)
  - [Funkcjonalności](#funkcjonalności)
    - [Użytkownik](#użytkownik)
    - [Organizator](#organizator)
    - [Administrator](#administrator)
  - [Technologie](#technologie)
  - [Budowanie i testowanie](#budowanie-i-testowanie)
  - [API i dokumentacja](#api-i-dokumentacja)
  - [Bezpieczeństwo](#bezpieczeństwo)
  - [Konteneryzacja](#konteneryzacja)
  - [Wdrożenie](#wdrożenie)
  - [Demo](#demo)
  - [Endpoints for user](#endpoints-for-user)
  - [Endpoints for the organizer](#endpoints-for-the-organizer)
  - [Endpoints for administrator](#endpoints-for-administrator)
  - [Wymagania projektowe - tracklist](#wymagania-projektowe---tracklist)


---

## Opis projektu

System sprzedaży biletów umożliwiający użytkownikom kupowanie i odsprzedawanie biletów na wydarzenia. Organizatorzy mogą zarządzać wydarzeniami, a administratorzy nadzorują całość systemu. Projekt zakłada wykorzystanie nowoczesnych technologii oraz automatyzację procesu budowania, testowania i wdrażania.

---

## Funkcjonalności

### Użytkownik
- Tworzenie konta
- Kupowanie, sprzedaż i odsprzedaż biletów
- Przeglądanie wydarzeń
- Wirtualny portfel
- Generowanie biletów w formacie PDF i QR code
- Centrum pomocy i FAQ
- Profil użytkownika i historia transakcji

### Organizator
- Tworzenie, anulowanie i edycja wydarzeń
- Zarządzanie wydarzeniami
- Podgląd statystyk wydarzenia
- Ustawienie planu obiektu
- Wysyłanie prośby o akceptację wydarzenia przed jego dodaniem

### Administrator
- Zatwierdzanie planów obiektów
- Zarządzanie użytkownikami i organizatorami
- Wgląd w statystyki
- Zarządzanie wydarzeniami

---

## Technologie
- **Backend**: Java + Spring Boot
- **Budowanie projektu**: Maven
- **Baza danych**: PostgreSQL (Docker), H2
- **Wersjonowanie bazy danych**: Flyway
- **Testy**: JUnit, Spring MVC (testowanie endpointów)
- **API Dokumentacja**: Swagger
- **Konteneryzacja**: Docker, Docker Compose
- **Bezpieczeństwo**: Spring Security
- **Autoryzacja**: JWT tokens

---

## Budowanie i testowanie

- ### Automatyczne budowanie
  - Projekt wykorzystuje Maven do budowania aplikacji.
  - Wyniki budowania (pass/fail) są widoczne w repozytorium.
  - Artefakt w postaci pliku z rozszerzeniem ```jar```

- ### Uruchamianie serwera lokalnie (Maven Wrapper)

  Projekt zawiera Maven Wrapper — nie wymaga zainstalowanego Mavena.

  **Profil `local`** używa bazy H2 w pamięci (bez Dockera) i wyłącza JWT:
  ```bash
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
  ```

  **Profil `dev`** wymaga uruchomionej bazy PostgreSQL (np. przez Docker Compose):
  ```bash
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev,dev_flyway
  ```

  Na Windows użyj `mvnw.cmd` zamiast `./mvnw`.

- ### Uruchamianie przez Docker Compose

Przed zbuduj aplikację komendą maven powyżej, jako że Dockewrfile kopiuje pliki z folderu `target/`

  Uruchamia aplikację razem z bazą PostgreSQL:
  ```bash
  docker-compose up --build
  ```

  Aplikacja będzie dostępna pod adresem `http://localhost:9090`.

  Aby zatrzymać i usunąć kontenery:
  ```bash
  docker-compose down
  ```

- ### Logowanie jako administrator

  Przy pierwszym uruchomieniu `AdminInitializer` automatycznie tworzy konto admina (jeśli żaden nie istnieje).

  Wyślij żądanie `POST /auth/login` z body:
  ```json
  {
    "login": "admin",
    "password": "admin"
  }
  ```

  W odpowiedzi otrzymasz token JWT, który należy dołączać do kolejnych żądań jako nagłówek:
  ```
  Authorization: Bearer <token>
  ```

- ### Testy
  - **Jednostkowe** i **integracyjne** (Spring MVC do testowania endpointów)
  - Testy uruchamiane automatycznie podczas procesu budowania

---

## API i dokumentacja

- Dokumentacja API dostępna za pomocą **Swaggera**.
- Poprawnie skonstruowane **REST API** z obsługą błędów.

---

## Bezpieczeństwo

Wykorzystanie *Spring Security* w celach bezpieczeństwa. 
Zabezpieczenie wszystkich endpointów aplikacji.
Implementacja autentykacji w postaci tokenów JWT.
Implementacja odświeżania wygasłych tokenów.
Dodanie ról oraz przywileji.

---

## Konteneryzacja

- **Dockerfile** i **Docker Compose**:
  - Baza danych (PostgreSQL)
  - Środowisko monitorujące (ELK)
  - Aplikacja Spring Boot

---

## Wdrożenie

- Proces budowania tworzy gotowy do wdrożenia artefakt (plik `.jar`)

## Demo

- Przygotowane **demo serwisu** z użyciem Postmana i innych narzędzi

---


🔗 **Link do repozytorium**: [Repozytorium projektu](https://github.com/passgodev/passgo)

---



## Endpoints for user

| HTTP_METHOD | ENDPOINT                     | OPIS                                                  |
|------------|------------------------------|------------------------------------------------------|
| POST       | /accounts                    | Tworzenie konta użytkownika                         |
| GET        | /accounts                    | Pobieranie listy kont (paginacja, filtrowanie)      |
| GET        | /accounts/{id}/details       | Pobieranie szczegółów konta                         |
| GET        | /tickets/{id}                | Pobieranie biletu                                   |
| POST       | /tickets                     | Kupowanie biletu                                   |
| GET        | /events                      | Przeglądanie wydarzeń (paginacja, filtrowanie)     |
| GET        | /users/{id}/wallet           | Pobranie informacji o portfelu                     |
| POST       | /users/{id}/wallet           | Dodanie środków do portfela                        |
| GET        | /tickets/{id}/(printing lub form)        | Pobranie biletu w formacie PDF z QR                |
| GET        | /faqs                        | Pobranie listy FAQ                                 |
| GET        | /faqs/{id}                   | Pobranie szczegółów FAQ                            |
| GET        | /users/{id}/transactions     | Pobranie historii transakcji użytkownika          |
| GET        | /users/{id}/details          | Pobranie szczegółów użytkownika                   |

## Endpoints for the organizer

| HTTP_METHOD | ENDPOINT                    | OPIS                                             |
|------------|-----------------------------|-------------------------------------------------|
| POST       | /events                      | Tworzenie wydarzenia                            |
| GET        | /events/{id}/details        | Podgląd szczegółów wydarzenia                  |
| GET        | /buildings                   | Pobranie listy budynków                         |
| GET        | /buildings/{id}/details      | Szczegóły budynku                               |
| POST       | /buildings                   | Prośba o utworzenie nowego obiektu             |

## Endpoints for administrator

| HTTP_METHOD | ENDPOINT                     | OPIS                                               |
|------------|------------------------------|---------------------------------------------------|
| GET        | /buildings?approved=false    | Pobranie planów budynków do zatwierdzenia        |
| PATCH      | /buildings/{id}              | Zatwierdzenie planu budynku (pole approved)      |
| GET        | /users                       | Zarządzanie użytkownikami                        |
| POST       | /users                        | Dodanie użytkownika                              |
| DELETE     | /users/{id}                   | Usunięcie użytkownika                            |
| PUT/PATCH  | /users/{id}                   | Aktualizacja danych użytkownika                  |
| GET        | /organisers                   | Zarządzanie organizatorami, pobranie listy organizatorów                       |
| GET        | /organisers/{id}                   | Pobranie informacji o organizatorze                       |
| GET        | /stats                        | Wgląd w statystyki (np. ile jest wydarzeń obecnie)                               |
| POST       | /events/{id}                   | Dodanie wydarzenia                              |
| PUT        | /events/{id}                   | Aktualizacja wydarzenia                         |
| DELETE     | /events/{id}                   | Usunięcie wydarzenia                            |
| POST       | /faqs                         | Dodanie FAQ                                     |
| DELETE     | /faqs/{id}                     | Usunięcie FAQ                                   |
| PUT        | /faqs/{id}                     | Aktualizacja FAQ                               |

## Wymagania projektowe - tracklist

- Domena i zakres funkcjonalny
  - Użytkownik:
    - [x] Tworzenie konta
    - [x] Kupowanie, sprzedaż biletu,odsprzedaż
    - [x] Przeglądanie wydarzeń
    - [x] Wirtualny portfel
    - [x] Generowanie biletów w formacie PDF i QR code
    - [x] Centrum pomocy i FAQ
    - [x] Profil użytkownika
    - [x] Historia transakcji

  - Organizator:
    - [x] Tworzenie konta
    - [x] Tworzenie wydarzeń
    - [x] Anulowanie wydarzeń
    - [x] Edycja, zarządzanie wydarzeń
    - [x] Podgląd statystyk wydarzenia
    - [x] Ustawienie planu obiektu
    - [x] Przed dodaniem wydarzenia dodaj prośbę o wydarzenie o akceptację

  - Administrator:
    - [x] Zatwierdzanie planów obiektów
    - Zarządzanie użytkownikami i organizatorami
      - [x] Zatwierdzanie kont organizatorów
    - [x] Wgląd w statystyki
    - Zarządzanie wydarzeniami
      - [x] Zatwierdzenia wydarzeń

- [x] Kod źródłowy aplikacji należy utrzymywać na **Githubie** ~~lub Gitlabie~~
- [x] Kod powinien być automatycznie budowany (~~gradle lub~~ **maven**), a wynik budowania (pass/fail) powinien być widoczny w repozytorium  
- [x] Kod powinien być automatycznie testowany
  - [x] Testowanie na min. dwóch poziomach
    - [x] Testy jednostkowe
    - [x] Testy interfejsu HHTP
  - [x] Testowanie powinno być elementem budowania projektu  
  - [x] Wyniki testowania powinny być widoczne w repozytorium  
- [x] Aplikację należy przetestować wydajnościowo z użyciem np. Gatlinga
- [x] Kod powinien być automatycznie skanowany pod kątem bezpieczeństwa (np. Snyk)
- [x] Dokumentację API należy udostępnić w formie Swaggera  
- [x] Usługa powinna udostępniać poprawnie skonstruowane REST API, łącznie z obsługą błędów  
- [x] Musi być wykorzystywana data i czas, z poprawnym użyciem klasy Clock 
- [x] Dependency Injection: należy używać wstrzykiwania przez konstruktor  
- [x] Należy używać bibliotekę do logów
- [x] Proces budowania powinien wytwarzać gotowy do wdrożenia artefakt: plik JAR  
- [x] Należy dostarczyć pliki: Dockerfile oraz Docker Compose, zawierające kompletne środowisko uruchomieniowe  
- [x] Usługa powinna zawierać persystencję. Baza danych w odrębnym Dockerfile, połączona poprzez compose  
- [x] Usługa powinna być monitorowana np. za pomocą ELK. Środowisko monitorujące w osobnym Dockerfile, połączone przez compose 
- [x] Usługa powinna korzystać z jakiegoś zewnętrznego API 
- [x] Usługa powinna być zabezpieczona przynajmniej na podstawowym poziomie. Logowanie basic auth lub autoryzacja Google  
- [x] Usługa zawiera minimum 3 poziomy uprawnień użytkowników  
- [x] Użytkownicy powinni mieć ograniczony dostęp do niektórych zasobów (np. tylko swoich)  
- [x] Należy przygotować demo serwisu  
- *Wszelkie dodatkowe elementy ustalone z prowadzącym są również obowiązkowe*
- [x] Użycie narzędzia do wersjonowania bazy danych (flyway)
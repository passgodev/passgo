# info for lab's professor: checkout dev branch

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
- **Baza danych**: PostgreSQL (Docker)
- **Testy**: JUnit, Spring MVC (testowanie endpointów)
- **API Dokumentacja**: Swagger
- **Konteneryzacja**: Docker, Docker Compose
- **Bezpieczeństwo**: *uzupełnij*
- **Autoryzacja**: *uzupełnij*

---

## Budowanie i testowanie

- ### Automatyczne budowanie
  - Projekt wykorzystuje Maven do budowania aplikacji.
  - Wyniki budowania (pass/fail) są widoczne w repozytorium.

- ### Testy
  - **Jednostkowe** i **integracyjne** (Spring MVC do testowania endpointów)
  - Testy uruchamiane automatycznie podczas procesu budowania

---

## API i dokumentacja

- Dokumentacja API dostępna za pomocą **Swaggera**.
- Poprawnie skonstruowane **REST API** z obsługą błędów.

---

## Bezpieczeństwo

*uzupełnij*

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


🔗 **Link do repozytorium**: [Repozytorium projektu](https://github.com/Pajtus/passgo)

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
    - [ ] Tworzenie konta
    - [ ] Kupowanie, sprzedaż biletu,odsprzedaż
    - [ ] Przeglądanie wydarzeń
    - [ ] Wirtualny portfel
    - [ ] Generowanie biletów w formacie PDF i QR code
    - [ ] Centrum pomocy i FAQ
    - [ ] Profil użytkownika i historia transakcji

  - Organizator:
    - [ ] Tworzenie wydarzeń, anulowanie, edycja - zarządzanie
    - [ ] Podgląd statystyk wydarzenia
    - [ ] Ustawienie planu obiektu
    - [ ] Przed dodaniem wydarzenia dodaj prośbę o wydarzenie o akceptację

  - Administrator:
    - [ ] Zatwierdzanie planów obiektów
    - [ ] Zarządzanie użytkownikami i organizatorami
    - [ ] Wgląd w statystyki
    - [ ] Zarządzanie wydarzeniami

- [x] Kod źródłowy aplikacji należy utrzymywać na **Githubie** ~~lub Gitlabie~~
- [ ] Kod powinien być automatycznie budowany (~~gradle lub~~ **maven**), a wynik budowania (pass/fail) powinien być widoczny w repozytorium  
- [ ] Kod powinien być automatycznie testowany
  - [ ] Testowanie na min. dwóch poziomach
    - [ ] Testy jednostkowe
    - [ ] Testy interfejsu HHTP
  - [ ] Testowanie powinno być elementem budowania projektu  
  - [ ] Wyniki testowania powinny być widoczne w repozytorium  
- [ ] Aplikację należy przetestować wydajnościowo z użyciem np. Gatlinga
- [ ] Kod powinien być automatycznie skanowany pod kątem bezpieczeństwa (np. Snyk)
- [ ] Dokumentację API należy udostępnić w formie Swaggera  
- [ ] Usługa powinna udostępniać poprawnie skonstruowane REST API, łącznie z obsługą błędów  
- [ ] Musi być wykorzystywana data i czas, z poprawnym użyciem klasy Clock 
- [ ] Dependency Injection: należy używać wstrzykiwania przez konstruktor  
- [ ] Należy używać bibliotekę do logów
- [ ] Proces budowania powinien wytwarzać gotowy do wdrożenia artefakt: plik JAR  
- [ ] Należy dostarczyć pliki: Dockerfile oraz Docker Compose, zawierające kompletne środowisko uruchomieniowe  
- [ ] Usługa powinna zawierać persystencję. Baza danych w odrębnym Dockerfile, połączona poprzez compose  
- [ ] Usługa powinna być monitorowana np. za pomocą ELK. Środowisko monitorujące w osobnym Dockerfile, połączone przez compose 
- [ ] Usługa powinna korzystać z jakiegoś zewnętrznego API 
- [ ] Usługa powinna być zabezpieczona przynajmniej na podstawowym poziomie. Logowanie basic auth lub autoryzacja Google  
- [ ] Usługa zawiera minimum 3 poziomy uprawnień użytkowników  
- [ ] Użytkownicy powinni mieć ograniczony dostęp do niektórych zasobów (np. tylko swoich)  
- [ ] Należy przygotować demo serwisu  
- *Wszelkie dodatkowe elementy ustalone z prowadzącym są również obowiązkowe*

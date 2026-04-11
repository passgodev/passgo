# Zarządzanie Schematem Bazy Danych (PassGo)

Dokumentacja procesów migracji i aktualizacji struktury bazy danych. Projekt wykorzystuje **Flyway** dla zachowania spójności na środowiskach współdzielonych oraz **Hibernate DDL-Auto** do szybkiego prototypowania lokalnego.

---

## Przygotowanie Środowiska

Przed przystąpieniem do prac na systemach Unixowych (Ubuntu/Fedora/macOS), należy nadać uprawnienia wykonywania dla skryptu migracyjnego oraz wrappera Mavena:

```bash
chmod +x mvnw
chmod +x flyway_migrate.sh
```

Włącz bazę danych w kontenerze dockera używając:

```bash
docker compose up -d db
```

### 1. Profil `dev` (PostgreSQL + Flyway)
**Kolejność działań:**
1. **Migracja SQL:** Utwórz nowy plik `.sql` w `src/main/resources/db/migration/` (zachowując numerację, np. `V1.6.0__opis_zmiany.sql`).
2. **Aktualizacja Bazy:** Uruchom skrypt pomocniczy, aby zaaplikować zmiany w Postgresie:
   ```bash
   ./flyway_migrate.sh
   ```
3. **Model Java:** Wprowadź zmiany w klasach oznaczonych adnotacją @Entity
4. **Weryfikacja:** Uruchom aplikację z aktywnym profilem dev. Hibernate wykona operację validate – jeśli model w Javie nie pokrywa się ze strukturą w Postgresie, aplikacja zgłosi błąd i zatrzyma start.

### 2. Profil `local` (H2 In-memory)
**Kolejność działań:**
1. **Model Java:** Wprowadź zmiany w klasach oznaczonych adnotacją @Entity
2. **Automatyczna Aktualizacja:** Uruchom aplikację z profilem local. Hibernate automatycznie zaktualizuje schemat w pamięci RAM (ddl-auto: update).
# Сервер очередей (бэкенд)
## Инструкция по запуску
* Создать базу данных *Queue*
  1. Создать пустую базу данных *Queue*, используя СУБД *PostgreSQL 11.4*
  2. Восстановить базу данных, используя дамп базы данных в папке *database* и команду `psql -U postgres Queue < backup.sql`
* Открыть проект в папке queue-server, используя среду разработки *IntelliJ IDEA* 

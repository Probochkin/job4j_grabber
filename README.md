# job4j_grabber
Система запускается по расписанию - раз в минуту.  Период запуска указывается в настройках - app.properties. 

Первый сайт будет career.habr.com. В нем есть раздел https://career.habr.com/vacancies/java_developer. С ним будет идти работа. Программа должна считывать все вакансии относящиеся к Java и записывать их в базу.

Доступ к интерфейсу будет через REST API
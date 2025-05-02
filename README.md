**Запуск тестов из программы IntelliJ IDEA**
	
- Навести фокус на папку с тестами, [\FinalWorks\src\test\java\tests](https://github.com/NNS-Nikolay/FinalWorks/tree/master/src/test/java/tests),
нажать ПКМ и выбрать Run 'Tests in 'tests''
- Тесты запускаются и начнется прохождение тестов (запуск браузера отключен)
- Результаты прохождения тестов будут находиться в папке allure-results (папка в корне проекта)
- Для просмотра результатов пройденных тестов, в терминале нужно выполнить команду allure serve
	
	
**Запуск тестов из командной строки**
	
- Запустить терминал Windows PowerShell
- Перейти в папку с проектом
- Выполнить команду mvn clean test 
- Начнется запуск тестов
- После прохождения тестов можно посмотреть отчет в Allure
- Для этого в терминале Windows PowerShell нужно выполнить команду allure serve
	
**Запуск тестов в GitHub Actions**

- Ссылка на проект https://github.com/NNS-Nikolay/FinalWorks
- Тесты запускаются после Push в ветку master

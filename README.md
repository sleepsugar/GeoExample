# GeoExample

Устранение зависимости от GPS и повышение качества трекинга с помощью использования встроенных в смартфон датчиков.

<h4>Реализованная функциональность</h4>
<ul>
    <li>Сбор и отображение данных с помощью GPS;</li>
    <li>Сбор и отображение данных с помощью сесоров;</li>
</ul> 
<h4>Особенность проекта в следующем:</h4>
<ul>
 <li>Трекинг локации в местах со слабым GPS-сигналом. На этом видео https://clck.ru/ZAhgF осуществляется переход через подземку. Красная линия – сенсоры, фиолетовая – GPS. Видно что в отличие от GPS, сенсоры не прерываются и позволяют отследить передвижение пользователя;</li>
 <li>Трегинг локации в магазинах. В магазинах ровный пол, что позволяет работать с сенсорами максимально точно;</li>
 <li>Данный способ лучше всего работает при передвижении на транспорте: такси, автобус, метро, поезд.</li>  
</ul>
<h4>Основной стек технологий:</h4>
<ul>
  <li>Android, Kotlin</li>
  <li>Python</li>
	<li>Git</li>
</ul>

<h4>Демо</h4>
<p>Ссылка на видеозаписи: https://clck.ru/ZAhgZ </p>
<p>Для того чтобы запустить приложение, зайдите в настройки приложения и дайте все необходимые разрешения. При запуске приложения на экране с начальной картой, перед тем как нажимать на кнопку старт убедитесь что карта распознала ваше местоположения, это нужно для начальной точки сенсоров. Ссылка на apk-файл: https://clck.ru/ZAhnD</p>

УСТАНОВКА
------------
Для запуска проекта вставьте свой api-ключ Google Maps
~~~
<meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${YOUR_MAPS_API_KEY}" />
~~~


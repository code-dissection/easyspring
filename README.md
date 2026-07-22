<h1 style="font-size: 70px; color: #4CAF50; margin-top: 0;">EasySpring 😎</h1>

<h2 style="font-size: 34px; margin-top: 40px;">
    EasySpring это ультра легкий фреймворк с концепцией Fail-Fast.
</h2>

<h2 style="font-size: 34px;margin-top: 40px;"> 💥Что умеет:</h2>

<div style="font-size: 18px;line-height: 1.6; font-family: monospace; margin-top: 20px;">

* **Валидация на этапе сборки графа:**
  * Контроль за наличием циклических зависимостей
  * Контроль проблемы внедрения разнородных областей видимости
  * Контроль отсутствия/множественности имплементаций
* **Менеджер жизненного цикла:** 
  * Аннотация @Init - для выполнения логики после создания бина.
  * Аннотация @Close - для выполнения логики бина перед уничтожением пользовательского приложения
  * Системный хук для вызова контролируемого уничтожения контекста
* **Менеджер конфигураций:**
  * Параметры приложения считываются из файла AppSettings.yaml / AppSettings.yml.
  * Полевая и конструкторская инжекция через аннотацию '@ValueFrom'
* **Дифференцированное управление скоупами:**
  * Аннотация @OneOff - для временного бина
  * Дефолтный скоуп бина - синглтон
* **Не требует дебага для понимания инфраструктурной проблемы:**
  * Фреймворк не кидает эксепшенов, кроме кастомных, с псевдографикой и стектрейсом до 10 строк.
* **Нулевой оверхед в рантайме**

</div>

<h2 style="font-size: 34px; margin-top: 40px;">🛠️ Архитектура</h2>

<div style="font-size: 18px;  line-height: 1.6; font-family: monospace; margin-top: 20px;">
Контекст поднимается за счет строго последовательного 6-ти фазного пайплайна, оркестрируемого <br/>
загрузчиком <code>Bootstrapper</code>

</div>
<div style="font-size: 18px;  line-height: 1.6; font-family: monospace; margin-top: 20px;">
    [1] Сканирование файла конфигураций <br>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>SettingsFileScanner</code> ──> Read AppSettings.yml / AppSettings.yaml<br/>
    [2] Сканирование клиентского проекта <br>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ProjectScanner</code> ──> Scan package & generate Metadata<br/>
    [3] Топологическая сортировка метафинформации клиентского проекта и контроль наличия<br/>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;циклических зависимостей<br>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>TopologySorter</code> ──> Build Graph & sort dependencies<br/>
    [4] Создание упорядоченного списка биндефинишенов. Валидация скоупа будущих бинов.<br/>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>DefinitionFactory</code> ──> Create BeanDefinitions & Validate Scopes<br/>
    [5] Создание бинов и валидация<br/>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>BeanFactory</code> ──> Instantiate Beans & Inject Dependencies<br/>
    [6] Сборка контекста<br/>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<code>ProjectContext</code> ──> Assemble Context & Ready for use
</div>

<h2 style="font-size: 34px; margin-top: 40px;">⚙️ Требования</h2>

<div style="font-size: 18px;  line-height: 1.6; font-family: monospace; margin-top: 20px;">

* **Java 21**
* **ClassGraph**
* **SnakeYAML**

</div>

<h2 style="font-size: 34px; margin-top: 40px;">🛡️ Принцип Fail-Fast</h2>
<div style="font-size: 18px;  line-height: 1.6; font-family: monospace; margin-top: 20px;">
Вместо того, чтобы прятать баги в рантайме или создавать нестабильные синглтоны, EasySpring кидает исключения и роняет клиентский код. 
Контекст безопасно падает во время старта, при этом генерируется понятное, структурированное сообщение об ошибке.<br/>

Пример:<br/>
Если в Spring'е напрямую внедрить прототайп в синглтон, то приложение успешно поднимется, но в рантайме работа будет 
идти только с 1й версией прототайп бина. Это баг. EasySpring защищает программиста от подобного рода ошибок.
</div>

```text
[EasySpring BeanDefinition configuration failure]: Scoped target problem detected
│
├──> Singleton scope type:
│     └──> com.example.app.services.ClassA
│      
└──> OneOff scope dependency:
      └──> com.example.app.services.ClassB
                    
Solution: We recommend to use provider pattern to inject OneOff bean in singleton.
```

<div style="font-size: 18px;  line-height: 1.6; font-family: monospace; margin-top: 20px;">
Пример:<br/>
Если разработчик пишит на Spring'е, как правило, в случае ошибки ему приходится дебажить код, поднимать и запускать проект, 
лаколизовать ошибку, работать с полотнами стектрейса. EasySpring выдает очень точные, сообщения об ошибках в псевдографике. 
Дебажить код не требуется.
</div>

```text
[EasySpring Topology sorter failure]: Circular dependency detected
│
└──> Circular chain of dependencies:
      └──> com.app.services.ClassC ──> com.app.services.ClassA ──> com.app.services.ClassB ──> com.app.services.ClassC

Solution: Resolve this architecture failure.
```

<h2 style="font-size: 34px; margin-top: 40px;">🔧 Аннотации</h2>
<div style="font-size: 18px;  line-height: 1.6; font-family: monospace; margin-top: 20px;">
@Managed - аналог @Component<br/>
@OneOff - аналог @Prototype<br/>
@ValueFrom - аналог @Value<br/>
@Init - аналог @PostConstruct<br/>
@Close - аналог @PreDestroy<br/>
</div>

<h2 style="font-size: 34px; margin-top: 40px;">🚀 Пример старта</h2>
<div style="font-size: 18px;  line-height: 1.6; font-family: monospace; margin-top: 20px;">
Фреймворк поддерживает аннатационную конфигурацию и НЕ поддеживает XML и Java конфигурации.
В остальном он очень похож на классический Spring.
</div>

### 1. Создание бина

```java
package com.example.app;

import com.github.codedissection.easyspring.annotation.ValueFrom;


@Managed
public class SomeServiceImpl implements SomeService {
    
    @ValueFrom("service.greeting.prefix")
    private String prefix;

    @Override
    public void sendMessage(String msg) {
        System.out.println(prefix + " 😎 " + msg);
    }
}
```

### 2. Старт приложения

```java
package com.example.app;

import com.github.codedissection.easyspring.EasySpringFacade;

public class App {
    public static void main(String[] args) {
        EasySpringFacade.run(App.class);
    }
}
```




<h2 style="font-size: 34px;  margin-top: 40px;">📄 Лицензия</h2>

Проект распространяется под лицензией **MIT** - для уточнений смотрите файл LICENCE.

---
Developed with 🧠 and 😎 by [code-dissection](https://github.com/code-dissection)
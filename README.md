CAM-5552 workaround
===================

This project implements a workaround for Camunda BPM bug [CAM-5552](https://app.camunda.com/jira/browse/CAM-5552).

It ensures that a process application's global execution listener is also invoked for the process instance start and end event. To integrate this acquisition into the engine, a process engine plugin must be registered.

How to use
----------

1. Build with `mvn clean install`
2. Make the resulting jar available on the process engine's classpath
3. Configure the process engine accordingly (see sections below)

### Shared Engine

In `bpm-platform.xml`, make sure to declare the process engine plugin as follows:

```
<?xml version="1.0" encoding="UTF-8"?>
<bpm-platform xmlns="http://www.camunda.org/schema/1.0/BpmPlatform"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.camunda.org/schema/1.0/BpmPlatform http://www.camunda.org/schema/1.0/BpmPlatform ">

  ...
  <process-engine name="default">
    ...

    <plugins>
      <!-- Plugin provided by this workaround project. Should be used in addition to org.camunda.bpm.application.impl.event.ProcessApplicationEventListenerPlugin -->
      <plugin>
        <class>org.camunda.bpm.workaround.Cam5552WorkaroundPlugin</class>
      </plugin>

      ...
    </plugins>

  </process-engine>

</bpm-platform>
```

This may require to externalize the `bpm-platform.xml` configuration files as described in the [Camunda documentation](https://docs.camunda.org/manual/7.4/reference/deployment-descriptors/descriptors/bpm-platform-xml/#configure-location-of-the-bpm-platform-xml-file).
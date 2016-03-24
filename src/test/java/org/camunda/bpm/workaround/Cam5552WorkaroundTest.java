/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.workaround;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.container.RuntimeContainerDelegate;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Thorben Lindhauer
 *
 */
public class Cam5552WorkaroundTest {

  @Rule
  public ProcessEngineRule engineRule = new ProcessEngineRule();

  protected TestProcessApplication testApplication;
  protected LogEventsExecutionListener listener;

  @Before
  public void setUp() {
    registerProcessEngine();
    deployProcessApplication();

  }

  @After
  public void tearDown() {
    undeployProcessApplication();
    unregisterProcessEngine();
  }


  public void registerProcessEngine() {
    RuntimeContainerDelegate runtimeContainerDelegate = RuntimeContainerDelegate.INSTANCE.get();
    runtimeContainerDelegate.registerProcessEngine(engineRule.getProcessEngine());
  }

  public void unregisterProcessEngine() {
    RuntimeContainerDelegate runtimeContainerDelegate = RuntimeContainerDelegate.INSTANCE.get();
    runtimeContainerDelegate.unregisterProcessEngine(engineRule.getProcessEngine());
  }

  public void deployProcessApplication() {
    // given
    testApplication = new TestProcessApplication();
    listener = new LogEventsExecutionListener();

    testApplication.setExecutionListener(listener);

    testApplication.deploy();
  }

  public void undeployProcessApplication() {
    testApplication.undeploy();
  }

  @Test
  public void testProcessLevelExecutionListenerInvocationOnStart() {

    // when
    engineRule.getRuntimeService()
      .createProcessInstanceByKey("process")
      .startBeforeActivity("task")
      .execute();

    // then
    Assert.assertEquals(1, listener.events.size());
    ExecutionListenerEvent event = listener.events.get(0);
    Assert.assertEquals(ExecutionListener.EVENTNAME_START, event.eventName);
    Assert.assertEquals("task", event.activityId);
  }

  @Test
  public void testProcessLevelExecutionListenerInvocationOnEnd() {
    // given
    engineRule.getRuntimeService()
      .createProcessInstanceByKey("process")
      .startBeforeActivity("task")
      .execute();

    Task task = engineRule.getTaskService().createTaskQuery().singleResult();

    listener.events.clear();

    // when
    engineRule.getTaskService().complete(task.getId());

    // then
    Assert.assertEquals(1, listener.events.size());
    ExecutionListenerEvent event = listener.events.get(0);
    Assert.assertEquals(ExecutionListener.EVENTNAME_END, event.eventName);
    Assert.assertEquals("theEnd", event.activityId);
  }

  public static class LogEventsExecutionListener implements ExecutionListener {
    protected List<ExecutionListenerEvent> events = new ArrayList<ExecutionListenerEvent>();

    public void notify(DelegateExecution execution) throws Exception {

      ExecutionListenerEvent event = new ExecutionListenerEvent();
      event.eventName = execution.getEventName();
      event.activityId = execution.getCurrentActivityId();

      events.add(event);
    }
  }

  public static class ExecutionListenerEvent {
    protected String eventName;
    protected String activityId;
  }
}

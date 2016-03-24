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

import org.camunda.bpm.application.impl.event.ProcessApplicationEventParseListener;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.util.xml.Element;

/**
 * @author Thorben Lindhauer
 *
 */
public class Cam5552WorkaroundProcessApplicationEventParseListener extends AbstractBpmnParseListener {

  @Override
  public void parseProcess(Element processElement, ProcessDefinitionEntity processDefinition) {
    processDefinition.addListener(ExecutionListener.EVENTNAME_START,
        ProcessApplicationEventParseListener.EXECUTION_LISTENER);
    processDefinition.addListener(ExecutionListener.EVENTNAME_END,
        ProcessApplicationEventParseListener.EXECUTION_LISTENER);
  }
}

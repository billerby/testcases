package testcases;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.test.AbstractFlowableTestCase;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.engine.test.FlowableTest;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FlowableTest
public class SignalParentProcessFromMultiinstanceSubExecutionTest extends AbstractFlowableTestCase {

    private Logger log = LoggerFactory.getLogger(SignalParentProcessFromMultiinstanceSubExecutionTest.class);

    private ProcessEngine processEngine;
    private HistoryService historyService;
    private RuntimeService runtimeService;
    private TaskService taskService;

    @BeforeEach
    void setUp(ProcessEngine processEngine) {
        this.processEngine = processEngine;
        this.historyService = processEngine.getHistoryService();
        this.runtimeService = processEngine.getRuntimeService();
        this.taskService = processEngine.getTaskService();
    }

    @Test
    @Deployment(resources = {
            "testcases/SignalParentProcessFromMultiinstanceSubExecution.bpmn20.xml"
    })
    void testSignaling() {

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("signal-parent-process-from-multiinstance-sub-execution");

        List<Execution> executions = runtimeService.createExecutionQuery().list();
        Date measurementDate = new Date();
        LocalDateTime localDateTime = LocalDateTime.from(measurementDate.toInstant().atZone(ZoneOffset.UTC)).plusDays(15);
        Date er = new Date(localDateTime.toEpochSecond(ZoneOffset.UTC));
        Date erik = new Date(measurementDate.getTime() + 6 * 60 * 60 * 1000); // 6 hours later
        for (Execution execution : executions){

            List<Execution> list = runtimeService.createExecutionQuery().executionId(execution.getId()).list();
            if (CollectionUtil.isEmpty(list)){
                continue;
            }
            Map<String, Object> variables = runtimeService.getVariables(execution.getId());
            log.info("variables in execution: {}: {}", execution.getId(), variables);
            String listValue = (String) variables.get("listValue");

            List<Task> tasks = taskService.createTaskQuery().executionId(execution.getId()).includeProcessVariables().list();
            for (Task task : tasks){
                if (StringUtils.equals("two", listValue)){
                    Map<String, Object> taskVars = new HashMap<>();
                    taskVars.put("newDate", true);
                    taskService.complete(task.getId(), taskVars);
                }
            }
        }

        List<Task> tasks = taskService.createTaskQuery().includeProcessVariables().list();



        log.info("tasks: {} {}", tasks != null? tasks.size() : "", tasks);

        log.info("executions:{} {}",executions != null ? executions.size() : "", executions);
        /*runtimeService.deleteProcessInstance(processInstance.getId(), null);

        List<HistoricProcessInstance> processes = historyService.createHistoricProcessInstanceQuery().list();

        for(HistoricProcessInstance process: processes) {
            Assertions.assertNotNull(process.getEndTime());
        }*/

    }

}

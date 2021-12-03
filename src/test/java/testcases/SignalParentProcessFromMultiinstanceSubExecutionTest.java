package testcases;

import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.engine.test.FlowableTest;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@FlowableTest
public class SignalParentProcessFromMultiinstanceSubExecutionTest {

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

        List<Task> tasks = taskService.createTaskQuery().list();
        log.info("tasks: {}", tasks);
        List<Execution> executions = runtimeService.createExecutionQuery().list();
        log.info("executions: {}", executions);

        /*runtimeService.deleteProcessInstance(processInstance.getId(), null);

        List<HistoricProcessInstance> processes = historyService.createHistoricProcessInstanceQuery().list();

        for(HistoricProcessInstance process: processes) {
            Assertions.assertNotNull(process.getEndTime());
        }*/

    }

}

package com.jyx.workflow;

import com.jyx.workflow.service.ActivityConsumerService;
import org.activiti.engine.*;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WorkflowApplicationTests {
	@Resource
	private ActivityConsumerService activityConsumerService;

    @Resource
    private ProcessEngine processEngine;            //流程引擎
    @Resource
    private RuntimeService runtimeService;          //运行时服务
    @Resource
    private TaskService taskService;                //任务服务
    @Resource
    private RepositoryService repositoryService;    //流程定义和部署服务
    @Resource
    private IdentityService identityService;        //用户和组的管理
    @Resource
    private ManagementService managementService;    //日常维护服务
    @Resource
    private HistoryService historyService;          //历史记录服务

	@Test
	public void startProcessInstance() {
		activityConsumerService.startProcessInstance("leave");
	}

    @Test
    public void delDeployment() {
        activityConsumerService.delDeployment("5005");
    }

    @Test
    public void queryProcessImage() {
        try {
            activityConsumerService.queryProcessImage("5005");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void queryLastVersionProcess() {
        activityConsumerService.queryLastVersionProcess("leave");
    }

    /**
     * 流程变量测试
     * */
    @Test
    public void processVariables() {
        //1.部署
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        Deployment deployment = deploymentBuilder
                .name("流程变量测试")
                //添加资源文件方式一
                .addClasspathResource("processes/processVariables.bpmn")
                .addClasspathResource("processes/processVariables.png")
                //添加资源文件方式二
                //以zip包的形式添加资源文件（必须为zip格式）
                //.addZipInputStream(new ZipInputStream(this.getClass().getClassLoader().getResourceAsStream(processFilePath)))
                .deploy();
        System.out.println("流程id："+ deployment.getId()+"   流程名称："+deployment.getName());
        //2.启动
        //流程启动
        //map用于设置流程变量
        Map<String,Object> map = new HashMap<String,Object>();
        ExecutionEntity pi1 = (ExecutionEntity) runtimeService.startProcessInstanceByKey("processVariables",map);

        //执行对象id
        String executionId = pi1.getId();

        System.out.println("executionId:"+executionId);
        //根据执行对象id及任务名称查询任务
        Task task = taskService.createTaskQuery().executionId(executionId).taskName("apply").singleResult();
        System.out.println("taskId:"+task.getId());
        //3.设置流程变量

        map.put("请假天数2",2);
        map.put("请假原因2","探亲2");
        taskService.setVariables(task.getId(),map);
        //taskService.setVariablesLocal(task.getId(),map);
        //4.获得流程变量
        Map<String, Object> variablesMap = taskService.getVariables(task.getId());
        for(Map.Entry<String, Object> entry : variablesMap.entrySet()) {
            System.out.println("================>"+entry.getKey()+":"+entry.getValue());
        }

        //5.完成任务
        taskService.complete(task.getId());

    }
    //完成任务
    @Test
    public void complateTask() {
//        Map<String,Object> map = new HashMap<String,Object>();
//        map.put("请假天数6",6);
//        map.put("请假原因6","探亲6");
        Map<String, Object> variablesMap = taskService.getVariables("5013");
        for(Map.Entry<String, Object> entry : variablesMap.entrySet()) {
            System.out.println("1================>"+entry.getKey()+":"+entry.getValue());
        }
        taskService.complete("5013");

        Map<String, Object> variablesMap2 = taskService.getVariables("5013");
        for(Map.Entry<String, Object> entry : variablesMap2.entrySet()) {
            System.out.println("2================>"+entry.getKey()+":"+entry.getValue());
        }
    }

    //根据流程变量的key查询正在执行的任务
    @Test
    public void queryTasksByProcessKey() {
//        List<Task> tasks = taskService.createTaskQuery().
//                processDefinitionKey("processVariables")
//                .taskCandidateOrAssigned("lisi")
//                .list();
//        for (Task task : tasks) {
//            System.out.println(task.getId()+":"+task.getName());
//        }
       Map<String,Object> map = taskService.getVariables("60009");

        for (Map.Entry<String,Object> entry : map.entrySet()) {
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }


}

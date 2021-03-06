package io.projectmanagementplatform.pmt.services;

import io.projectmanagementplatform.pmt.Exceptions.ProjectIdException;
import io.projectmanagementplatform.pmt.Exceptions.ProjectNotFoundException;
import io.projectmanagementplatform.pmt.domain.Backlog;
import io.projectmanagementplatform.pmt.domain.Project;
import io.projectmanagementplatform.pmt.domain.ProjectTask;
import io.projectmanagementplatform.pmt.repositories.BacklogRepository;
import io.projectmanagementplatform.pmt.repositories.ProjectRepository;
import io.projectmanagementplatform.pmt.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username) {


            //ProjectTasks to be added to specific project, project != null, BackLog means exist
            Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog(); //backlogRepository.findByProjectIdentifier(projectIdentifier);
            //set the backlog to ProjectTask

            projectTask.setBacklog(backlog);
            //Project sequence will show a like IDPRO-1, IDPRO-2
            Integer BacklogSequence = backlog.getPTSequence();
            //Update BackLog Sequence
            BacklogSequence++;
            backlog.setPTSequence(BacklogSequence);

            //Add sequence to projectTask
            projectTask.setProjectSequence(projectIdentifier + "-" + BacklogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            //Initial Priorty when priority is null

            //Initial status when status is null

            if (projectTask.getStatus() == "" || projectTask.getStatus() == null) {
                projectTask.setStatus("TO_DO");
            }

                if(projectTask.getPriority()==null || projectTask.getPriority()==0 ){ //In the future we need projectTask.getPriority()== 0 to handle the form
                projectTask.setPriority(3);
            }

            return projectTaskRepository.save(projectTask);

    }
    public Iterable<ProjectTask>findBacklogById(String id, String username){

        //Project project = projectRepository.findByProjectIdentifier(id);
        projectService.findProjectByIdentifier(id, username);

//        if(project == null){
  //          throw new ProjectNotFoundException("Project With ID: '"+id+"' does not exist ");
    //    }

        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findProjectTaskSequence(String backlog_id, String pt_id, String username){

        //has to be sure we are searching ot he right backlog
       // Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
       // if(backlog==null){
        //    throw new ProjectNotFoundException("Project with ID: '"+backlog_id+"' does not exist ");
        //}

        //make sure we are searching on an existin backlog
        projectService.findProjectByIdentifier(backlog_id, username);


        //make sure task is exist
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if(projectTask == null){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' not found");
        }

        //make sure that the backlog/project id in the path corresponds to the right project
        if (!projectTask.getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' does not exist in project: '"+backlog_id);
        }

        return projectTaskRepository.findByProjectSequence(pt_id);
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog, String pt_id, String username){
        ProjectTask projectTask = findProjectTaskSequence(backlog, pt_id, username);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);

    }

    public void deleteProjectTaskByProjectSequence(String backlog, String pt_id, String username){
        ProjectTask projectTask = findProjectTaskSequence(backlog, pt_id, username);
        projectTaskRepository.delete(projectTask);
    }
}

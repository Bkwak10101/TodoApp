package com.github.bkwak.todoapp.logic;

import com.github.bkwak.todoapp.TaskConfigurationProperties;
import com.github.bkwak.todoapp.model.ProjectRepository;
import com.github.bkwak.todoapp.model.TaskGroup;
import com.github.bkwak.todoapp.model.TaskGroupRepository;
import com.github.bkwak.todoapp.model.Project;
import com.github.bkwak.todoapp.model.Task;
import com.github.bkwak.todoapp.model.projection.GroupReadModel;
import com.github.bkwak.todoapp.model.projection.GroupTaskWriteModel;
import com.github.bkwak.todoapp.model.projection.GroupWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {
    private ProjectRepository repository;
    private TaskGroupRepository taskGroupRepository;
    private TaskGroupService taskGroupService;
    private TaskConfigurationProperties config;

    public ProjectService(ProjectRepository repository, TaskGroupRepository taskGroupRepository, TaskGroupService taskGroupService, TaskConfigurationProperties config) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }

    public List<Project> readAll() {
        return repository.findAll();
    }

    public Project save(final Project toSave) {
        return repository.save(toSave);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId) {
        if (config.getTemplate().isAllowMultipleTasks() && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException(("Only one undone group from project is allowed"));
        }
        return repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(project.getSteps().stream()
                            .map(projectStep -> {
                                var task = new GroupTaskWriteModel();
                                task.setDescription((projectStep.getDescription()));
                                task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                return task;
                            })
                            .collect(Collectors.toSet())
                    );
                    return taskGroupService.createGroup(targetGroup);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
    }
}

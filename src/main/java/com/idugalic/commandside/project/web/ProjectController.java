package com.idugalic.commandside.project.web;

import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import com.idugalic.commandside.project.command.ActivateProjectCommand;
import com.idugalic.commandside.project.command.DeactivateProjectCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.idugalic.commandside.project.command.CreateProjectCommand;
import com.idugalic.commandside.project.command.UpdateProjectCommand;
import com.idugalic.common.model.AuditEntry;

/**
 * A web controller for managing {@link ProjectAggregate} - create/update only.
 * 
 * @author idugalic
 *
 */
@RestController
@RequestMapping(value = "/api/projectcommands")
public class ProjectController {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);
    
    private CommandGateway commandGateway;
    
    @Autowired
    public ProjectController(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	private String getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return null;
    }

    private AuditEntry createAudit() {
        return new AuditEntry(getCurrentUser());
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public void create(@RequestBody CreateProjectRequest request, HttpServletResponse response, Principal principal) {
        LOG.debug(CreateProjectRequest.class.getSimpleName() + " request received");
        CreateProjectCommand command = new CreateProjectCommand(createAudit(), request.getName(), request.getRepoUrl(), request.getSiteUrl(), request.getCategory(), request
                .getDescription());
        commandGateway.sendAndWait(command);
        LOG.debug(CreateProjectCommand.class.getSimpleName() + " sent to command gateway: Project [{}] ", command.getId());
    }

    @RequestMapping(value = "/{id}/updatecommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void update(@PathVariable String id, @RequestBody UpdateProjectRequest request, HttpServletResponse response, Principal principal) {
        System.out.println("################################ URAAA");
        LOG.debug(UpdateProjectRequest.class.getSimpleName() + " request received");
        UpdateProjectCommand command = new UpdateProjectCommand(id, createAudit(), request.getName(), request.getRepoUrl(), request.getSiteUrl(), request.getDescription());
        commandGateway.sendAndWait(command);
        LOG.debug(UpdateProjectCommand.class.getSimpleName() + " sent to command gateway: Project [{}] ", command.getId());
    }

    @RequestMapping(value = "/{id}/activatecommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void activate(@PathVariable String id, HttpServletResponse response, Principal principal) {
        ActivateProjectCommand command = new ActivateProjectCommand(id, createAudit());
        commandGateway.sendAndWait(command);
        LOG.debug(ActivateProjectCommand.class.getSimpleName() + " sent to command gateway: Project [{}] ", command.getId());
    }

    @RequestMapping(value = "/{id}/deactivatecommand", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public void deactivate(@PathVariable String id, HttpServletResponse response, Principal principal) {
        DeactivateProjectCommand command = new DeactivateProjectCommand(id, createAudit());
        commandGateway.sendAndWait(command);
        LOG.debug(DeactivateProjectCommand.class.getSimpleName() + " sent to command gateway: Project [{}] ", command.getId());
    }

}

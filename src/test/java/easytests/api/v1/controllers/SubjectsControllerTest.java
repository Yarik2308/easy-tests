package easytests.api.v1.controllers;

import easytests.api.v1.mappers.SubjectsMapper;
import easytests.api.v1.models.Subject;
import easytests.auth.services.AccessControlLayerServiceInterface;
import easytests.config.SwaggerRequestValidationConfig;
import easytests.core.models.*;
import easytests.core.models.empty.UserModelEmpty;
import easytests.core.options.builder.SubjectsOptionsBuilder;
import easytests.core.services.SubjectsServiceInterface;
import easytests.core.options.SubjectsOptions;
import easytests.core.options.SubjectsOptionsInterface;
import easytests.core.services.UsersServiceInterface;
import easytests.support.SubjectsSupport;
import easytests.support.UsersSupport;
import easytests.support.JsonSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.BDDMockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author VeronikaRevjakina
 */
@Import({SubjectsMapper.class, SwaggerRequestValidationConfig.class})
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SubjectsController.class, secure = false)
public class SubjectsControllerTest {
    private static final String id = "id";
    private static final String name = "name";
    private static final String description = "description";
    private static final String user = "user";
    private static final String userIdParamValue = "2";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SubjectsServiceInterface subjectsService;

    @MockBean
    private UsersServiceInterface usersService;


    @Autowired
    @Qualifier("SubjectsMapperV1")
    private SubjectsMapper subjectsMapper;

    @MockBean
    private AccessControlLayerServiceInterface acl;

    @MockBean
    SubjectsOptionsBuilder subjectsOptionsBuilder;

    private SubjectsSupport subjectsSupport = new SubjectsSupport();

    private UsersSupport usersSupport = new UsersSupport();

    @Test
    public void testListSuccess() throws Exception {
        final List<SubjectModelInterface> subjectsModels = new ArrayList<>();
        IntStream.range(0, 2).forEach(idx -> {
            final SubjectModel subjectModel = new SubjectModel();
            subjectModel.map(this.subjectsSupport.getEntityFixtureMock(idx));
            subjectsModels.add(subjectModel);
        });


        int userIdParamValue = 1;

        when(this.usersService.find(userIdParamValue))
                .thenReturn(new UserModelEmpty(userIdParamValue));
        when(this.subjectsService.findByUser(new UserModelEmpty(userIdParamValue)))
                .thenReturn(subjectsModels);
        when(this.acl.hasAccess(any(UserModelInterface.class))).thenReturn(true);

        mvc.perform(get("/v1/subjects?userId={userIdParamValue}", userIdParamValue)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new JsonSupport()
                        .with(new JsonSupport()
                                .with(id, subjectsModels.get(0).getId())
                                .with(name, subjectsModels.get(0).getName())
                                .with(description,subjectsModels.get(0).getDescription())
                                .with(user, new JsonSupport().with(id, subjectsModels.get(0).getUser().getId())))
                        .with(new JsonSupport()
                                .with(id, subjectsModels.get(1).getId())
                                .with(name, subjectsModels.get(1).getName())
                                .with(description,subjectsModels.get(1).getDescription())
                                .with(user, new JsonSupport().with(id, subjectsModels.get(1).getUser().getId())))
                        .build()
                ))
                .andReturn();

    }

    @Test
    public void testListNotFound() throws Exception {
        int userIdParamValue = 5;

        this.mvc.perform(get("/v1/subjects?userId={userIdParamValue}", userIdParamValue)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
                .andReturn();
    }

    @Test
    public void testListForbidden() throws Exception {
        int userIdParamValue = 1;

        when(this.usersService.find(userIdParamValue))
                .thenReturn(new UserModelEmpty(userIdParamValue));
        when(this.acl.hasAccess(any(UserModelInterface.class))).thenReturn(false);

        this.mvc.perform(get("/v1/subjects?userId={userIdParamValue}", userIdParamValue)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isForbidden())
                .andExpect(content().string(""))
                .andReturn();

    }

    @Test
    public void testCreateSuccess() throws Exception {
        doAnswer(invocation -> {
            final SubjectModel subjectModel = (SubjectModel) invocation.getArguments()[0];
            subjectModel.setId(5);
            final SubjectEntity subjectCheckEntity = this.subjectsSupport.getEntityAdditionalMock(0);
            final SubjectModel subjectCheckModel = new SubjectModel();
            subjectCheckModel.map(subjectCheckEntity);
            subjectCheckModel.setId(5);
            int userIdParamValue =2;
            subjectModel.setUser(new UserModelEmpty(userIdParamValue));
            this.subjectsSupport.assertEquals(subjectModel, subjectCheckModel);
            return null;
        }).when(this.subjectsService).save(any(SubjectModelInterface.class));

        final UserModelInterface userModel = this.usersSupport.getModelFixtureMock(1);
        when(this.usersService.find(any(Integer.class))).thenReturn(userModel);
        when(this.acl.hasAccess(any(UserModelInterface.class))).thenReturn(true);
        final ArgumentCaptor<SubjectModelInterface> subjectCaptor = ArgumentCaptor.forClass(SubjectModelInterface.class);

        mvc.perform(post("/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JsonSupport()
                        .with(name, "Subject")
                        .with(description, "Subject description")
                        .with(user, new JsonSupport().with(id,2))
                        .build()
                ))
                .andExpect(status().is(201))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(
                        new JsonSupport()
                                .with(id, 5)
                                .build()
                ))
                .andReturn();
        verify(this.subjectsService, times(1)).save(subjectCaptor.capture());
        Assert.assertEquals(subjectCaptor.getValue().getName(), "Subject");
        Assert.assertEquals(subjectCaptor.getValue().getDescription(), "Subject description");
        Assert.assertEquals(subjectCaptor.getValue().getUser().getId(), (Integer) 2);
    }

    @Test
    public void testCreateWithIdFailed() throws Exception {
        mvc.perform(post("/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JsonSupport()
                        .with(id, 2)
                        .with(name, "Name1")
                        .with(description, "Description1")
                        .with(user, new JsonSupport().with(2))
                        .build()
                ))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""))
                .andReturn();
    }

    @Test
    public void testCheckUserBadRequest() throws Exception {
        Subject subject = new Subject();
        mvc.perform(post("/v1/subjects").param("subject", "subject"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testCreateForbidden() throws Exception {
        when(this.usersService.find(2)).thenReturn(new UserModelEmpty(2));
        when(this.acl.hasAccess(any(UserModelInterface.class))).thenReturn(false);

        mvc.perform(post("/v1/subjects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new JsonSupport()
                        .with(name, "Subject1")
                        .with(description, "Subject description")
                        .with(user, new JsonSupport().with(id, 2))
                        .build()
                ))
                .andExpect(status().isForbidden()
                )
                .andReturn();
    }
    /**
     * update
     */
    @Test
    public void testShowSuccess() throws Exception {
        final SubjectModelInterface subjectModel = new SubjectModel();
        subjectModel.map(this.subjectsSupport.getEntityFixtureMock(0));
        when(this.subjectsOptionsBuilder.forAuth()).thenReturn(new SubjectsOptions());
        when(this.subjectsService.find(any(Integer.class), any(SubjectsOptionsInterface.class))).thenReturn(subjectModel);
        when(this.acl.hasAccess(any(SubjectModelInterface.class))).thenReturn(true);

        mvc.perform(get("/v1/subjects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json(new JsonSupport()
                        .with(id, subjectModel.getId())
                        .with(name, subjectModel.getName())
                        .with(description, subjectModel.getDescription())
                        .with(user, new JsonSupport().with(id, subjectModel.getUser().getId()))
                        .build()
                ))
                .andReturn();
    }

    @Test
    public void testShowNotFound() throws Exception {
        when(this.subjectsOptionsBuilder.forAuth()).thenReturn(new SubjectsOptions());
        when(this.subjectsService.find(any(Integer.class), any(SubjectsOptionsInterface.class))).thenReturn(null);

        mvc.perform(get("/v1/subjects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""))
                .andReturn();
    }

    @Test
    public void testShowForbidden() throws Exception {
        final SubjectModelInterface subjectModel = new SubjectModel();
        subjectModel.map(this.subjectsSupport.getEntityFixtureMock(0));
        when(this.subjectsOptionsBuilder.forAuth()).thenReturn(new SubjectsOptions());
        when(this.subjectsService.find(any(Integer.class), any(SubjectsOptionsInterface.class))).thenReturn(subjectModel);
        when(this.acl.hasAccess(any(SubjectModelInterface.class))).thenReturn(false);

        mvc.perform(get("/v1/subjects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().string(""))
                .andReturn();
    }
    /**
     * delete(subjectId)
     */
    
}

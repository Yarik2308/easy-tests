package easytests.services;

import easytests.entities.*;
import easytests.mappers.IssueStandardsMapper;
import easytests.models.*;
import easytests.models.empty.ModelsListEmpty;
import easytests.models.empty.SubjectModelEmpty;
import easytests.options.IssueStandardsOptionsInterface;
import easytests.services.exceptions.DeleteUnidentifiedModelException;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit4.*;

import static org.mockito.BDDMockito.*;

/**
 * @author SingularityA
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IssueStandardsServiceTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    private IssueStandardsMapper issueStandardsMapper;

    @InjectMocks
    private IssueStandardsService issueStandardsService;

    private IssueStandardEntity
        createIssueStandardEntityMock(Integer id, Integer timeLimit, Integer questionsNumber, Integer subjectId) {

        final IssueStandardEntity issueStandardEntity = Mockito.mock(IssueStandardEntity.class);
        Mockito.when(issueStandardEntity.getId()).thenReturn(id);
        Mockito.when(issueStandardEntity.getTimeLimit()).thenReturn(timeLimit);
        Mockito.when(issueStandardEntity.getQuestionsNumber()).thenReturn(questionsNumber);
        Mockito.when(issueStandardEntity.getSubjectId()).thenReturn(subjectId);
        return issueStandardEntity;
    }

    private IssueStandardModelInterface
        createIssueStandardModel(Integer id, Integer timeLimit, Integer questionsNumber, Integer subjectId) {

        final IssueStandardModelInterface issueStandardModel = new IssueStandardModel();
        issueStandardModel.setId(id);
        issueStandardModel.setTimeLimit(timeLimit);
        issueStandardModel.setQuestionsNumber(questionsNumber);
        issueStandardModel.setTopicPriorities(new ModelsListEmpty());
        issueStandardModel.setQuestionTypeOptions(new ModelsListEmpty());
        issueStandardModel.setSubject(new SubjectModelEmpty(subjectId));
        return issueStandardModel;
    }

    private IssueStandardEntity mapIssueStandardEntity(IssueStandardModelInterface issueStandardModel) {
        final IssueStandardEntity issueStandardEntity = new IssueStandardEntity();
        issueStandardEntity.map(issueStandardModel);
        return issueStandardEntity;
    }

    private IssueStandardModelInterface mapIssueStandardModel(IssueStandardEntity issueStandardEntity) {
        final IssueStandardModelInterface issueStandardModel = new IssueStandardModel();
        issueStandardModel.map(issueStandardEntity);
        return issueStandardModel;
    }

    private List<IssueStandardEntity> getIssueStandardEntities() {
        final List<IssueStandardEntity> issueStandardEntities = new ArrayList<>(2);
        issueStandardEntities.add(this.createIssueStandardEntityMock(1, 600, 10, 1));
        issueStandardEntities.add(this.createIssueStandardEntityMock(2, 1200, 20, 2));
        return issueStandardEntities;
    }

    private List<IssueStandardModelInterface> getIssueStandardModels() {
        final List<IssueStandardModelInterface> issueStandardModels = new ArrayList<>(2);
        for (IssueStandardEntity issueStandardEntity: this.getIssueStandardEntities()) {
            issueStandardModels.add(this.mapIssueStandardModel(issueStandardEntity));
        }
        return issueStandardModels;
    }

    @Test
    public void testFindAllPresentList() throws Exception {
        final List<IssueStandardEntity> issueStandardEntities = this.getIssueStandardEntities();
        given(this.issueStandardsMapper.findAll()).willReturn(issueStandardEntities);

        final List<IssueStandardModelInterface> issueStandardModels = this.issueStandardsService.findAll();

        Assert.assertNotNull(issueStandardModels);
        Assert.assertEquals(this.getIssueStandardModels(), issueStandardModels);
    }

    @Test
    public void testFindAllAbsentList() throws Exception {
        given(this.issueStandardsMapper.findAll()).willReturn(new ArrayList<>(0));

        final List<IssueStandardModelInterface> issueStandardModels = this.issueStandardsService.findAll();

        Assert.assertNotNull(issueStandardModels);
        Assert.assertEquals(0, issueStandardModels.size());
    }

    @Test
    public void testFindAllWithOptions() throws Exception {
        final List<IssueStandardEntity> issueStandardEntities = this.getIssueStandardEntities();
        final List<IssueStandardModelInterface> issueStandardModels = this.getIssueStandardModels();

        final IssueStandardsOptionsInterface issueStandardsOptions = Mockito.mock(IssueStandardsOptionsInterface.class);
        given(this.issueStandardsMapper.findAll()).willReturn(issueStandardEntities);
        given(issueStandardsOptions.withRelations(Mockito.anyList())).willReturn(issueStandardModels);

        List<IssueStandardModelInterface> foundedIssueStandardModels
                = this.issueStandardsService.findAll(issueStandardsOptions);

        verify(issueStandardsOptions).withRelations(issueStandardModels);
        Assert.assertNotNull(foundedIssueStandardModels);
        Assert.assertEquals(issueStandardModels, foundedIssueStandardModels);
    }

    @Test
    public void testFindPresentModel() throws Exception {
        final Integer id = 1;
        final IssueStandardEntity issueStandardEntity = this.createIssueStandardEntityMock(id, 1200, 20, 2);
        given(this.issueStandardsMapper.find(id)).willReturn(issueStandardEntity);

        final IssueStandardModelInterface issueStandardModel = this.issueStandardsService.find(id);
        Assert.assertNotNull(issueStandardModel);
        Assert.assertEquals(this.mapIssueStandardModel(issueStandardEntity), issueStandardModel);
    }

    @Test
    public void testFindAbsentModel() throws Exception {
        final Integer id = 10;
        given(this.issueStandardsMapper.find(id)).willReturn(null);

        final IssueStandardModelInterface issueStandardModel = this.issueStandardsService.find(id);
        Assert.assertNull(issueStandardModel);
    }

    @Test
    public void testFindWithOptions() throws Exception {
        final Integer id = 1;
        final IssueStandardEntity issueStandardEntity = this.createIssueStandardEntityMock(id, 1200, 20, 2);
        final IssueStandardModelInterface issueStandardModel = this.mapIssueStandardModel(issueStandardEntity);

        final IssueStandardsOptionsInterface issueStandardsOptions = Mockito.mock(IssueStandardsOptionsInterface.class);
        given(this.issueStandardsMapper.find(id)).willReturn(issueStandardEntity);
        given(issueStandardsOptions.withRelations(issueStandardModel)).willReturn(issueStandardModel);

        final IssueStandardModelInterface foundedIssueStandardModel
                = this.issueStandardsService.find(id, issueStandardsOptions);

        verify(issueStandardsOptions).withRelations(issueStandardModel);
        Assert.assertNotNull(foundedIssueStandardModel);
        Assert.assertEquals(issueStandardModel, foundedIssueStandardModel);
    }

    @Test
    public void testFindBySubjectPresentModel() throws Exception {
        final Integer subjectId = 3;
        final IssueStandardEntity issueStandardEntity = this.createIssueStandardEntityMock(3, 600, 10, subjectId);
        given(this.issueStandardsMapper.findBySubjectId(subjectId)).willReturn(issueStandardEntity);

        final SubjectModelInterface subjectModel = Mockito.mock(SubjectModelInterface.class);
        Mockito.when(subjectModel.getId()).thenReturn(subjectId);

        final IssueStandardModelInterface issueStandardModel = this.issueStandardsService.findBySubject(subjectModel);
        Assert.assertNotNull(issueStandardModel);
        Assert.assertEquals(this.mapIssueStandardModel(issueStandardEntity), issueStandardModel);
    }

    @Test
    public void testFindBySubjectAbsentModel() throws Exception {
        final Integer subjectId = 5;
        given(this.issueStandardsMapper.findBySubjectId(subjectId)).willReturn(null);

        final SubjectModelInterface subjectModel = Mockito.mock(SubjectModelInterface.class);
        Mockito.when(subjectModel.getId()).thenReturn(subjectId);

        final IssueStandardModelInterface issueStandardModel = this.issueStandardsService.findBySubject(subjectModel);
        Assert.assertNull(issueStandardModel);
    }

    @Test
    public void testFindBySubjectOptions() throws Exception {
        final Integer subjectId = 3;
        final IssueStandardEntity issueStandardEntity = this.createIssueStandardEntityMock(3, 600, 10, subjectId);
        final IssueStandardModelInterface issueStandardModel = this.mapIssueStandardModel(issueStandardEntity);

        final IssueStandardsOptionsInterface issueStandardsOptions = Mockito.mock(IssueStandardsOptionsInterface.class);
        given(this.issueStandardsMapper.findBySubjectId(subjectId)).willReturn(issueStandardEntity);
        given(issueStandardsOptions.withRelations(issueStandardModel)).willReturn(issueStandardModel);

        final IssueStandardModelInterface foundedIssueStandardModel
                = this.issueStandardsService.findBySubject(issueStandardModel.getSubject(), issueStandardsOptions);

        verify(issueStandardsOptions).withRelations(issueStandardModel);
        Assert.assertNotNull(foundedIssueStandardModel);
        Assert.assertEquals(issueStandardModel, foundedIssueStandardModel);
    }

    @Test
    public void testSaveUpdatesEntity() throws Exception {
        final IssueStandardModelInterface issueStandardModel = this.createIssueStandardModel(1, 600, 10, 1);

        this.issueStandardsService.save(issueStandardModel);

        verify(this.issueStandardsMapper, times(1)).update(this.mapIssueStandardEntity(issueStandardModel));
    }

    @Test
    public void testSaveInsertsEntity() throws Exception {
        final IssueStandardModelInterface issueStandardModel = this.createIssueStandardModel(null, 1200, 20, 5);
        final Integer id = 10;

        doAnswer(invocations -> {
            final IssueStandardEntity issueStandardEntity = (IssueStandardEntity) invocations.getArguments()[0];
            issueStandardEntity.setId(id);
            return null;
        }).when(this.issueStandardsMapper).insert(Mockito.any(IssueStandardEntity.class));

        this.issueStandardsService.save(issueStandardModel);

        verify(this.issueStandardsMapper, times(1)).insert(this.mapIssueStandardEntity(issueStandardModel));
        Assert.assertEquals(id, issueStandardModel.getId());
    }

    @Test
    public void testSaveWithOptions() throws Exception {
        final IssueStandardModelInterface issueStandardModel = this.createIssueStandardModel(null, 1200, 20, 5);
        final IssueStandardsOptionsInterface issueStandardsOptions = Mockito.mock(IssueStandardsOptionsInterface.class);

        this.issueStandardsService.save(issueStandardModel, issueStandardsOptions);

        verify(issueStandardsOptions).saveWithRelations(issueStandardModel);
    }

    @Test
    public void testDeleteIdentifiedModel() throws Exception {
        final IssueStandardModelInterface issueStandardModel = this.createIssueStandardModel(1, 600, 10, 2);

        this.issueStandardsService.delete(issueStandardModel);

        verify(issueStandardsMapper, times(1)).delete(this.mapIssueStandardEntity(issueStandardModel));
    }

    @Test
    public void testDeleteUnidentifiedModel() throws Exception {
        final IssueStandardModelInterface issueStandardModel = this.createIssueStandardModel(null, 600, 10, 1);

        exception.expect(DeleteUnidentifiedModelException.class);
        this.issueStandardsService.delete(issueStandardModel);
    }

    @Test
    public void testDeleteWithOptions() throws Exception {
        final IssueStandardModelInterface issueStandardModel = this.createIssueStandardModel(1, 1200, 20, 5);
        final IssueStandardsOptionsInterface issueStandardsOptions = Mockito.mock(IssueStandardsOptionsInterface.class);

        this.issueStandardsService.delete(issueStandardModel, issueStandardsOptions);

        verify(issueStandardsOptions).deleteWithRelations(issueStandardModel);
    }
}
